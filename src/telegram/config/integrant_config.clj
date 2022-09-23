(ns telegram.config.integrant-config
  (:require [integrant.core :as ig]))

(def config
  {:systems/config
   {:options {}}

   :systems/bot
   {:options {}
    :config (ig/ref :systems/config)}

   :systems/dispatcher
   {:options {}}

   :systems/server
   {:options {}
    :systems/dispatcher (ig/ref :systems/dispatcher)
    :systems/config (ig/ref :systems/config)}})
