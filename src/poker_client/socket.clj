(ns poker-client.socket
  (:import [java.net Socket]
           [java.io PrintWriter InputStreamReader BufferedReader EOFException])
  (:require [clojure.tools.logging :refer [info error debug]]))

(def json-delimiter "_-^emil^-_")

(defn- write [conn msg]
  (debug (str "Writing " msg))
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
       (if (>= character 0)
         (read-till-delimiter
           in
           delimiter
           (str input (char character))))))))

(defn send-msg [conn msg]
  (write
    conn
    (str msg json-delimiter)))

(defn read-msg [conn]
  (try
    (read-till-delimiter (:in @conn) json-delimiter)
    (catch EOFException e
      (info "End of file encountered"))
    (catch Exception e
      (error e "Exception when reading from socket"))))

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
        out (PrintWriter. (.getOutputStream socket))
        conn (ref {:in in :out out})]
    conn))
