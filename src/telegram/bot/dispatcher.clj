(ns telegram.bot.dispatcher
  (:require [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [integrant.core :as ig]))

(defn message-type [upd]
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


(defmulti command command?)

(defmethod command :default [_]
  (timbre/debug ::command :unknown))

(defmulti text message-text?)

(defmethod text :default [_])

(defmulti dispatch update-type)

(defmethod dispatch :command [upd]
  (timbre/info ::dispatch :command (command? upd))
  (command upd))

(defmethod dispatch :text [upd]
  (timbre/debug ::dispatch :text)
  (text upd))


(comment
  (require '[telegram.server.core :refer [upd d]])
  upd
  (d)
  (d upd)
  (update-type upd)

  (command? upd)
  (dispatch upd)
  (message-text? upd))

(defmethod ig/init-key :systems/dispatcher
  [_ _]
  (timbre/debug ::dispatcher :init)
  dispatch)
