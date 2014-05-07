(ns poker-client.event
  (:require [clojure.tools.logging :refer [info]]
            [camel-snake-kebab :refer [->kebab-case-keyword]]
            [clojure.data.json :as json :refer [read-str]]))

(defn- event-class [data]
  (last
   (clojure.string/split
    (:type data)
    #"\.")))

(defn msg->event-map [msg]
  (let [m (json/read-str msg :key-fn ->kebab-case-keyword)]
    (assoc m :type (event-class m))))
