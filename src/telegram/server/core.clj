(ns telegram.server.core
  (:require [org.httpkit.server :as server]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [integrant.core :as ig]
            [cheshire.core :as json]
            [reitit.ring :as ring]
            [camel-snake-kebab.core :as csk])
  (:gen-class))

(defn update-type [update]
    (-> update
      :message
      :entities
      first
      :type))

(def secret-token (str (random-uuid)))
(defn make-handler [dispatcher]
  (assert fn? dispatcher)
  (ring/ring-handler
   (ring/router
    [["/telegram/update"
      {:post (fn [request]
               (timbre/info ::update-received)
               (let [body (-> request
                              :body
                              io/reader
                              slurp
                              (json/parse-string csk/->kebab-case-keyword))]
                 (def upd body)
                 (dispatcher body)
                 )
               {:status 201})}]])))


(defn stop-server [server]
  (if server
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (do
      (timbre/debug ::stop-server :stopping)
      (server :timeout 100)
      (timbre/info ::stop-server :stopped))

    (timbre/info ::stop-server :not-running)))

(defn start-server [port handler]
  ;; The #' is useful when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and https://http-kit.github.io/migration.html#reload
  (server/run-server handler {:port port}))

(defmethod ig/init-key :systems/server
  [_ {:keys [systems/config systems/dispatcher]}]
  (timbre/debug ::server :systems/server :init)
  (let [handler (make-handler dispatcher)]

    (start-server (-> config :port) handler)))

(defmethod ig/halt-key! :systems/server
  [_ server]
  (timbre/debug ::server :systems/halt {:server server})
  (stop-server server))

