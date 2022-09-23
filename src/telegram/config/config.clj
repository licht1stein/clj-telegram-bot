(ns telegram.config.config
  (:require [integrant.core :as ig]
            [taoensso.timbre :as timbre]
            [cprop.core :refer [load-config]]))


(defmethod ig/init-key :systems/config
  [_ _]
  (timbre/debug :systems/config :init)
  (load-config))
