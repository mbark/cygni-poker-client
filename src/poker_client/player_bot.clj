(ns poker-client.player-bot
  (:use [clojure.tools.logging :as log]
        [poker-client responses]))

(defprotocol PlayerBot
  (bot-name [_])
  (action-request [this request]))

(defrecord LoggingBot []
  PlayerBot
  (bot-name [_] "clojure-client")
  (action-request
   [this request]
   (info (str "Request: " request))
   (->Action (:request-id request))))
