(ns poker-client.client
  (:require [clojure.tools.logging :refer [info error debug]]
            [poker-client.socket :refer [connect send-msg read-msg]]
            [poker-client.event-router :refer [route]]
            [poker-client.player :refer :all]
            [poker-client.board-state :refer [->BoardUpdater]]
            [poker-client.message :refer [register-for-play
                                          action-response
                                          exception?
                                          request?
                                          msg->map]]))

(defn- exit [conn]
  (info "Shutting down")
  (System/exit 0))

(defn- invalid? [event]
  (if(exception? event)
    (do (error (:message event))
      true)
    false))

(defn- handle-event [event conn bot board-updater]
  (debug (str "Received event from server " event))
  (if (invalid? event)
    (exit conn))
  (if (request? event)
    (send-msg conn
              (action-response event (play bot event)))
    (route event bot board-updater)))

(defn- handle-events [conn bot board-updater]
  (while true
    (if-let [msg (read-msg conn)]
      (handle-event (msg->map msg) conn bot board-updater)
      (exit conn))))

(defn start [server bot]
  (let [conn (connect server)
        board-updater (->BoardUpdater)]
    (send-msg conn
              (register-for-play (bot-name bot)))
    (doto (Thread. #(handle-events conn bot board-updater)) (.start))))
