(ns poker-client.message
  (:require [clojure.tools.logging :refer [info]]
            [clojure.data.json :as json :refer [write-str]]
            [camel-snake-kebab :refer [->camelCaseString]]))

(defprotocol IMessage
  (->map [this]))

(defrecord RegisterForPlay [bot-name]
  IMessage
  (->map
   [_]
   {:type "se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"
    :sessionId ""
    :name bot-name
    :room "TRAINING"}))

(defrecord ActionResponse [id action]
  IMessage
  (->map
   [_]
   {:type "se.cygni.texasholdem.communication.message.response.ActionResponse"
    :requestId id
    :action action}))

(defn ->str [msg]
  (json/write-str (->map msg) :key-fn ->camelCaseString)
