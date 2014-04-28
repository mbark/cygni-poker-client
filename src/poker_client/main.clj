(ns poker-client.main
  ;;  (:gen-class)
  (:import (java.net Socket)
           (java.io PrintWriter InputStreamReader BufferedReader))
  (:require [clojure.data.json :as json])
  (:use [clojure.tools.logging :as log]
        [clojure.set]))

(declare connect)

(defn -main []
  (connect {:name "poker.cygni.se" :port 4711}))

(def json-delimiter "_-^emil^-_")
(def poker-bot-name "clojure-client")

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
     :name poker-bot-name
     :room "TRAINING"})
   json-delimiter))

(defn is-done? [response]
  (or
   (empty? response)
   (= "se.cygni.texasholdem.communication.message.exception.UsernameAlreadyTakenException" (response :type))))

(defn keyify-actions [actions]
  (map
   (fn [action]
     (update-in
      (rename-keys action {"actionType" :action-type
                           "amount" :amount})
      [:action-type]
      #({"FOLD" :fold
         "CALL" :call
         "ALL_IN" :all-in
         "RAISE" :raise}
                %)))
   actions))

(defn parse-action-request [json]
  (let [keys-renamed (rename-keys json
                                  {"type" :type
                                   "version" :version
                                   "sessionId" :session-id
                                   "requestId" :request-id
                                   "possibleActions" :possible-actions})]
    (if (contains? keys-renamed :possible-actions)
      (update-in
       keys-renamed
       [:possible-actions]
       keyify-actions)
      keys-renamed)))

(defn json-map->clj-map [json]
  (case (json "type")
    "se.cygni.texasholdem.communication.message.request.ActionRequest" (parse-action-request json)
    json))

(defn conn-handler [conn]
  (do
    (write conn register-for-play)
    (while (nil? (:exit @conn))
      (let [raw-response (read-till-delimiter
                          (:in @conn)
                          json-delimiter)
            response (json-map->clj-map (json/read-str raw-response))]
        (info (str "Received response " response))
        (if (is-done? response)
          (dosync (alter conn merge {:exit true})))))))

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
        out (PrintWriter. (.getOutputStream socket))
        conn (ref {:in in :out out})]
    (doto (Thread. #(conn-handler conn)) (.start))
    conn))
