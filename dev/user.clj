(ns user
  (:require [hashp.core]
            [telegram.core :as tg]
            [telegram.bot.dispatcher :as tg.dispatcher]
            [telegram.api.core :as tg.api]))

(def config (tg/from-pass "telegram/aristarhbot"))

(defmethod tg.dispatcher/command "/start" [upd ctx]
  (println upd))

(comment
  (tg.api/delete-webhook config)
  (def updater (tg/start-polling config tg.dispatcher/dispatch))
  (tg/stop-polling updater))
