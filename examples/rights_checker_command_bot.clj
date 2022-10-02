(ns rights-checker-command-bot
  (:require [telegram.core :as t]
            [telegram.bot.dispatcher :as t.d]
            [telegram.middleware.auth :as t.auth]))

(def *ctx (t/from-token "YOUR_BOT_TOKEN"))

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

(def handlers
  [{:type :command
    :doc "Doesn't require authentication (no :user key)"
    :filter #"/start"
    :actions [{:reply-text {:text "/start command"}}]}

   {:type :command
    :filter "/admin"
    :user :admin?
    :doc "Checks if :ctb/user map produced by middleware contains and :admin? key"
    :actions [{:reply-text {:text "You are an admin"}}]}

   {:type :command
    :filter "/admin_fn"
    :user (fn [user] (:admin? user))
    :doc "Check for :admin? key but using a function"
    :actions [{:reply-text {:text "You are an admin"}}]}])

;; Now we can add the middleware when instantiating our dispatcher.
(def dispatcher (t.d/make-dispatcher *ctx handlers :update-middleware [auth-middleware]))
(def updater (t/start-polling *ctx dispatcher))

(comment
  (t/stop-polling updater))
