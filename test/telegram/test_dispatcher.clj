(ns telegram.test-dispatcher
  (:require [clojure.test :refer :all]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def updates-dir (io/file "test/updates"))
(def updates (filter #(.isFile %) (file-seq updates-dir)))



(defn anonymize-update [update]
  (let [fname "Firstname"
        lname "Lastname"
        uname "username"
        id 123456
        anonymizer {:message {:from {:id id
                                     :first-name fname
                                     :last-name lname}}
                    :chat {:id id
                           :first-name fname
                           :last-name lname
                           :username uname}}]
    (merge update anonymizer)))

(defn anonymize-all-files []
  (doseq [f updates]
    (let [res (-> f slurp edn/read-string anonymize-update)]
      (spit f (with-out-str (clojure.pprint/pprint res))))))

(comment
  (anonymize-all-files))
