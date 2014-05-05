(ns poker-client.client
  (:require [clojure.tools.logging :refer [info error]]
            [poker-client.socket :refer [connect send-msg read-msg]]
            [poker-client.router :refer [route]]
            [poker-client.player :refer [bot-name action-response]]
            [poker-client.message :refer [->RegisterForPlay ->ActionResponse]]
            [poker-client.event :refer [msg->event-map]]))

(defn- exit [conn]
  (info "Shutting down")
  (System/exit 0))

(defn- exception? [event]
  (= "UsernameAlreadyTakenException" (:type event)))

(defn- invalid? [event]
  (if(exception? event)
    (do (error (:message event))
      true)
    false))

(defn- handle-event [event conn bot]
  (info (str "Received event from server " event))
  (if (invalid? event)
    (exit conn))
  (if-let [response (route event bot)]
    (send-msg
     conn
     (->ActionResponse (:request-id event)
                       (action-response bot event)))))

(defn- handle-events [conn bot]
  (if-let [msg (read-msg conn)]
    (do
      (handle-event (msg->event-map msg) conn bot)
      (handle-events conn bot))
    (exit conn)))

(defn start [server bot]
  (let [conn (connect server)]
    (send-msg conn
              (->RegisterForPlay (bot-name bot)))
    (doto (Thread. #(handle-events conn bot)) (.start))))
