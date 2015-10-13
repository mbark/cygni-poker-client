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

(defn- named-rank->num [k v]
  (if (and (string? v) (= k :rank))
    ({"DEUCE" 2
      "THREE" 3
      "FOUR" 4
      "FIVE" 5
      "SIX" 6
      "SEVEN" 7
      "EIGHT" 8
      "NINE" 9
      "TEN" 10
      "JACK" 11
      "QUEEN" 12
      "KING" 13
      "ACE" 14} v)
    v))

(defn msg->map [msg]
  (let [m (json/read-str msg :key-fn ->kebab-case-keyword :value-fn named-rank->num)]
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
