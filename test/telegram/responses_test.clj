(ns telegram.responses-test
  (:require [clojure.test :refer :all]
            [telegram.responses :as r]))


(deftest tests-are-working
  (testing "Correct start of test suite"
    (is (= 1 1))))

(deftest response-makers
  (testing "Simple text maps"
    (is (= {:send-text {:chat-id 1 :text "foo"}} (r/plain-text 1 "foo")))
    (is (= {:reply-text {:text "foo"}} (r/reply-text "foo")))))
