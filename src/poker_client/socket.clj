(ns poker-client.socket
  (:import [java.net Socket]
           [java.io PrintWriter InputStreamReader BufferedReader])
  (:require [clojure.data.json :as json :refer [read-str write-str]]
            [clojure.tools.logging :refer [info]]))

(def json-delimiter "_-^emil^-_")

(defn- write [conn msg]
  (info (str "Writing " msg))
  (doto (:out @conn)
    (.println (str msg "\r"))
    (.flush)))

(defn- strip-end [s word]
  (.substring s 0 (- (count s) (count word))))

(defn- read-till-delimiter
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

(defn send-msg [conn m]
  (write
   conn
   (str (json/write-str m) json-delimiter)))

(defn next-event [conn]
  (json/read-str
   (read-till-delimiter (:in @conn) json-delimiter)))

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
        out (PrintWriter. (.getOutputStream socket))
        conn (ref {:in in :out out})]
    conn))
