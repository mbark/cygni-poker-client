(defproject poker-client "1.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main poker-client.simple-bot
  :aot [poker-client.simple-bot]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [camel-snake-kebab "0.1.5"]
                 [org.clojure/math.combinatorics "0.0.8"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jdmk/jmxtools
                                                    com.sun.jmx/jmxri]]])
