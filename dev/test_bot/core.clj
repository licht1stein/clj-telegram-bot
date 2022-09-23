(ns test-bot.core
  (:require [telegram.bot.dispatcher :as dispatcher]))

(def telegram (from-pass))
(def ngrok "https://17ee5b8eecb4.ngrok.io")

(comment
  (bot/delete-webhook telegram)
  (bot/get-me telegram)
  (bot/set-webhook telegram (str ngrok "/telegram/update") :drop-pending? true))



(defmethod dispatcher/command "/start" [upd]
  (println "/start"))

(defmethod dispatcher/text "foo" [upd]
  (println "bar"))
