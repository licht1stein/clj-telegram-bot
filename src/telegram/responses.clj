(ns telegram.responses)

(defrecord Handlers [command text callback-query conversation inline-query db])

(defn make-handlers-map [& {:keys [command text callback-query conversation inline-query db] :as opts}]
  (map->Handlers opts))

(defrecord Action [send-text reply-text])

(defn make-action-map [& {:keys [send-text reply-text] :as opts}]
  (map->Action opts))

(make-action-map {:send-text {:text "foo"}})

(defn plain-text [chat-id text]
  {:chat-id chat-id :text text})

(defn reply-text [text]
  {:reply-text {:text text}})
