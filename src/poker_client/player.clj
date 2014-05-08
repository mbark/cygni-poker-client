(ns poker-client.player)

(defprotocol IPlayer
  (bot-name [_])
  (play [this request]))
