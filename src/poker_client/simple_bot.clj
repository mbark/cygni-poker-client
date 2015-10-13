(ns poker-client.simple-bot
  (:require [clojure.tools.logging :refer [info debug]]
            [poker-client.player :refer [IPlayer]]
            [poker-client.event-listener :refer [IEventListener]]
            [poker-client.client :as client :refer [start]]
            [poker-client.board-state :refer [current-board]]))

(declare ->SimpleBot)

(defn -main []
  (client/start {:name "poker.cygni.se" :port 4711} (->SimpleBot)))

(defrecord SimpleBot []
  IPlayer IEventListener
  (bot-name [_] "clojure-client")
  (play
   [this request]
   (debug (str "Bot received request " request))
   (debug (str "Current board " (current-board)))
   (let [response (first (:possible-actions request))]
    (info (str "Responding with " response))
    response))
  (register-for-play-response
   [this event])
  (play-is-started-event
   [this event])
  (community-has-been-dealt-a-card-event
   [this event]
   (debug (str "Bot received event " event)))
  (player-bet-big-blind-event
   [this event]
   (debug (str "Bot received event " event)))
  (player-bet-small-blind-event
   [this event]
   (debug (str "Bot received event " event)))
  (player-called-event
   [this event]
   (debug (str "Bot received event " event)))
  (player-checked-event
   [this event]
   (debug (str "Bot received event " event)))
  (player-folded-event
   [this event]
   (debug (str "Bot received event " event)))
  (player-forced-folded-event
   [this event]
   (debug (str "Bot received event " event)))
  (player-quit-event
   [this event]
   (debug (str "Bot received event " event)))
  (player-raised-event
   [this event]
   (debug (str "Bot received event " event)))
  (player-went-all-in-event
   [this event]
   (debug (str "Bot received event " event)))
  (server-is-shutting-down-event
   [this event]
   (debug (str "Bot received event " event)))
  (show-down-event
   [this event]
   (debug (str "Bot received event " event)))
  (table-changed-state-event
   [this event]
   (debug (str "Bot received event " event)))
  (table-is-done-event
   [this event]
   (debug (str "Bot received event " event)))
  (you-have-been-dealt-a-card-event
   [this event]
   (debug (str "Bot received event " event)))
  (you-won-amount-event
   [this event]
   (debug (str "Bot received event " event))))
