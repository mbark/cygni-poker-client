(ns poker-client.router
  (:require [clojure.tools.logging :refer [info]]
            [camel-snake-kebab :refer [->kebab-case]]
            [poker-client.player :refer :all]
            [poker-client.event-listener :refer :all]))

(defn- matching-fn [^String nm]
  (ns-resolve *ns* (symbol nm)))

(defn- event-listener-in [fn-namespace event]
  (matching-fn
   (str "poker-client." fn-namespace "/" (->kebab-case (:type event)))))

(defn route [event bot]
  (info (str "Searching for matching function for event " (:type event)))
  (if-let [fun (or
                (event-listener-in "player" event)
                (event-listener-in "event-listener" event))]
    (fun bot event)))
