(ns ping-pong-bot
  (:require [telegram.core :as t]
            [telegram.bot.dispatcher :as t.d]))

(def *ctx (t/from-token "YOUR_BOT_TOKEN"))

(def handlers
  [{:type :message
    :filter #"ping"
    :actions [{:reply-text {:text "pong"}}]}])

(def dispatcher (t.d/make-dispatcher *ctx handlers))
(def updater (t/start-polling *ctx dispatcher))

(comment
  (t/stop-polling updater))
