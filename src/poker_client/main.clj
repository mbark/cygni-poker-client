(ns poker-client.main
  (:gen-class)
  (:use [clojure.tools.logging :as log]
        [poker-client socket]))

(def poker-bot-name "clojure-client")

(def register-for-play
  {:type "se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"
   :sessionId ""
   :name poker-bot-name
   :room "TRAINING"})

(defn- done? [response]
  (or
   (empty? response)
   (= "se.cygni.texasholdem.communication.message.exception.UsernameAlreadyTakenException" (response :type))))

(defn- json-map->clj-map [json]
  (into
   {}
   (for [[k v] json]
     [(keyword k)
      (cond
       (vector? v) (map json-map->clj-map v)
       (map? v) (json-map->clj-map v)
       :else v)])))

(defn- event-handler [conn]
  (while (nil? (:exit @conn))
    (let [event (json-map->clj-map (next-event conn))]
      (info (str "Received event " event))
      (if (done? event)
        (dosync (alter conn merge {:exit true}))))))

(defn start-client [server]
  (let [conn (connect server)]
    (respond conn register-for-play)
    (doto (Thread. #(event-handler conn)) (.start))))

(defn -main []
  (start-client {:name "poker.cygni.se" :port 4711}))
