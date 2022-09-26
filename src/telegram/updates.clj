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

(defn from
  "Return who the update is from."
  [upd]
  (cond
    (:callback-query upd) (-> upd :callback-query :from)
    (:message upd) (-> upd :message :from)))

(comment
  (from q)
  (from t)
  (def t {:update-id 328539069,
          :message
          {:message-id 6430,
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
           :date 1664045833,
           :text "simpel text"}})

  (def q {:update-id 328539073,
          :callback-query
          {:id "94660098722300021",
           :from
           {:id 22039771,
            :is-bot false,
            :first-name "Mikhail",
            :last-name "Beliansky",
            :username "beliansky",
            :language-code "en",
            :is-premium true},
           :message
           {:message-id 6319,
            :from
            {:id 419870734,
             :is-bot true,
             :first-name "Аристарх",
             :username "AristarhBot"},
            :chat
            {:id 22039771,
             :first-name "Mikhail",
             :last-name "Beliansky",
             :username "beliansky",
             :type "private"},
            :date 1663244663,
            :text "hello!",
            :reply-markup
            {:inline-keyboard [[{:text "a", :callback-data "1"}]]}},
           :chat-instance "2438618088107699556",
           :data "1"}}))
