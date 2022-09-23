(ns user
  (:require [hashp.core]
            [telegram.core :as tg]
            [telegram.bot.dispatcher :as tg.dispatcher]
            [telegram.api.core :as tg.api]))

(def config (tg/from-pass "telegram/aristarhbot"))

(defmethod tg.dispatcher/command "/start" [upd ctx]
  {:reply-text {:text "Start command"}})

(defmethod tg.dispatcher/text :default [upd ctx]
  {:reply-text {:text (tg.dispatcher/message-text? upd)}})

(comment
  (tg.api/delete-webhook config)
  (def dispatcher (tg.dispatcher/make-dispatcher config))
  (def updater (tg/start-polling config dispatcher))
  (tg/stop-polling updater))
