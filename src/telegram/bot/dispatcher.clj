(ns telegram.bot.dispatcher
  (:require [clojure.string :as str]
            [telegram.api.core :as api]))

(def *ctx (atom {}))

(defn- message-type [upd]
  (-> upd
      :message
      :entities
      first
      :type))

(defn message-text? [upd]
  (-> upd
      :message
      :text))

(defn command? [upd]
  (when (= (message-type upd) "bot_command")
    (-> upd
        message-text?
        (str/split #" ")
        first)))

(defn callback-query? [upd]
  (-> upd
      :callback-query))


(defn update-type [upd]
  (cond (command? upd) :command
        (message-text? upd) :text
        (callback-query? upd) :callback-query
        :else :unrecognized))


(defmulti command (fn [upd _] (command? upd)))

(defmethod command :default [upd ctx])

(defmulti text (fn [upd _] (message-text? upd)))

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

(defn make-dispatcher [config]
  (let [dispatcher
        (fn [upd ctx]
          (let [actions (dispatch upd ctx)]
            (process-actions config upd ctx actions)))]
    dispatcher))
