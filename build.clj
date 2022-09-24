(ns build
  (:refer-clojure :exclude [test])
  (:require [org.corfield.build :as bb]
            [clojure.java.io :as io]))



(def lib 'com.github.licht1stein/clj-telegram-bot)
(def main 'telegram.core)

(defn test "Run the tests." [opts]
  (bb/run-tests opts))

(defn jar "Build uberjar" [& {:keys [version] :as opts}]
  (-> opts
      (assoc :lib lib :version version :main main)
      (bb/uber)))

(defn deploy "Deploy the JAR to Clojars." [{:keys [version] :as opts}]
  (-> opts
      (assoc :lib lib :version version)
      (bb/deploy)))
