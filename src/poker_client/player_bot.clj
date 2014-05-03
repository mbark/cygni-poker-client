(ns poker-client.player-bot
  (:require [clojure.tools.logging :refer [info]]))

(defprotocol PlayerBot
  (bot-name [_])
  (action-request [this request])
  (register-for-play-response [this event])
  (play-is-started-event [this event])
  (community-has-been-dealt-a-card-event [this event])
  (player-bet-big-blind-event [this event])
  (player-bet-small-blind-event [this event])
  (player-called-event [this event])
  (player-checked-event [this event])
  (player-folded-event [this event])
  (player-forced-folded-event [this event])
  (player-quit-event [this event])
  (player-raised-event [this event])
  (player-went-all-in-event [this event])
  (server-is-shutting-down-event [this event])
  (show-down-event [this event])
  (table-changed-state-event [this event])
  (table-is-done-event [this event])
  (you-have-been-dealt-a-card-event [this event])
  (you-won-amount-event [this event]))

(defrecord LoggingBot []
  PlayerBot
  (bot-name [_] "clojure-client")
  (action-request
   [this request]
   (info (str "Request: " request))
   {"actionType" "FOLD" "amount" 0})
  (register-for-play-response
   [this event]
   (info (str "Bot received event " event)))
  (play-is-started-event
   [this event]
   (info (str "Bot received event " event)))
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
