(ns minlokalebutik.firebase
  (:require [clj-http.client :as client])
  (:require [clojure.set :as set])
  (:require [cheshire.core :refer :all])
  (:require [clojure.string :as str])
  (:require [pandect.algo.sha256 :refer :all])
  (:require [clojure.pprint :refer [pprint print-table]])
  (:require [minlokalebutik.time  :refer :all]))


(defn postTilbud [tilbud]
  (client/post "https://minlokalebutik.firebaseio.com/alletilbud.json" {:form-params tilbud
                                                                        :content-type :json}))

(defn load-tilbud [parms]
  (parse-string (:body (client/get (str "https://minlokalebutik.firebaseio.com/alletilbud.json" ) {:query-params parms
                                                                                                   :debug true}))))

(defn load-tilbud-before [date]
  (let [earlier {:orderBy "\"slut\""
                  :endAt (str "\"" date "\"")}]
    (load-tilbud earlier)))


(defn delete-tilbud [key]
  (client/delete (str "https://minlokalebutik.firebaseio.com/alletilbud/" key ".json") {:debug true}))



(defn delete-tilbud-before [date]
  (map delete-tilbud (keys (load-tilbud-before date))))












