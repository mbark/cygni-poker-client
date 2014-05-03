(ns poker-client.responses
  (:require [clojure.data.json :as json]
            [clojure.tools.logging :refer [info]]))

(defprotocol Response
  (->map [this]))

(defrecord RegisterForPlay [bot-name]
  Response
  (->map [this]
         {:type "se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"
          :sessionId ""
          :name bot-name
          :room "TRAINING"}))

(deftype Action [id]
  Response
  (->map [this]
         {:type "se.cygni.texasholdem.communication.message.response.ActionResponse"
          :requestId id
          :action { "actionType" "FOLD" "amount" 0}}))
