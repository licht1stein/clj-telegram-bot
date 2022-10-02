(ns telegram.schema-test
  (:require [telegram.schema :as s]
            [expectations.clojure.test
             :refer [defexpect expect expecting
                     approximately between between' functionally
                     side-effects]]
            [malli.core :as m]))

(def sample-text
  {:update-id 328539070,
   :message
   {:message-id 6431,
    :from
    {:id 1234567,
     :is-bot false,
     :first-name "Firstname",
     :last-name "Lname",
     :username "username",
     :language-code "en",
     :is-premium true},
    :chat
    {:id 1234567,
     :first-name "Firstname",
     :last-name "Lname",
     :username "username",
     :type "private"},
    :date 1664046148,
    :text "simple text"}})

(def sample-callback-query
  {:update-id 328539073,
   :callback-query
   {:id "94660098722300021",
    :from
    {:id 1234567,
     :is-bot false,
     :first-name "Fname",
     :last-name "Lname",
     :username "username",
     :language-code "en",
     :is-premium true},
    :message
    {:message-id 6319,
     :from
     {:id 654321,
      :is-bot true,
      :first-name "Sample Bot",
      :username "SampleBot"},
     :chat
     {:id 1234567,
      :first-name "Fname",
      :last-name "Lname",
      :username "username",
      :type "private"},
     :date 1663244663,
     :text "hello!",
     :reply-markup
     {:inline-keyboard [[{:text "a", :callback-data "1"}]]}},
    :chat-instance "2438618088107699556",
    :data "1"}})

(def sample-command-update
  {:update-id 328539074,
   :message
   {:message-id 6434,
    :from
    {:id 1234567,
     :is-bot false,
     :first-name "Fname",
     :last-name "Lname",
     :username "username",
     :language-code "en",
     :is-premium true},
    :chat
    {:id 1234567,
     :first-name "Fname",
     :last-name "Lname",
     :username "username",
     :type "private"},
    :date 1664193098,
    :text "/command",
    :entities [{:offset 0, :length 8, :type "bot_command"}]}})
