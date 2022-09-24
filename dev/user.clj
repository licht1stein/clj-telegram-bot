(ns user
  (:require [hashp.core]
            [telegram.core :as t]
            [telegram.bot.dispatcher :as t.d]
            [telegram.responses :as t.r]))

;; TODO: add example bots with deps.edn
;; TODO: add babashka with version and build tasks
;; TODO: add webhook support
;; TODO: make webhook server library independent (a ring handler), allowing usage of any server and router
;; TODO: make one ctx map with db atom inside

(def *ctx (t/from-pass "telegram/aristarhbot"))

(def handlers
  {:command {#"/start" (fn [upd ctx]
                         {:reply-text {:text "Start command"}})}
   :text {:default (fn [upd ctx]
                     {:reply-text {:text (t.r/message-text? upd)}})}})

(comment
  (def dispatcher (t.d/make-dispatcher *ctx handlers))
  (def updater (t/start-polling *ctx dispatcher))
  (t/stop-polling updater))
