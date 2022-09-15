(ns telegram.db.core
  (:require [datascript.core :as d]))


(def schema {:bot/token {:db/cardinality :db.cardinality/one}})

(def conn (d/create-conn schema))

(defn bot:set-token! [conn token]
  (d/transact! conn [{:bot/token token}]))



(comment
  (bot:set-token! conn "bar")
  @conn
  (d/q '[:find ?token
         :where [_ :bot/token ?token]]
       @conn))


(let [schema {:aka {:db/cardinality :db.cardinality/many}}
      conn   (d/create-conn schema)]
  (d/transact! conn [ { :db/id -1
                       :name  "Maksim"
                       :age   45
                       :aka   ["Max Otto von Stierlitz", "Jack Ryan"] } ])
  (d/q '[ :find  ?n ?a
         :where [?e :aka "Max Otto von Stierlitz"]
         [?e :name ?n]
         [?e :age  ?a] ]
       @conn))
