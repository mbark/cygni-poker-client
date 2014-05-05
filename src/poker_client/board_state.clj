(ns poker-client.board-state
  (:require [clojure.tools.logging :refer [info]]
            [poker-client.event-listener :refer [IEventListener]]))

(def ^{:private true} board (atom {}))

(defn board-state []
  @board)

(defn reset-board []
  (reset! board
          {:turn nil
           :players []
           :small-blind 0
           :big-blind 0
           :dealer nil
           :small-blind-player nil
           :big-blind-player nil
           :player-cards []
           :community-cards []
           :pot 0
           :player-chips 0}))

(defn replace-player [players new-player]
  (let [without-old-player (remove #(= (:name new-player) (:name %)) players)]
    (conj without-old-player new-player)))

(defn update-player [player]
  (swap!
   board
   (fn [m]
     (update-in m
                [:players]
                #(replace-player % player)))))

(defn update-pot [event amount-key]
  (update-player (:player event))
  (swap!
   board
   (fn [m]
     (update-in m
                [:pot]
                #(+ % (amount-key event))))))

(defrecord BoardUpdater []
  IEventListener
  (register-for-play-response [this event])
  (play-is-started-event
   [this event]
   (reset-board)
   (swap!
    board
    #(assoc
       %
       :players (:players event)
       :small-blind (:small-blind-amount event)
       :big-blind (:big-blind-amount event)
       :dealer (:dealer event)
       :small-blind-player (:small-blind-player event)
       :big-blind-player (:big-blind-player event))))
  (community-has-been-dealt-a-card-event
   [this event]
   (swap!
    board
    (fn [m]
      (update-in m
                 [:community-cards]
                 #(conj % (:card event))))))
  (you-have-been-dealt-a-card-event
   [this event]
   (swap!
    board
    (fn [m]
      (update-in m
                 [:player-cards]
                 #(conj % (:card event))))))
  (player-bet-big-blind-event
   [this event]
   (update-pot event :big-blind))
  (player-bet-small-blind-event
   [this event]
   (update-pot event :small-blind))
  (player-called-event
   [this event]
   (update-pot event :call-bet))
  (player-raised-event
   [this event]
   (update-pot event :raise-bet))
  (player-went-all-in-event
   [this event]
   (update-pot event :all-in-amount))
  (show-down-event
   [this event])
  (table-changed-state-event
   [this event]
   (swap!
    board
    #(assoc % :turn (:state event))))
  (table-is-done-event
   [this event])
  (player-checked-event [this event])
  (player-folded-event [this event])
  (player-forced-folded-event [this event])
  (player-quit-event [this event])
  (server-is-shutting-down-event [this event])
  (you-won-amount-event
   [this event]
   (swap!
    board
    #(assoc % :player-chips (:your-chip-amount event)))))
