(ns poker-client.main
  (:gen-class)
  (:use [clojure.tools.logging :as log]
        [camel-snake-kebab]
        [poker-client socket routing player-bot]))

(def poker-bot-name "clojure-client")
(def poker-bot (->LoggingBot))

(def register-for-play
  {:type "se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"
   :sessionId ""
   :name poker-bot-name
   :room "TRAINING"})

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

(defn- event-handler [conn]
  (while (nil? (:exit @conn))
    (let [event (->clj-map (next-event conn))]
      (info (str "Received event " event))
      (if (done? event)
        (dosync (alter conn merge {:exit true}))
        (route event poker-bot)))))

(defn start-client [server]
  (let [conn (connect server)]
    (respond conn register-for-play)
    (doto (Thread. #(event-handler conn)) (.start))))

(defn -main []
  (start-client {:name "poker.cygni.se" :port 4711}))
