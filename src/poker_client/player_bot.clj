(ns poker-client.player-bot
  (:use [clojure.tools.logging :as log]))

(defprotocol PlayerBot
  (bot-name [_])
  (action-request [this request]))

(defrecord LoggingBot []
  PlayerBot
  (bot-name [_] "clojure-client")
  (action-request
   [this request]
   (info "Request to act")))
