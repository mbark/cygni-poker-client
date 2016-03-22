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
  (client/start {:name "localhost" :port 4711} (->SimpleBot)))

(defn- actions->keyword-map [actions]
  (into {}
        (map
         (fn [action] [(->kebab-case-keyword (:action-type action)) action])
         actions)))

(defn- am-i-small-blind? [board]
  (=
   bot-name
   (:name (:small-blind-player board))))

(defn- play [board request]
  (let [cards [(:community-cards board) (:player-cards board)]
        hand (best-hand cards)
        rank (hand-rank (eval-hand hand))
        actions (actions->keyword-map (:possible-actions request))
        better-than? (fn [hand-name] (> rank (hand-rank hand-name)))
        can-do? (fn [action] (contains? actions action))]
    ((cond
      (and (better-than? :three-of-a-kind) (can-do? :all-in))
      :all-in
      (can-do? :check)
      :check
      (and (better-than? :pair) (can-do? :call))
      :call
      (and (better-than? :two-pair) (can-do? :raise))
      :raise
      (and (am-i-small-blind? board) (= (:turn board) "PRE_FLOP"))
      :call
      :else :fold) actions)))

(defrecord SimpleBot []
  IPlayer IEventListener
  (bot-name [_] bot-name)
  (play
   [this request]
   (debug "Bot received request " request)
   (debug "Current board " (current-board))
   (let [response (play (current-board) request)]
     (info "Responding with " response " to " request)
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
