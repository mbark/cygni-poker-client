(ns poker-client.router
  (:require [clojure.tools.logging :refer [info error debug]]
            [camel-snake-kebab :refer [->kebab-case]]
            [poker-client.player :refer :all]
            [poker-client.event-listener :refer :all]))

(def listener-ns "poker-client.event-listener")

(defn- matching-fn [^String nm]
  (ns-resolve *ns* (symbol nm)))

(defn- route-event [event bot]
  (debug (str "Searching for matching function for event " (:type event)))
  (let [fn-name (str listener-ns "/" (->kebab-case (:type event)))]
    (if-let [fun (matching-fn fn-name)]
      (fun bot event)
      (error (str "Unable to find fn " fn-name)))))

(defn request? [event]
  (= (:type event) "ActionRequest"))

(defn route [event bot board-updater]
  (if (request? event)
    (action-response bot event)
    (do
      (route-event event board-updater)
      (route-event event bot)
      nil)))
