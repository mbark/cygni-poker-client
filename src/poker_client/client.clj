(ns poker-client.client
  (:gen-class)
  (:require [clojure.tools.logging :refer [info]]
            [poker-client.socket :refer [connect send-msg read-msg]]
            [poker-client.routing :refer [route]]
            [poker-client.player :refer [bot-name]]
            [poker-client.message :refer [->RegisterForPlay ->ActionResponse]]
            [poker-client.event :refer [msg->map]]))

(defn- exit [conn]
  (info "Shutting down")
  (dosync (alter conn merge {:exit true})))

(defn- event-handler [conn bot]
  (try
    (while true
      (let [msg (read-msg conn)
            event (msg->map msg)]
        (info (str "Received event from server " event))
        (when-let [action (route event bot)]
          (send-msg conn (->ActionResponse (:request-id event) action)))))
      (catch Exception e (exit conn))))

(defn start [server bot]
  (let [conn (connect server)]
    (send-msg conn
              (->RegisterForPlay (bot-name bot)))
    (doto (Thread. #(event-handler conn bot)) (.start))))
