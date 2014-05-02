(ns poker-client.responses
  (:require [clojure.data.json :as json])
  (:use [clojure.tools.logging :as log]
        [poker-client player-bot]))

(defprotocol Response
  (->map [this]))

(defrecord RegisterForPlay [bot]
  Response
  (->map [this]
         {:type "se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"
          :sessionId ""
          :name (bot-name bot)
          :room "TRAINING"}))

(deftype Action [id]
  Response
  (->map [this]
         {:type "se.cygni.texasholdem.communication.message.response.ActionResponse"
          :sessionId id
          :action { "actionType" "FOLD" "amount" 0}}))
