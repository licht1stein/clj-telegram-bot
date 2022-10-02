(ns user
  (:require [hashp.core]
            [malli.core :as m]
            [tick.core :as tick]
            [clojure.java.io :as io]
            [telegram.core :as t]
            [telegram.bot.dispatcher :as t.d]
            [telegram.schema :as ts]
            [telegram.updates :as t.u]
            [telegram.middleware.auth :as t.auth]
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


(def *ctx (t/from-pass "telegram/aristarhbot"))

(def handlers
  [{:type :message
    :filter :any
    :passthrough true
    :actions [(fn [upd ctx] {:reply-text {:text (t.u/message-text? upd)}})]}

   {:type :message
    :filter "ping"
    :actions [{:reply-text {:text "pong"}}]}

   {:type :command
    :doc "Handles start command"
    :filter #"/start"
    :actions [{:reply-text {:text "/start command"}}]}

   {:type :command
    :doc "Get user profile by id."
    :filter #"/user_(\d+)"
    :actions [(fn [upd ctx] (println "This is a function"))]}])

(defn save-update-mw [upd]
  (let [temp-dir (io/file "tmp")
        ts (tick/format (tick/formatter "YYYY-MM-DD--HH-mm-ss") (tick/date-time))]
    (when-not (.exists temp-dir)
      (.mkdir temp-dir))
    (spit (format "tmp/%s.edn" ts) (with-out-str (clojure.pprint/pprint upd))))
  upd)

(defn log-update-mw [upd]
  (let [ts (tick/format (tick/formatter "HH:mm:ss") (tick/date-time))]
    (puget/pprint  upd print-opts))
  upd)

(def user-db {22039771 {:user "Owner"
                        :admin? true}})
(defn user-auth [telegram-id]
  (user-db telegram-id))

(def auth-middleware (t.auth/make-auth-middleware user-auth))

(comment
  (def dispatcher (t.d/make-dispatcher *ctx handlers :update-middleware [log-update-mw auth-middleware]))
  (def updater (t/start-polling *ctx dispatcher))
  (t/stop-polling updater))
