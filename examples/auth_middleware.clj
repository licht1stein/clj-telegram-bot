(ns auth-middleware
  (:require [telegram.middleware.auth :as t.auth]))

(def user-db
  "This is a simple example of some sort of database that stores user information."
  {1234567 {:user "Owner"
            :admin? true}})

(defn user-auth
  "This is a function that we provide to auth middleware maker. It has to accept one argument — a telegram id, and return a map or nil."
  [telegram-id]
  (user-db telegram-id))

(def auth-middleware
  "We can use the user-auth function to create authentication middleware that will add the resulting user map to the update under `:ctb/user` key."
  (t.auth/make-auth-middleware user-auth))

;; Now we can add the middleware when instantiating our dispatcher.
(def dispatcher (t.d/make-dispatcher *ctx handlers :update-middleware [auth-middleware]))
