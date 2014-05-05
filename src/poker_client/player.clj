(ns poker-client.player)

(defprotocol IPlayer
  (bot-name [_])
  (action-response [this request]))
