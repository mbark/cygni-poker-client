(ns poker-client.routing
  (:use [clojure.tools.logging :as log]
        [camel-snake-kebab]
        [poker-client player-bot]))

(defn- find-fn-by-name [^String nm]
  (ns-resolve *ns* (symbol nm)))

(defn route [event bot]
  (let [full-name (str "poker-client.player-bot/" (->kebab-case (:type event)))]
    (if-let [fun (find-fn-by-name full-name)]
      (do
        (info (str "Found " fun " for event " (:type event)))
        (fun bot event))
      (info "No function found " full-name))))
