(ns version
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(def version-file (fs/file "resources/version"))

(defn get-version []
  (-> version-file slurp str/trim))

(defn set-version [v]
  (spit version-file v)
  v)

(defn bump-version [version command]
  (case command
    :minor-snapshot (-> version
                        (bump-version :minor)
                        (bump-version :snapshot))
    :major-snapshot (-> version
                        (bump-version :major)
                        (bump-version :snapshot))
    (let [[major minor _] (str/split version #"[\.\-]")
          new-version-coll (->> (case command

                                  :major [(inc (parse-long major)) 0 nil]
                                  :minor [major (inc (parse-long minor)) nil]
                                  :snapshot [major minor "-SNAPSHOT"]

                                  ) )
          new-version (format "%s.%s%s"
                              (first new-version-coll)
                              (second new-version-coll)
                              (or (last new-version-coll) ""))]
      new-version)))

(defn version [& args]
  (let [command (first args)
        allowed #{"minor" "major" "snapshot" "minor-snapshot" "major-snapshot"}
        version (get-version)]
    (cond
      (nil? command) (println version)
      (allowed command) (println (format "%s -> %s" version (set-version (bump-version version (keyword command)))))
      :else (println (format  "Unexpected argument: %s. Run without args to see current version, or one of the following to bump: %s" command (str/join ", " (sort allowed)))))))

(comment
  (version "snapshot")
  (get-version)
  (bump-version "1.1" :minor)
  (bump-version "1.2" :minor-snapshot)
  (bump-version "1.0-SNAPSHOT" :major-snapshot)
  (bump-version "1.0" nil)
  (set-version "1.0-SNAPSHOT"))
