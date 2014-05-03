(ns poker-client.player-bot
  (:require [clojure.tools.logging :refer [info]]
          [poker-client.responses :refer [->Action]]))

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
