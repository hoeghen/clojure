(ns minlokalebutik.shopgun-clj
  (:require [clj-http.client :as client])
  (:require [cheshire.core :refer :all])
  (:require [pandect.algo.sha256 :refer :all]))



(def api-key "00ix2uliu8y2tobx8rowrshxwtr03g2b")
(def api-secret "00ix2uliu8w70qfdrafwhe4f1vukngas")
(def api-path "https://api.etilbudsavis.dk/v2")


(defn make-signature [token]
  (sha256 (str api-secret token)))

(defn dopost [endpoint form]
  (let [
        url (str api-path "/" endpoint)
        parms {:form-params  form
               :content-type :json
               :debug false}]
    (client/post url parms)))

(defn get-token []
  (->
    (dopost "sessions" {:api_key api-key})
    (select-keys [:body])
    (:body)
    (parse-string true)
    (:token)))

(defn init []
  "initialise a map with secret and token"
  (let [
        tok (get-token)
        sig (make-signature tok)]

    (assoc {} :_token tok :_signature sig)))




(defn doget [ endpoint query]
  (let [
        parms {:form-params  (init)
               :content-type :json
               :debug        false
               :query-params (merge query {:order_by "-created"})}
        url (str api-path "/" endpoint)]
    (client/get url parms)))

(defn parse [str]
  (parse-string str true))






