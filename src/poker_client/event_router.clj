(ns poker-client.event-router
  (:require [clojure.tools.logging :refer [info error debug]]
            [poker-client.player :refer :all]
            [poker-client.event-listener :refer :all]))

(def listener-ns "poker-client.event-listener")

(defn- matching-fn [^String nm]
  (ns-resolve *ns* (symbol nm)))

(defn- route-to-fn [event bot]
  (debug (str "Searching for matching function for event " (:type event)))
  (let [fn-name (str listener-ns "/" (:type event))]
    (if-let [fun (matching-fn fn-name)]
      (fun bot event)
      (error (str "Unable to find fn " fn-name)))))

(defn route [event bot board-updater]
  (route-to-fn event board-updater)
  (route-to-fn event bot))
