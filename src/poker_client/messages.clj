(ns poker-client.messages
  (:require [clojure.tools.logging :refer [info]]))

(defn register-for-play [bot-name]
  {:type "se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"
   :sessionId ""
   :name bot-name
   :room "TRAINING"})

(defn action-response [id action]
  {:type "se.cygni.texasholdem.communication.message.response.ActionResponse"
   :requestId id
   :action action})
