(ns telegram.core
  (:require [clojure.string :as str]
            [clojure.java.shell :as shell]
            [clojure.core.async :refer [go go-loop <! timeout]]
            [telegram.api.core :as api])
  (:gen-class))

(def ^{:private true} default-data
  {:bot {:user-agent "clj-telegram-bot"
         :timeout 300000
         :keepalive 300000}})

(defn from-token
  "Create a map of telegram params using token and check that it works."
  [token & {:keys [] :as opts}]
  (assert (seq token) "No token provided or token is empty.")
  (-> (merge default-data opts)
      (assoc-in [:bot :token] token)
      (assoc :db (atom {:user {} :chat {} :bot {}}))))

(defn from-env
  "Create a map of telegram params using env var BOT_TOKEN and check that it works."
  [& {:keys [] :as opts}]
  (from-token (System/getenv "BOT_TOKEN") opts))

(defn from-fn
  "Create a map of telegram params by providing a function that returns a bot token"
  [token-fn & {:keys [] :as opts}]
  (from-token (token-fn) opts))

(defn from-pass
  "Get token from `pass` secrets manager."
  [key & {:keys [] :as opts}]
  (from-token (-> (shell/sh "pass" key) :out str/trim) opts))

(defn from-op
  "Get token from 1Password CLI."
  [item field & {:keys [] :as opts}]
  (-> (shell/sh "op" "item" "get" item "--fields" field)
      :out
      (str/trim)
      (from-token opts)))

(defn start-polling
  "Start long polling telegram bot API for updates.
  Any incoming updates will be asynchronously processed with dispatcher.

  Returns a boolean atom. Reset it to nil using `stop-polling` to, well, stop polling."
  [ctx dispatcher]
  (let [flag (atom true)]
    (go-loop [last-update-id nil]
      (when @flag
        (let [updates (api/get-updates
                       (:bot ctx)
                       (cond-> {}
                         (some? last-update-id) (assoc :offset last-update-id)))]
          (doseq [upd updates]
            (go (dispatcher upd)))
          (<! (timeout 1000))
          (recur (if (seq updates)
                   (-> updates last :update-id inc)
                   last-update-id) ))))
    flag))

(defn stop-polling [updater]
  (reset! updater nil))
