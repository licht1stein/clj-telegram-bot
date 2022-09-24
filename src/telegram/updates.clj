(ns telegram.updates)

(defn message-text? [upd]
  (-> upd
      :message
      :text))

(defn plain-text [chat-id text]
  {:chat-id chat-id :text text})

(defn reply-text [text]
  {:reply-text {:text text}})
