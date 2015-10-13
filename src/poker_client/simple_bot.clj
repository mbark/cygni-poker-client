(ns poker-client.simple-bot
  (:require [clojure.tools.logging :refer [info debug]]
            [poker-client.player :refer [IPlayer]]
            [poker-client.event-listener :refer [IEventListener]]
            [poker-client.client :as client :refer [start]]
            [poker-client.board-state :refer [current-board]]
            [poker-client.util :refer :all]
            [camel-snake-kebab :refer [->kebab-case-keyword]]))

(def bot-name "clojure-client")

(declare ->SimpleBot)

(defn -main []
  (client/start {:name "poker.cygni.se" :port 4711} (->SimpleBot)))

(defn- actions->map [actions]
  (into {}
        (map
         (fn [action] [(->kebab-case-keyword (:action-type action)) action])
         actions)))

(defn- i-am-small-blind? [board]
  (=
   bot-name
   (:name (:small-blind-player board))))

(defn- play [board request]
  (let [cards [(:community-cards board) (:player-cards board)]
        hand (best-hand cards)
        rank (hand-rank (eval-hand hand))
        actions (actions->map (:possible-actions request))]
    ((cond
      (and (> rank (hand-rank :three-of-a-kind))
           (contains? actions :all-in))
      :all-in
      (contains? actions :check)
      :check
      (and (> rank (hand-rank :pair))
           (contains? actions :call))
      :call
      (and (> rank (hand-rank :two-pair))
           (contains? actions :raise))
      :raise
      (and (i-am-small-blind? board)
           (= (:turn board) "PRE_FLOP"))
      :call
      :else :fold) actions)))

(defrecord SimpleBot []
  IPlayer IEventListener
  (bot-name [_] bot-name)
  (play
   [this request]
   (debug (str "Bot received request " request))
   (debug (str "Current board " (current-board)))
   (let [response (play (current-board) request)]
     (info (str "Responding with " response " to " request))
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
