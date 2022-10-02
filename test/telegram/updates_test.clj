(ns telegram.updates-test
  (:require [telegram.updates :as sut]
            [telegram.schema :as s]
            [clojure.test.check.generators :as gen]
            [expectations.clojure.test
             :refer [defexpect expect expecting
                     approximately between between' functionally
                     side-effects]]
            [malli.generator :as mg]))
