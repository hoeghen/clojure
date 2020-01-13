(ns minlokalebutik.firebase
  (:require [clj-http.client :as client])
  (:require [clojure.set :as set])
  (:require [cheshire.core :refer :all])
  (:require [clojure.string :as str])
  (:require [pandect.algo.sha256 :refer :all])
  (:require [clojure.pprint :refer [pprint print-table]])
  (:require [minlokalebutik.time :refer :all]
            [minlokalebutik.time :as time]))


(defn postTilbud [tilbud]
  (client/post "https://minlokalebutik.firebaseio.com/alletilbud.json" {:form-params tilbud
                                                                        :content-type :json}))

(defn load-tilbud [parms]
  (parse-string (:body (client/get (str "https://minlokalebutik.firebaseio.com/alletilbud.json" ) {:query-params parms
                                                                                                   :debug false}))))

(defn load-tilbud-before [date]
  (let [earlier {:orderBy "\"slut\""
                  :endAt (str "\"" date "\"")}]
    (load-tilbud earlier)))

(defn load-last-published-offer
  "henter det seneste tilbud fra firebase"
  []
  (let [newest {:orderBy "\"publish:\"" :limitToLast "1"}]
    (time/parseDateTimeString (get (val (first (load-tilbud newest))) "publish"))))


(defn delete-tilbud [key]
  (println "deleting item "  key)
  (client/delete (str "https://minlokalebutik.firebaseio.com/alletilbud/" key ".json") {:debug false}))



(defn delete-tilbud-before [date]
  (let [k (keys (load-tilbud-before date))
        c (count k)]
    (println "deleting " c " tilbud that ends on " date  " from firebase")
    (dorun (map delete-tilbud k))))

















