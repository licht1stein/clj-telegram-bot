(ns ^{:doc "Standard helpers to produce auth middleware."}
    telegram.middleware.auth
  (:require [telegram.updates :as u]))

(defn make-auth-middleware
  "Provide a `auth-fn` function that takes one argument — an integer telegram id, and returns nil or a map with user data."
  [auth-fn]
  (fn [upd]
    (let [from-id (-> upd u/from :id)
          authenticated (auth-fn from-id)]
      (assoc upd :ctb/user authenticated))))
