(ns telegram.core
  (:require [org.httpkit.server :as server]
            [taoensso.timbre :as timbre]
            [reitit.ring :as ring])
  (:gen-class))


(defonce server (atom nil))

(def secret-token (str (random-uuid)))
(def handler
  (ring/ring-handler
   (ring/router
    [["/telegram/update"
      {:post (fn [request]
               (timbre/info ::update-received)
               (def r request)
               {:status 201})}]])))

(comment
  (-> r :body)
  )

(defn stop-server []
  (if-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (do
      (timbre/debug ::stop-server :stopping)
      (@server :timeout 100)
      (reset! server nil)
      (timbre/info ::stop-server :stopped))

    (timbre/info ::stop-server :not-running)))

(defn start-server [& args]
  ;; The #' is useful when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and https://http-kit.github.io/migration.html#reload
  (if-not @server
    (do
      (timbre/debug ::start-server :starting)
      (reset! server (server/run-server #'handler {:port 8080}))
      (timbre/info ::start-server :started))
    (timbre/info ::start-server :already-running)))

(comment
  (stop-server)
  (start-server))
