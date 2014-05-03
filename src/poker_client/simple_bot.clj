(ns poker-client.simple-bot
  (:require [clojure.tools.logging :refer [info]]
            [poker-client.player :refer [IPlayer]]
            [poker-client.main :refer [start-client]]))

(declare ->SimpleBot)

(defn -main []
  (start-client {:name "poker.cygni.se" :port 4711} (->SimpleBot)))

(defrecord SimpleBot []
  IPlayer
  (bot-name [_] "clojure-client")
  (action-request
   [this request]
   (info (str "Request: " request))
   {"actionType" "FOLD" "amount" 0})
  (register-for-play-response
   [this event])
  (play-is-started-event
   [this event])
  (community-has-been-dealt-a-card-event
   [this event]
   (info (str "Bot received event " event)))
  (player-bet-big-blind-event
   [this event]
   (info (str "Bot received event " event)))
  (player-bet-small-blind-event
   [this event]
   (info (str "Bot received event " event)))
  (player-called-event
   [this event]
   (info (str "Bot received event " event)))
  (player-checked-event
   [this event]
   (info (str "Bot received event " event)))
  (player-folded-event
   [this event]
   (info (str "Bot received event " event)))
  (player-forced-folded-event
   [this event]
   (info (str "Bot received event " event)))
  (player-quit-event
   [this event]
   (info (str "Bot received event " event)))
  (player-raised-event
   [this event]
   (info (str "Bot received event " event)))
  (player-went-all-in-event
   [this event]
   (info (str "Bot received event " event)))
  (server-is-shutting-down-event
   [this event]
   (info (str "Bot received event " event)))
  (show-down-event
   [this event]
   (info (str "Bot received event " event)))
  (table-changed-state-event
   [this event]
   (info (str "Bot received event " event)))
  (table-is-done-event
   [this event]
   (info (str "Bot received event " event)))
  (you-have-been-dealt-a-card-event
   [this event]
   (info (str "Bot received event " event)))
  (you-won-amount-event
   [this event]
   (info (str "Bot received event " event))))
