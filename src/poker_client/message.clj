(ns poker-client.message
  (:require [clojure.tools.logging :refer [info]]
            [clojure.data.json :as json :refer [write-str]]))

(defprotocol IMessage
  (->str [this])
  (->map [this]))

(defrecord RegisterForPlay [bot-name]
  IMessage
  (->str
   [this]
   (json/write-str (->map this)))
  (->map
   [_]
   {:type "se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"
    :sessionId ""
    :name bot-name
    :room "TRAINING"}))

(defrecord ActionResponse [id action]
  IMessage
  (->str [this] (json/write-str (->map this)))
  (->map
   [_]
   {:type "se.cygni.texasholdem.communication.message.response.ActionResponse"
    :requestId id
    :action action}))
