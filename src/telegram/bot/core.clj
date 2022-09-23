(ns telegram.bot.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.java.shell :as shell]
            [org.httpkit.client :as http]
            [cheshire.core :as json]
            [taoensso.timbre :as timbre]
            [integrant.core :as ig]))

(def default-data {:user-agent "clj-telegram-bot"
                   :timeout 300000
                   :keepalive 300000})

(defn from-token
  "Create a map of telegram params using token and check that it works."
  [token & {:keys [] :as opts}]
  (assert (seq token) "No token provided or token is empty.")
  (merge default-data {:token token} opts))

(defn from-env
  "Create a map of telegram params using env var BOT_TOKEN and check that it works."
  [& {:keys [] :as opts}]
  (from-token (System/getenv "BOT_TOKEN") opts))

(defn from-fn
  "Create a map of telegram params by providing a function that returns a bot token"
  [token-fn & {:keys [] :as opts}]
  (from-token (token-fn) opts))

(defn from-pass
  "Get token from `pass` secrets manager."
  [key]
  (from-token (-> (shell/sh "pass" key) :out str/trim)))

(defn from-op
  "Get token from 1Password CLI."
  [item field]
  (-> (shell/sh "op" "item" "get" item "--fields" field)
      :out
      (str/trim)))

(defn filter-params
  "Filter out nil values from a map."
  [params]
  (persistent!
   (reduce-kv
    (fn [result k v]
      (if (some? v)
        (assoc! result k v)
        result))
    (transient {})
    params)))

(defn encode-params
  "JSON-encode complex values of a map."
  [params]
  (persistent!
   (reduce-kv
    (fn [result k v]
      (if (coll? v)
        (assoc! result k (json/generate-string v))
        (assoc! result k v)))
    (transient {})
    params)))

(defn api-request
  [{:keys [token
           user-agent
           timeout
           keepalive]}

   api-method http-method params]

  (let [params
        (filter-params params)

        url
        (format "https://api.telegram.org/bot%s/%s"
                token (name api-method))

        request
        {:url url
         :method http-method
         :as :stream}

        request
        (cond-> request

          user-agent
          (assoc :user-agent user-agent)

          timeout
          (assoc :timeout timeout)

          keepalive
          (assoc :keepalive keepalive))

        request
        (cond-> request

          ;; for GET, complex values must be JSON-encoded
          (= :get http-method)
          (assoc :query-params (encode-params params))

          (= :post http-method)
          (->
           (assoc-in [:headers "content-type"] "application/json")
           (assoc :body (json/generate-string params))))

        {:keys [error status body headers]}
        @(http/request request)]

    (if error
      (throw (ex-info (format "Telegram HTTP error: %s" (ex-message error))
                      {:api-method api-method
                       :api-params params}
                      error))

      (let [{:keys [content-type]}
            headers

            json?
            (some-> content-type
                    (str/starts-with? "application/json"))

            ;; parse JSON manually as Http Kit cannot
            body-json
            (if json?
              (-> body io/reader (json/decode-stream keyword))
              (throw (ex-info (format "Telegram response was not JSON: %s" content-type)
                              {:http-status status
                               :http-method http-method
                               :http-headers headers
                               :api-method api-method
                               :api-params params})))

            {:keys [ok
                    result
                    error_code
                    description]}
            body-json]

        (if ok
          result
          (throw (ex-info (format "Telegram API error: %s %s %s"
                                  error_code api-method description)
                          {:http-status status
                           :http-method http-method
                           :api-method api-method
                           :api-params params
                           :error-code error_code
                           :error description})))))))

(defn send-message
  "https://core.telegram.org/bots/api#sendmessage"

  ([config chat-id text]
   (send-message config chat-id text nil))

  ([config chat-id text {:keys [parse-mode
                                entities
                                disable-web-page-preview
                                disable-notification
                                protect-content
                                reply-to-message-id
                                allow-sending-without-reply
                                reply-markup]}]

   (api-request config
                :sendMessage
                :post
                {:chat_id chat-id
                 :text text
                 :parse_mode parse-mode
                 :entities entities
                 :disable_web_page_preview disable-web-page-preview
                 :disable_notification disable-notification
                 :protect_content protect-content
                 :reply_to_message_id reply-to-message-id
                 :allow_sending_without_reply allow-sending-without-reply
                 :reply_markup reply-markup})))

(defn reply-text [config msg text & {:keys [] :as opts}]
  (let [chat-id (-> msg :chat :id)]
    (send-message config chat-id text opts)))

(defn get-me
  "https://core.telegram.org/bots/api#getme"
  [config]
  (api-request config :getMe :get nil))

(defn get-updates
  "https://core.telegram.org/bots/api#getupdates"

  ([config]
   (get-updates config nil))

  ([config {:keys [limit
                   offset
                   timeout
                   allowed-updates]}]

   (api-request config
                :getUpdates
                :get
                {:limit limit
                 :offset offset
                 :timeout timeout
                 :allowed_updates allowed-updates})))

(defn ban-user
  "https://core.telegram.org/bots/api#banchatmember"

  ([config chat-id user-id]
   (ban-user config chat-id user-id nil))

  ([config chat-id user-id {:keys [until-date
                                   revoke-messages]}]

   (api-request config
                :banChatMember
                :post
                {:chat_id chat-id
                 :user_id user-id
                 :until_date until-date
                 :revoke_messages revoke-messages})))

(defn delete-message
  "https://core.telegram.org/bots/api#deletemessage"
  [config chat-id message-id]
  (api-request config
               :deleteMessage
               :post
               {:chat_id chat-id
                :message_id message-id}))

(def chat-permission-types
  #{:can_send_messages
    :can_send_media_messages
    :can_send_polls
    :can_send_other_messages
    :can_add_web_page_previews
    :can_change_info
    :can_invite_users
    :can_pin_messages})

(def chat-permissions-on
  (zipmap chat-permission-types (repeat true)))

(def chat-permissions-off
  (zipmap chat-permission-types (repeat false)))

(defn restrict-user

  ([config chat-id user-id permissions]
   (restrict-user config chat-id user-id permissions nil))

  ([config chat-id user-id permissions {:keys [until_date]}]
   (api-request config
                :restrictChatMember
                :post)))

(defn answer-callback-query
  "https://core.telegram.org/bots/api#answercallbackquery"

  ([config callback-query-id]
   (answer-callback-query config callback-query-id nil))

  ([config callback-query-id {:keys [url
                                     text
                                     show-alert?
                                     cache-time]}]
   (api-request config
                :answerCallbackQuery
                :post
                {:callback_query_id callback-query-id
                 :text text
                 :show_alert show-alert?
                 :url url
                 :cache_time cache-time})))

(defn delete-webhook [config & {:keys [drop-pending?]}]
  (api-request config :deleteWebhook :post {:drop_pending_updates (boolean drop-pending?)}))

(defn set-webhook [config url & {:keys [secret-token drop-pending?]}]
  (api-request config :setWebhook :post {:url url :secret_token secret-token :drop_pending_updates (boolean drop-pending?)}))

(defmethod ig/init-key :systems/bot
  [_ {:keys [systems/config]}]
  (timbre/debug ::bot :systems/bot :init)
  (from-token (-> config :bot :token)))

