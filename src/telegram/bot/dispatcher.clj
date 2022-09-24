(ns telegram.bot.dispatcher
  (:require [clojure.string :as str]
            [telegram.updates :as u]
            [telegram.responses :as r]
            [telegram.api.core :as api]))

(def *ctx (atom {}))

(defn- message-type [upd]
  (-> upd
      :message
      :entities
      first
      :type))

(defn command? [upd]
  (when (= (message-type upd) "bot_command")
    (-> upd
        u/message-text?
        (str/split #" ")
        first)))

(defn callback-query? [upd]
  (-> upd
      :callback-query))


(defn update-type [upd]
  (cond (command? upd) :command
        (u/message-text? upd) :text
        (callback-query? upd) :callback-query
        :else :unrecognized))


(defmulti command (fn [upd _] (command? upd)))

(defmethod command :default [upd ctx])

(defmulti text (fn [upd _] (u/message-text? upd)))

(defmethod text :default [upd ctx])

(defmulti dispatch (fn [upd _] (update-type upd)))

(defmethod dispatch :command [upd ctx]
  (command upd ctx))

(defmethod dispatch :text [upd ctx]
  (text upd ctx))

(defn process-actions [config upd ctx actions]
  (when-let [send (:send-text actions)]
    (api/send-message config (:chat-id send) (:text send)))
  
  (when-let [reply (:reply-text actions)]
    (api/send-message config (-> #p upd :message :chat :id) (:text reply))))

(comment
  (def handlers
    {:command {"/start" (fn [upd ctx]
                          {:reply-text {:text "Start command"}})

               #"/foo_\d+" (r/reply-text "Command /foo_NUM")

               :default (r/reply-text "Unknown command")
               }

     :text {:default (fn [upd ctx]
                       {:reply-text {:text (u/message-text? upd)}})}}))

(make-command-predicates (:command handlers))

(defn make-command-predicates [handlers]
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

(defn make-dispatcher [config handlers & {:keys [middleware]}]
  (let [dispatcher
        (fn [upd]
          (let [actions (dispatch upd @*ctx)]
            (process-actions config upd @*ctx actions)))]
    dispatcher))
