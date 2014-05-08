(ns poker-client.message
  (:require [clojure.tools.logging :refer [info]]
            [clojure.data.json :as json :refer [read-str write-str]]
            [camel-snake-kebab :refer [->camelCaseString
                                       ->kebab-case-keyword
                                       ->kebab-case]]))

(defn- ->str [msg]
  (json/write-str msg :key-fn ->camelCaseString))

(defn- event-class [data]
  (->kebab-case
   (last
    (clojure.string/split
     (:type data)
     #"\."))))

(defn msg->map [msg]
  (let [m (json/read-str msg :key-fn ->kebab-case-keyword)]
    (assoc m :type (event-class m))))

(defn request? [msg]
  (= (:type msg) "action-request"))

(defn exception? [msg]
  (= (:type msg) "username-already-taken-exception"))

(defn event? [msg]
  (not (or
        (request? msg)
        (exception? msg))))

(defn action-response [request action]
  (->str
   {:type "se.cygni.texasholdem.communication.message.response.ActionResponse"
    :requestId (:request-id request)
    :action action}))

(defn register-for-play [bot-name]
  (->str
   {:type "se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"
    :sessionId ""
    :name bot-name
    :room "TRAINING"}))
