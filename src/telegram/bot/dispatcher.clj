(ns telegram.bot.dispatcher
  (:require [clojure.string :as str]
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
  (when-let [send (:send-text actions)]
    (api/send-message (:bot ctx) (:chat-id send) (:text send)))
  
  (when-let [reply (:reply-text actions)]
    (api/send-message (:bot ctx) (-> #p upd :message :chat :id) (:text reply))))

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

  (def handlers
    {:command {"/start" (fn [upd ctx]
                          {:reply-text {:text "Start command"}})

               #"/foo_\d+" (r/reply-text "Command /foo_NUM")

               :default (r/reply-text "Unknown command")
               }

     :text {:default (fn [upd ctx]
                       {:reply-text {:text (u/message-text? upd)}})}})

  (make-predicates (:command handlers))
  (make-predicates (:text handlers)))


(defn- make-predicates
  "Take a handlers map and produce predicate maps for each type of handlers."
  [handlers]
  (for [h handlers]
    {:pred (cond
             #p (= #p (first h) :default) (constantly true)
             (= (type (first h)) java.util.regex.Pattern) #(re-matches (first h) %)
             (= (type (first h)) java.lang.String) #(= (first h) %)
             (fn? (first h)) (first h)
             (keyword? (first h)) (throw (ex-info "Only :default key allowed as handler key" {:key (first h)}))
             :else (throw (ex-info "No command handlers provided" {:handlers handlers}))
             )
     :handler (cond
                (map? (last h)) (fn [upd ctx] (last h))
                (fn? (last h)) (last h)
                :else (throw (ex-info "Handler can either be an action map or a function." {:handler (last h)})))}))

;; TODO: add middleware

(defn make-dispatcher [ctx handlers & {:keys [middleware]}]
  (let [dispatcher
        (fn [upd]
          (let [actions (dispatch upd ctx)]
            (process-actions upd (assoc ctx :db @(:db ctx)) actions)))]
    dispatcher))
