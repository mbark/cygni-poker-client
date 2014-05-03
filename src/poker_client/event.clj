(ns poker-client.event
  (:require [clojure.tools.logging :refer [info]]
            [camel-snake-kebab :refer [->kebab-case]]
            [clojure.data.json :as json :refer [read-str]]))

(defn- json-keys->keyword [json]
  (into
   {}
   (for [[k v] json]
     [(keyword (->kebab-case k))
      (cond
       (vector? v) (map json-keys->keyword v)
       (map? v) (json-keys->keyword v)
       :else v)])))

(defn- event-class [data]
  (last
   (clojure.string/split
    (:type data)
    #"\.")))

(defn- ->clj-map [json]
  (let [m (json-keys->keyword json)]
    (assoc m :type (event-class m))))

(defn msg->map [msg]
  (->clj-map (json/read-str msg)))


;;(defn- done? [response]
;;  (or
;;   (empty? response)
;;   (= "UsernameAlreadyTakenException" (response :type))))
