(ns telegram.schema
  (:require [malli.core :as m]
            [malli.generator :as mg]))

(def TelegramRegistry
  "Objects from Telegram API are named using :api/CamelCase, just like in the official API docs: https://core.telegram.org/bots/api"
  ;; props
  {:e/update-id [:and :int [:> 10000]]
   :e/message-id pos-int?
   :e/telegram-id :int
   :e/email [:re #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$"]
   :e/first-name [:string {:min 1}]
   :e/last-name [:string {:min 1}]
   :e/username [:string {:min 1}]
   :e/chat-type [:enum "private" "group" "supergroup" "channel"]
   :e/date pos-int?
   :e/some-string [:string {:min 1}]

   ;; API Objects
   :api/Update
   [:map
    [:update-id :e/update-id]
    [:message {:optional true} [:ref :api/Message]]
    [:edited-message {:optional true} [:ref :api/Message]]
    [:channel-post {:optional true} [:ref :api/Message]]
    [:edited-channel-post {:optional true} [:ref :api/Message]]
    [:inline-query {:optional true} :InlineQuery]
    [:chosen-inline-result {:optional true} :ChosenInlineResult]
    [:callback-query {:optional true} :CallbackQuery]
    [:shipping-query {:optional true} :ShippingQuery]
    [:pre_checkout_query {:optional true} :PreCheckoutQuery]
    [:poll {:optional true} :Poll]
    [:poll-answer {:optional true} :PollAnswer]
    [:my-chat-member {:optional true} :ChatMemberUpdated]
    [:chat-member {:optional true} :ChatMemberUpdated]
    [:chat-join-request {:optional true} :ChatJoinRequest]]

   :api/User
   [:map
    [:id :e/telegram-id]
    [:is-bot :boolean]
    [:first-name :e/first-name]
    [:last-name {:optional true} :e/last-name]
    [:username :e/username]
    [:language-code {:optional true} :string]
    [:is-premium {:optional true} :boolean]
    [:added-to-attachment-menu {:optional true} :boolean]
    [:can-join-groups {:optional true} :boolean]
    [:can-read-all-group-messages {:optional true} :boolean]
    [:support-inline-queries {:optional true} :boolean]]

   :api/Chat
   [:map
    [:id :e/telegram-id]
    [:type :e/chat-type]
    [:title {:optional true} :e/some-string]
    [:username :e/username]
    [:first-name {:optional true} :e/first-name]
    [:last-name {:optional true} :e/last-name]
    [:photo {:optional true} :api/ChatPhoto]
    [:bio {:optional true} :e/some-string]
    [:has-private-forwards {:optional true} :boolean]
    [:has-restricted-voice-and-video-messages {:optional true} :boolean]
    [:join-to-send-messages {:optional true} :boolean]
    [:join-by-request {:optional true} :boolean]
    [:description {:optional true} :e/some-string]
    [:invite-link {:optional true} :e/some-string]
    [:pinned-message {:optional true} [:ref :api/Message]]
    [:permissions {:optional true} :api/ChatPermissions]
    [:slow-mode-delay {:optional true} [:int {:min 0}]]
    [:message-auto-delete-time {:optional true} [:int {:min 0}]]
    [:has-protected-content {:optional true} :boolean]
    [:sticker-set-name {:optional true} [:string {:min 1}]]
    [:can-set-sticker-set {:optional true} :boolean]
    [:linked-chat-id {:optional true} :e/telegram-id]
    [:location {:optional true} :api/ChatLocation]]

   :api/ChatPermissions
   [:map
    [:can-send-messages :boolean]
    [:can-send-media-messages :boolean]
    [:can-send-polls :boolean]
    [:can-send-other-messages :boolean]
    [:can-add-web-page-previews :boolean]
    [:can-change-info :boolean]
    [:can-invite-users :boolean]
    [:can-ping-mesages :boolean]]

   :api/Message
   [:map
    [:message-id :e/message-id]
    [:from {:optional true} [:ref :api/User]]
    [:sender-chat {:optional true} :api/Chat]
    [:chat :api/Chat]
    [:date :e/date]
    [:forward-from {:optional true} [:ref :api/User]]
    [:forward-from-chat {:optional true} :api/Chat]
    [:forward-from-message-id {:optional true} :e/message-id]
    [:forward-signature {:optional true} :e/some-string]
    [:forward-sender-name {:optional true} :e/some-string]
    [:forward-date {:optional true} :e/date]
    [:is-automatic-forward {:optional true} :boolean]
    [:reply-to-message {:optional true} [:ref :api/Message]]
    [:via-bot {:optional true} [:ref :api/User]]
    [:edit-date {:optional true} :e/date]
    [:has-protected-content {:optional true} :boolean]
    [:media-group-id {:optional true} :e/some-string]
    [:author-signature {:optional true} :e/some-string]
    [:text {:optional true} :e/some-string]
    [:entities {:optional true} [:vector :api/MessageEntity]]
    [:animation {:optional true} :Animation]
    [:audio {:optional true} :Audio]
    [:document {:optional true} :Document]
    [:photo {:optional true} [:vector :PhotoSize]]
    [:sticker {:optional true} :Sticker]
    [:video {:optional true} :Video]
    [:video-note {:optional true} :VideoNote]
    [:voice {:optional true} :Voice]
    [:caption {:optional true} :e/some-string]
    [:caption-entities {:optional true} [:vector :api/MessageEntity]]
    [:contact {:optional true} :Contact]
    [:dice {:optional true} :Dice]
    [:game {:optional true} :Game]
    [:poll {:optional true} :Poll]
    [:venue {:optional true} :Venue]
    [:location {:optional true} :Location]
    [:new-chat-members {:optional true} [:vector :api/User]]
    [:left-chat-member {:optional true} [:ref :api/User]]
    [:new-chat-title {:optional true} :e/some-string]
    [:new-chat-photo {:optional true} [:vector :PhotoSize]]
    [:delete-chat-photot {:optional true} :boolean]
    [:group-chat-created {:optional true} :boolean]
    [:supergeroup-chat-created {:optional true} :boolean]
    [:channel-chat-created {:optional true} :boolean]
    [:message-auto-delete-timer-changed {:optional true} :AutoDeleteTimerChanged]
    [:migrate-to-chat-id {:optional true} :e/telegram-id]
    [:pinned-message {:optional true} [:ref :api/Message]]
    [:invoice {:optional true} :Invoice]
    [:successful-payment {:optional true} :SuccessfulPayment]
    [:connected-website {:optional true} :e/some-string]
    [:passport-data {:optional true} :PassportData]
    [:proximity-alert-triggered {:optional true} :ProximityAlertTriggered]
    [:video-chat-scheduled {:optional true} :VideoChatScheduled]
    [:video-chat-started {:optional true} :VideoChatStarted]
    [:video-chat-ended {:optional true} :VideoChatEnded]
    [:video-chat-participants-invited {:optional true} :VideoChatParticipantsInvited]
    [:web-app-data {:optional true} :WebAppData]
    [:reply-markup {:optional true} :InlineKeyboardMarkup]]

   :api/MessageEntity
   [:map
    [:type
     [:enum
      "mention"
      "hashtag"
      "cashtag"
      "bot_command"
      "url"
      "email"
      "phone_number"
      "bold"
      "italic"
      "underline"
      "strikethrough"
      "spoiler"
      "code"
      "pre"
      "text_link"
      "text_mention"
      "custom_emoji"]]
    [:offset pos-int?]
    [:length pos-int?]
    [:url {:optional true} :e/some-string]
    [:user {:optional true} [:ref :api/User]]
    [:language {:optional true} :e/some-string]
    [:custom-emoji-id {:optional true} :e/some-string]]

   :api/ChatPhoto
   [:map
    [:small-file-id :e/some-string]
    [:small-file-unique-id :e/some-string]
    [:big-file-id :e/some-string]
    [:big-file-unique-id :e/some-string]]

   :api/Location
   [:map
    [:longitude float?]
    [:latitude float?]
    [:horizontal-accuracy [float? {:min 0 :max 1500}]]
    [:live-period :int]
    [:heading [:int {:min 1 :max 360}]]
    [:proximity-alert-radius :int]]

   :api/ChatLocation
   [:map
    [:location :api/Location]
    [:address [:string {:min 1}]]]

   :WebAppInfo :string                  ;TODO

   :LoginURL
   [:map
    [:url :e/some-string]
    [:forward-text {:optional true} :e/some-string]
    [:bot-username {:optional true} :e/some-string]
    [:request-write-access {:optional true} :boolean]]

   :CallbackGame :string                ;TODO

   :InlineKeyboardButton
   [:map
    [:text :e/some-string]
    [:url {:optional true} :e/some-string]
    [:callback-data {:optional true} :e/some-string]
    [:web-app :WebAppInfo]
    [:login-url :LoginURL]
    [:switch-inline-query {:optional true} :e/some-string]
    [:switch-inline-query-current-chat {:optional true} :e/some-string]
    [:callback-game {:optional true} :CallbackGame]
    [:pay {:optional true} :boolean]]

   ;; Used for recursion
   :inline-button-row [:vector :InlineKeyboardButton]
   :inline-button-rows [:vector [:alt :inline-button-row :InlineKeyboardButton]]

   :InlineKeyboardMarkup [:vector [:alt :inline-button-rows :InlineKeyboardButton]]

   })


(defn get-schema [identifier]
  (m/schema
   [:schema
    {:registry TelegramRegistry}
    identifier]))

(def schema:User (get-schema :api/User))

(def schema:InlineKeyboardMarkup
  (m/schema
   [:schema
    {:registry TelegramRegistry}
    :InlineKeyboardMarkup]))
