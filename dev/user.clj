(ns user
  (:require [hashp.core]
            [telegram.core :as t]
            [telegram.bot.dispatcher :as t.dispatcher]
            [telegram.api.core :as t.api]
            [telegram.updates :as t.u]))

(def config (t/from-pass "telegram/aristarhbot"))

(defmethod t.dispatcher/command "/start" [upd ctx]
  {:reply-text {:text "Start command"}})

(defmethod t.dispatcher/text :default [upd ctx]
  {:reply-text {:text (t.u/message-text? upd)}})

(comment
  (def dispatcher (t.dispatcher/make-dispatcher config))
  (def updater (t/start-polling config dispatcher))
  (t/stop-polling updater))
