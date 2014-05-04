(ns poker-client.router
  (:require [clojure.tools.logging :refer [info]]
            [camel-snake-kebab :refer [->kebab-case]]
            [poker-client.player :refer :all]))

(defn- find-fn-by-name [^String nm]
  (ns-resolve *ns* (symbol nm)))

(defn route [event bot]
  (info (str "Searching for matching function for event " (:type event)))
  (let [full-name (str "poker-client.player/" (->kebab-case (:type event)))]
    (if-let [fun (find-fn-by-name full-name)]
      (fun bot event))))
