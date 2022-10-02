(ns echo-bot
  (:require[telegram.core :as t]
           [telegram.updates :as t.u]   ; update helpers
           [telegram.bot.dispatcher :as t.d]))

(def *ctx (t/from-token "YOUR_BOT_TOKEN"))

(def handlers
  [{:type :message
    :filter :any
    :actions [(fn [upd ctx] {:reply-text {:text (t.u/message-text? upd)}})]}])

(def dispatcher (t.d/make-dispatcher *ctx handlers))
(def updater (t/start-polling *ctx dispatcher))

(comment
  (t/stop-polling updater))
