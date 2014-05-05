(ns poker-client.router
  (:require [clojure.tools.logging :refer [info]]
            [camel-snake-kebab :refer [->kebab-case]]
            [poker-client.player :refer :all]
            [poker-client.event-listener :refer :all]))

(defn- matching-fn [^String nm]
  (ns-resolve *ns* (symbol nm)))

(defn- route-event [event bot]
  (info (str "Searching for matching function for event " (:type event)))
  (if-let [fun
           (matching-fn (str "poker.client.event-listener/"
                             (->kebab-case (:type event))))]
    (fun bot event)))

(defn request? [event]
  (= (:type event) "ActionRequest"))

(defn route [event bot]
  (if (request? event)
    (action-response bot event)
  (route-event event bot)))
