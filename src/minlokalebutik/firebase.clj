(ns minlokalebutik.firebase
  (:require [clj-http.client :as client])
  (:require [clojure.set :as set])
  (:require [cheshire.core :refer :all])
  (:require [clojure.string :as str])
  (:require [pandect.algo.sha256 :refer :all])
  (:require [clojure.pprint :refer [pprint print-table]]))

(defn postTilbud [tilbud]
  (client/post "https://minlokalebutik.firebaseio.com/alletilbud.json" {:form-params tilbud
                                                                        :content-type :json}))

(defn load-tilbud [parms]
  (parse-string (:body (client/get (str "https://minlokalebutik.firebaseio.com/alletilbud.json") {
                                                                                                  :debug true}))))














