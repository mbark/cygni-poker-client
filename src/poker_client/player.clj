(ns poker-client.player)

(defprotocol IPlayer
  (bot-name [_])
  (action-request [this request])
  (register-for-play-response [this event]))
