(ns user
  (:require [hashp.core]
            [tick.core :as tick]
            [clojure.java.io :as io]
            [telegram.core :as t]
            [telegram.bot.dispatcher :as t.d]
            [telegram.updates :as t.u]
            [puget.printer :as puget]
            [telegram.responses :as t.r]))

;; TODO: add example bots with deps.edn
;; TODO: add babashka with version and build tasks
;; TODO: add webhook support
;; TODO: make webhook server library independent (a ring handler), allowing usage of any server and router
;; TODO: make one ctx map with db atom inside

(def print-opts
  (merge puget/*options*
         {:print-color    true
          :namespace-maps true
          :color-scheme
          {:nil [:bold :blue]}}))



(def temp-dir (io/file "tmp"))
(when-not (.exists temp-dir)
  (.mkdir temp-dir))

(def *ctx (t/from-pass "telegram/aristarhbot"))

(def handlers
  {:command {#"/start" (fn [upd ctx]
                         {:reply-text {:text "Start command"}})}
   :text {:default (fn [upd ctx]
                     {:reply-text {:text (t.u/message-text? upd)}})}})

(defn save-update-mw [upd]
  (let [ts (tick/format (tick/formatter "YYYY-MM-DD--HH-mm-ss") (tick/date-time))]
    (spit (format "tmp/%s.edn" ts) (with-out-str (clojure.pprint/pprint upd))))
  upd)

(defn log-update-mw [upd]
  (let [ts (tick/format (tick/formatter "HH:mm:ss") (tick/date-time))]
    (puget/pprint  upd print-opts))
  upd)

(comment
  (def dispatcher (t.d/make-dispatcher *ctx handlers :update-middleware [save-update-mw log-update-mw]))
  (def updater (t/start-polling *ctx dispatcher))
  (t/stop-polling updater))
