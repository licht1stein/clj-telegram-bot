(ns build
  (:refer-clojure :exclude [test])
  (:require [org.corfield.build :as bb]
            [clojure.java.io :as io]))



(def lib 'net.clojars.clj-telegram-bot/clj-telegram-bot)
(def main 'clj-telegram-bot.clj-telegram-bot)

(defn test "Run the tests." [opts]
  (bb/run-tests opts))

(defn ci "Run the CI pipeline of tests (and build the uberjar)." [& {:keys [version] :as opts}]
  (-> opts
      (assoc :lib lib :version (:version opts) :main main)
      ;; (bb/run-tests)
      (bb/clean)
      (bb/uber)
      ))
