(ns poker-client.board-state
  (:require [clojure.tools.logging :refer [info]]
            [poker-client.event-listener :refer [IEventListener]]))

(def ^:private board-state (atom {}))

(defn current-board []
  @board-state)

(defn- find-first [coll f]
  (first (filter f coll)))

(defn player [board player-name]
  (find-first (:players board)
              #(= (:name %) player-name)))

(defn all-in? [board player-name]
  (let [player (find-first (:players board)
                           #(= (:name %) player-name))]
    (zero? (:chip-count player))))

(defn has-role? [board player-name role]
  (=
   (role board)
   (player board player-name)))

(defn reset-board []
  (reset! board-state
          {:turn nil
           :players []
           :currently-playing []
           :small-blind 0
           :big-blind 0
           :dealer nil
           :small-blind-player nil
           :big-blind-player nil
           :player-cards []
           :community-cards []
           :pot 0}))

(defn remove-player [players player-name]
  (remove #(= (:name %) player-name) players))

(defn replace-player [players new-player]
  (conj (remove-player players (:name new-player))
        new-player))

(defn update-player [player]
  (swap!
   board-state
   (fn [m]
     (update-in m
                [:players]
                #(replace-player % player)))))

(defn update-pot [event amount-key]
  (update-player (:player event))
  (swap!
   board-state
   (fn [m]
     (update-in m
                [:pot]
                #(+ % (amount-key event))))))

(defn player-folded [player]
  (swap!
   board-state
   (fn [m]
     (update-in m
                [:currently-playing]
                #(remove-player % (:name player))))))

(defrecord BoardUpdater []
  IEventListener
  (register-for-play-response [this event])
  (play-is-started-event
   [this event]
   (reset-board)
   (swap!
    board-state
    #(assoc
       %
       :players (:players event)
       :currently-playing (:players event)
       :small-blind (:small-blind-amount event)
       :big-blind (:big-blind-amount event)
       :dealer (:dealer event)
       :small-blind-player (:small-blind-player event)
       :big-blind-player (:big-blind-player event))))
  (community-has-been-dealt-a-card-event
   [this event]
   (swap!
    board-state
    (fn [m]
      (update-in m
                 [:community-cards]
                 #(conj % (:card event))))))
  (you-have-been-dealt-a-card-event
   [this event]
   (swap!
    board-state
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
    board-state
    #(assoc % :turn (:state event))))
  (table-is-done-event
   [this event])
  (player-checked-event [this event])
  (player-folded-event
   [this event]
   (player-folded (:player event)))
  (player-forced-folded-event
   [this event]
   (player-folded (:player event)))
  (player-quit-event [this event])
  (server-is-shutting-down-event [this event])
  (you-won-amount-event
   [this event]
   (swap!
    board-state
    #(assoc % :player-chips (:your-chip-amount event)))))
