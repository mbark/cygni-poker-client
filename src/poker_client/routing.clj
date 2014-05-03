(ns poker-client.routing
  (:require [clojure.tools.logging :refer [info]]
          [camel-snake-kebab :refer [->kebab-case]]
          [poker-client.player :refer :all]))

(defn- find-fn-by-name [^String nm]
  (ns-resolve *ns* (symbol nm)))

(defn route [event bot]
  (let [full-name (str "poker-client.player/" (->kebab-case (:type event)))]
    (if-let [fun (find-fn-by-name full-name)]
      (do
        (info (str "Found " fun " for event " (:type event)))
        (fun bot event))
      (info "No function found " full-name))))
