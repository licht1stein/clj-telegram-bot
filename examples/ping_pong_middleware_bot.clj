(ns ping-pong-middleware-bot
  (:require [telegram.core :as t]
            [telegram.bot.dispatcher :as t.d]))

(def *ctx (t/from-token "YOUR_BOT_TOKEN"))

(def handlers
  [{:type :message
    :filter #"ping"
    :actions [{:reply-text {:text "pong"}}]}])

(defn log-update [upd ctx]
  (println upd)
  upd)

(defn spit-update [upd ctx]
  (spit "last-update.edn" upd)
  upd)

(def dispatcher (t.d/make-dispatcher *ctx handlers :update-middleware [spit-update log-update]))
(def updater (t/start-polling *ctx dispatcher))

(comment
  (t/stop-polling updater))
