(ns poker-client.event
  (:require [clojure.tools.logging :refer [info]]
            [camel-snake-kebab :refer [->kebab-case]]
            [clojure.data.json :as json :refer [read-str]]))

(defn- ->keywords [json]
  (into
   {}
   (for [[k v] json]
     [(keyword (->kebab-case k))
      (cond
       (vector? v) (map ->keywords v)
       (map? v) (->keywords v)
       :else v)])))

(defn- event-class [data]
  (last
   (clojure.string/split
    (:type data)
    #"\.")))

(defn- ->clj-map [json]
  (let [m (->keywords json)]
    (assoc m :type (event-class m))))

(defn msg->event-map [msg]
  (->clj-map (json/read-str msg)))
