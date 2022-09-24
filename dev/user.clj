(ns user
  (:require [hashp.core]
            [telegram.core :as t]
            [telegram.bot.dispatcher :as t.dispatcher]
            [telegram.api.core :as t.api]
            [telegram.updates :as t.u]))

;; TODO: add example bots with deps.edn
;; TODO: add babashka with version and build tasks
;; TODO: add webhook support
;; TODO: make webhook server library independent (a ring handler), allowing usage of any server and router

(def config (t/from-pass "telegram/aristarhbot"))

(def handlers
  {:command {#"/start" (fn [upd ctx]
                         {:reply-text {:text "Start command"}})}
   :text {:default (fn [upd ctx]
                   {:reply-text {:text (t.u/message-text? upd)}})}})

(comment
  (def dispatcher (t.dispatcher/make-dispatcher config handlers))
  (def updater (t/start-polling config dispatcher))
  (t/stop-polling updater))
