(ns user
  (:require [integrant.core :as ig]
            [clojure.string :as str]
            [clojure.java.shell :as shell]
            [integrant.repl :refer [go prep halt reset-all] :as igr]
            [integrant.repl.state :refer [system]]
            [taoensso.timbre :as timbre]
            [telegram.config.integrant-config :as igc]
            [telegram.config.config]
            [telegram.server.core]
            [telegram.bot.dispatcher]
            [telegram.bot.core :as bot]
            [hashp.core]))

(integrant.repl/set-prep! #(ig/prep igc/config))
(prep)
(go)

(comment


  integrant.repl.state/system
  (halt)
  (reset-all)
  )

(comment
  (def telegram (from-pass))
  (def ngrok "https://17ee5b8eecb4.ngrok.io")

  (bot/delete-webhook telegram)
  (bot/get-me telegram)
  (bot/set-webhook telegram (str ngrok "/telegram/update") :drop-pending? true)

  (get-updates telegram {:timeout 10})

  (delete-webhook telegram :drop-pending? true)


  (ban-user telegram -721166690 223429441 {:unix-until 0})

  (send-message telegram 22039771 "hello!")
  (send-message telegram 22039771 "hello!"
                {:reply-markup
                 {:inline_keyboard
                  [[{:text "a"
                     :callback_data 1}
                    {:text "b"
                     :callback_data 2}]]}}))
