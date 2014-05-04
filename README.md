# poker-client

A client to handle the connection to Cygni's [Texas hold'em Botgame](https://github.com/emilb/texas-holdem-botgame).

## Using the client

To run it you need [Leiningen](http://leiningen.org/).

    #download all depndencies
    lein deps

    #run the project
    lein run

This will start the client, and you will see log output.

## Writing your bot

The example bot used is [simple-bot](https://github.com/mbark/cygni-poker-client/blob/master/src/poker_client/simple_bot.clj). To write your own bot you need to to change the main class in [project.clj](https://github.com/mbark/cygni-poker-client/blob/master/project.clj)

```clojure
(defproject poker-client "0.1.0-SNAPSHOT"
;; bunch of stuff here
  :main poker-client.simple-bot ;; set to your own bot
;; more stuff here
```

Then in your bot you just add a main function

```clojure
(defn -main []
  (client/start {:name "poker.cygni.se" :port 4711} (->SimpleBot)))
```

Change port or name as necessary.
