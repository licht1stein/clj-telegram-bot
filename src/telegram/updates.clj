(ns telegram.updates)

(defn message-text? [upd]
  (-> upd
      :message
      :text))
