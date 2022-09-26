(ns telegram.schema
  (:require [malli.core :as m]
            [malli.generator :as mg]))

(def update-id
  (m/schema [:and :int [:> 10000]]))

(def message-id (m/schema pos-int?))

(def telegram-id (m/schema :int))

(def first-name (m/schema :string {:min 1}))

(def last-name (m/schema [:string {:min 1 :optional true}]))

(def username (m/schema [:string {:min 1 :optional true}]))

(def chat-type (m/schema [:enum "private"]))

(def date (m/schema :int))

(def inline-button
  (m/schema
   [:map
    [:text [:string {:min 1}]]
    [:callback-data [:string {:min 1}]]]))

(def inline-keyboard
  [:schema {:registry {::row [:vector inline-button]
                       ::rows [:vector [:alt [:ref ::row] inline-button]]
                       ::keyboard [:maybe [:vector [:alt [:ref ::rows] inline-button]]]}}
   ::keyboard])

(def reply-markup
  (m/schema
   [:map
    [:inline-keyboard {:optional true} inline-keyboard]]))

(comment
(mg/generate inline-button))

(def from
(m/schema
[:map
 [:id telegram-id]
 [:is-bot :boolean]
 [:first-name first-name]
 [:last-name {:optional true} last-name]
 [:username username]
 [:language-code {:optional true} :string]
 [:is-premium {:optional true} :boolean]]))

(def chat
(m/schema
[:map
 [:id telegram-id]
 [:first-name first-name]
 [:last-name {:optional true} last-name]
 [:username username]
 [:type chat-type]]))

(def message
(m/schema
[:map
 [:message-id message-id]
 [:from from]
 [:chat chat]
 [:date date]
 [:text :string]
 ;; [:reply-markup {:optional true} reply-markup]
 [:entities {:optional true}
  [:vector
   [:map
    [:offset [:and :int [:>= 0]]]
    [:length [:and :int [:>= 0]]]
    [:type [:enum "bot_command"]]]]]]))

(def callback-query
(m/schema
[:map
 [:id :string]
 [:from from]
 [:message message]]))

(def text-update
(m/schema
[:map
 [:update-id update-id]
 [:message message]]))

(def callback-query-update
(m/schema
[:map
 [:update-id update-id]
 [:callback-query
  [:map
   [:from from]
   [:message message]
   [:chat-instance :string]
   [:data :string]]]]))

(def update-any (m/schema [:or text-update callback-query-update]))
