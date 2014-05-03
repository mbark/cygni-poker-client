(ns poker-client.main
  (:gen-class)
  (:require [clojure.tools.logging :refer [info]]
            [camel-snake-kebab :refer [->kebab-case]]
            [poker-client.socket :refer [connect respond next-event]]
            [poker-client.routing :refer [route]]
            [poker-client.player-bot :refer [->LoggingBot bot-name]]
            [poker-client.responses :refer [->map ->RegisterForPlay]]))

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
            (respond conn (->map action))))))
    (catch Exception e (exit conn))))

(defn start-client [server bot]
  (let [conn (connect server)]
    (respond conn (->map (->RegisterForPlay (bot-name bot))))
    (doto (Thread. #(event-handler conn bot)) (.start))))

(defn -main []
  (start-client {:name "poker.cygni.se" :port 4711} (->LoggingBot)))
