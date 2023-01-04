(ns telegram.bot.dispatcher
  (:require [clojure.string :as str]
            [malli.core :as m]
            [malli.error :as me]
            [telegram.schema :as ts]
            [telegram.updates :as u]
            [telegram.responses :as r]
            [telegram.api.core :as api]))


(defmulti command (fn [upd _] (u/command? upd)))

(defmethod command :default [upd ctx])

(defmulti text (fn [upd _] (u/message-text? upd)))

(defmethod text :default [upd ctx])

(defmulti dispatch (fn [upd _] (u/update-type upd)))

(defmethod dispatch :command [upd ctx]
  (command upd ctx))

(defmethod dispatch :text [upd ctx]
  (text upd ctx))

(defn process-actions [upd ctx actions]
  (when-let [send (:send-text  actions)]
    (api/send-message (:bot ctx) (:chat-id send) (:text send)))
  
  (when-let [reply (:reply-text actions)]
    (api/send-message (:bot ctx) (-> upd u/from :id) (:text reply))))

(comment
  ;; Handler map is composed of keys depending on the type of update to process.
  ;; For convenience we separate :command from :text, although if you wanted you could handle
  ;; all commands using :text handlers and regex.
  ;;
  ;; Following types of handlers are supported:
  ;; - :command
  ;; - :text
  ;; - :callback-query
  ;; - :inline-query
  ;; - :conversation
  ;; - :any
  ;;
  ;; Conversation is a special type of handler. Once user initiates a conversation, all handlers except the handlers
  ;; in the conversation will be ignored. To exit this state user either needs to complete the conversation (a handler
  ;; must return {:conversation :end} or you need to provide a fallback handler within the conversation map.
  ;;
  ;; See example user registration to understand it better.
  (def sample-command-upd
    {:update-id 328539074,
     :message
     {:message-id 6434,
      :from
      {:id 22039771,
       :is-bot false,
       :first-name "Mikhail",
       :last-name "Beliansky",
       :username "beliansky",
       :language-code "en",
       :is-premium true},
      :chat
      {:id 22039771,
       :first-name "Mikhail",
       :last-name "Beliansky",
       :username "beliansky",
       :type "private"},
      :date 1664193098,
      :text "/start",
      :entities [{:offset 0, :length 8, :type "bot_command"}]}})

  (def handlers
    [{:type :command
      :doc "Handles start command"
      :filter #"/start"
      :actions [{:reply-text {:text "/start command"}}]}

     {:type :text
      :filter "ping"
      :actions [{:reply-text {:text "pong"}}]}

     {:type :command
      :doc "Get user profile by id."
      :filter #"/user_(\d+)"
      :actions [(fn [upd ctx] (println "This is a function"))]}])

  (match-handlers handlers sample-command-upd {})
  )

(defn- match-regex-filter
  "Take a regex filter and match it with update text."
  [handler upd _]
  (re-matches (:filter handler) (u/message-text? upd)))

(defn- match-string-filter
  "Take a string filter and match it with update text."
  [handler upd _]
  (= (:filter handler) (u/message-text? upd)))

(defn- match-filter [handler upd _]
  (let [filt (:filter handler)]
    (cond
      (= :any filt) true
      (= (type filt) java.util.regex.Pattern) (match-regex-filter handler upd nil)
      (string? filt) (match-string-filter handler upd nil)
      (fn? filt) (filt upd _))))

(defn- user-auth?
  "Checks if handler has `:user` key and check it's conditions if it does. If condition is true or there is no `:user` key in handler return the original handler."
  [handler upd ctx]
  (if-let [user-cond (:user handler)]
    (cond
      (keyword? user-cond) (when (-> upd :ctb/user user-cond) handler)
      (fn? user-cond) (when (user-cond (:ctb/user upd)) handler)
      :else (throw (ex-info "Unknown :user condition" {:user user-cond})))
    handler))

(defn- match-handlers
  "Take a list of handlers and match the update."
  [handlers upd ctx]
  (let [update-type (u/update-type upd)
        by-type (filter #(= (:type %) update-type) handlers)
        by-filter (filter #(match-filter % upd ctx) by-type)]
    (filter #(user-auth? % upd ctx) by-filter)))

(defn- process-action [action upd ctx]
  (cond
    (ifn? action) (action upd ctx)
    (:send-text action) action
    (:reply-text action) {:send-text (merge {:chat-id (-> upd u/from :id)} (:reply-text action))}
    :else (throw (ex-info "Don't know how to process action" {:action action}))))

(defn- process-handlers
  ([handlers upd ctx]
   (process-handlers handlers upd ctx []))
  ([handlers upd ctx res]
   (let [matched-handlers (match-handlers handlers upd ctx)
         first-handler (first matched-handlers)
         no-more-handlers? (empty? (rest handlers))
         no-passthrough? (not (:passthrough first-handler))
         result (for [a (:actions first-handler)]
                  (process-action a upd ctx))]
     (if (or no-more-handlers? no-passthrough?)
       (flatten (concat res result))
       (recur (rest matched-handlers) upd ctx (concat res result))))))

(comment
  (process-handlers handlers sample-command-upd {}))

(defn make-dispatcher [ctx handlers & {:keys [update-middleware]}]
  (when-let [errors (ts/explain-humanized ts/schema:handlers handlers)]
    (throw (ex-info "Handlers do not conform to schema" {:errors errors})))
  (let [dispatcher
        (fn [upd]
          (let [update-after-mw ((apply comp update-middleware) upd)
                actions (process-handlers handlers update-after-mw ctx)]
            (mapv #(process-actions upd (assoc ctx :db @(:db ctx)) %) actions)))]
    dispatcher))
