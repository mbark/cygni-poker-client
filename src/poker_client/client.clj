(ns poker-client.client
  (:gen-class)
  (:require [clojure.tools.logging :refer [info]]
            [camel-snake-kebab :refer [->kebab-case]]
            [poker-client.socket :refer [connect send-msg next-event]]
            [poker-client.routing :refer [route]]
            [poker-client.player :refer [bot-name]]
            [poker-client.messages :as msg :refer [register-for-play]]))

(defn- done? [response]
  (or
   (empty? response)
   (= "UsernameAlreadyTakenException" (response :type))))

(defn- json-keys->keyword [json]
  (into
   {}
   (for [[k v] json]
     [(keyword (->kebab-case k))
      (cond
       (vector? v) (map json-keys->keyword v)
       (map? v) (json-keys->keyword v)
       :else v)])))

(defn- type-class [data]
  (last
   (clojure.string/split
    (:type data)
    #"\.")))

(defn- ->clj-map [json]
  (let [m (json-keys->keyword json)]
    (assoc m :type (type-class m))))

(defn- exit [conn]
  (info "Shutting down")
  (dosync (alter conn merge {:exit true})))

(defn- event-handler [conn bot]
  (try
    (while (nil? (:exit @conn))
      (let [event (->clj-map (next-event conn))]
        (info (str "Received event " event))
        (if (done? event)
          (exit conn)
          (when-let [action (route event bot)]
            (send-msg conn (msg/action-response (:request-id event) action))))))
    (catch Exception e (exit conn))))

(defn start [server bot]
  (let [conn (connect server)]
    (send-msg conn
              (msg/register-for-play (bot-name bot)))
    (doto (Thread. #(event-handler conn bot)) (.start))))
