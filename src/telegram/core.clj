(ns telegram.core
  (:require [integrant.core :as ig]
            [taoensso.timbre :as timbre]
            [telegram.config.integrant-config :as igc]
            [telegram.config.config]
            [telegram.server.core]
            [telegram.bot.core])
  (:gen-class))


;; (defn -main [& args]
;;   (ig/init igc/config))
