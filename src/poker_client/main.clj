(ns poker-client.main
  ;;  (:gen-class)
  (:import (java.net Socket)
           (java.io PrintWriter InputStreamReader BufferedReader))
  (:require [clojure.data.json :as json])
  (:use [clojure.tools.logging :as log]))

(defn -main []
  (connect {:name "poker.cygni.se" :port 4711}))

(def json-delimiter "_-^emil^-_")

(defn write [conn msg]
  (doto (:out @conn)
    (.println (str msg "\r"))
    (.flush)))

(defn strip-end [s word]
  (.substring s 0 (- (count s) (count word))))

(defn read-till-delimiter
  ([in delimiter] (read-till-delimiter in delimiter ""))
  ([in delimiter input]
   (if (.endsWith input delimiter)
     (strip-end input delimiter)
     (let [character (.read in)]
       (if (< character 0)
         input
         (read-till-delimiter
          in
          delimiter
          (str input (char character))))))))

(def register-for-play
  (str
   (json/write-str
    {:type "se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"
     :sessionId ""
     :name "clojure-test"
     :room "TRAINING"})
   json-delimiter))

(defn conn-handler [conn]
  (do
    (write conn register-for-play)
    (println (str "Response " (read-till-delimiter
                             (:in @conn)
                             json-delimiter)))))

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
        out (PrintWriter. (.getOutputStream socket))
        conn (ref {:in in :out out})]
    (doto (Thread. #(conn-handler conn)) (.start))
    conn))
