(ns simple-command-bot
  (:require[telegram.core :as t]
           [telegram.updates :as t.u]   ; update helpers
           [telegram.bot.dispatcher :as t.d]))

(def *ctx (t/from-token "YOUR_BOT_TOKEN"))

(def handlers
  [{:type :command
    :filter "/start"
    :actions [{:reply-text {:text "You called the /start command"}}]}

   {:type :command
    :filter #"/help"
    :actions [{:reply-text {:text "This bot does nothing useful"}}]}

   {:type :command
    :filter (fn [upd ctx] (= (t.u/message-text? upd) "/fn_command"))
    :actions [{:reply-text {:text "Note that you can use functions for :filter and :actions for more complex filtering and action logic"}}]}])

(def dispatcher (t.d/make-dispatcher *ctx handlers))
(def updater (t/start-polling *ctx dispatcher))

(comment
  (t/stop-polling updater))
