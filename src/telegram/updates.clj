(ns telegram.updates
  (:require [clojure.string :as str]))

(defn message-text?
  "Extract message text from an update if any. Otherwise return nil."
  [upd]
  (-> upd
      :message
      :text))

(defn- message-type
  "Return message-type specified by Telegram or nil."
  [upd]
  (-> upd
      :message
      :entities
      first
      :type))

(defn command?
  "Return a map with two keys :command and :args if `message-type` returns \"bot_command\".

  Example. User sent: \"/start foo bar\". Output:
  
      {:command \"/start\" :args [\"foo\" \"bar\"]}"
  [upd]
  (when (= (message-type upd) "bot_command")
    (let [parsed (-> upd
                     message-text?
                     (str/split #" "))
          trimmed (map str/trim parsed)]
      {:command (first trimmed)
       :args (rest trimmed)})))

(defn callback-query?
  "Return callback-query map or nil."
  [upd]
  (-> upd
      :callback-query))

(defn update-type
  "Try to figure out the type of update. Can be :command :text :callback-query or :unrecognized"
  [upd]
  (cond (command? upd) :command
        (message-text? upd) :text
        (callback-query? upd) :callback-query
        :else :unrecognized))
