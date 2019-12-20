(ns shopgun.call-url
  (:require [clj-http.client :as client])
  (:require [clojure.set :as set])
  (:require [cheshire.core :refer :all])
  (:require [clojure.string :as str])
  (:require [pandect.algo.sha256 :refer :all]))


(def api-key "00ix2uliu8y2tobx8rowrshxwtr03g2b")
(def api-secret "00ix2uliu8w70qfdrafwhe4f1vukngas")
(def api-path "https://api.etilbudsavis.dk/v2")

(def locations {:dragør {:lat 55.591173 :long 12.658146 :rad 1000}})

(defn filter-query [location]
  (when (seq location)
    (apply format (concat ["r_lat=%s&r_lng=%s&r_radius=%s"] (vals location)))))


(defn make-signature [token]
  (sha256 (str api-secret token)))


(defn dopost [endpoint form]
  (let [
        url (str api-path "/" endpoint)
        parms {:form-params  form
               :content-type :json}]

    (client/post url parms)))


(defn doget [url form]
  (let [
        parms {:form-params  form
               :content-type :json}]
    (println "calling " url)
    (client/get url parms)))

(defn token []
  (->
    (dopost "sessions" {:api_key api-key})
    (select-keys [:body])
    (:body)
    (parse-string true)
    (:token)))

(defn init []
  "initialise a map with secret and token"
  (let [
        tok (token)
        sig (make-signature tok)]

    (assoc {} :_token tok :_signature sig)))

(defn extract-store [m]
  (let [
        {{country :id} :country
         {name :name}  :branding
         :keys         [city street longitude latitude zip_code id]} m]

    {:id   id :name name :country country :zip zip_code
     :city city :street street :long longitude :lat latitude}))

(defn url [endpoint city]
  (str api-path "/" endpoint "?" (filter-query (city locations))))

(defn paging [url offset limit]
    (str url "&" "offset=" offset "&limit=" limit))

(defn get-complete
  [endpoint city limit offset]
  (let [form (init)
        url (paging (url endpoint city) offset limit)]
      (-> (doget url form)
          (:body)
          (parse-string true))))

(defn get-all [endpoint city]
  (let [size 24
        load (partial get-complete endpoint city size)]
    (loop [
           offset 0
           l (load offset)
           r []]
      (println (count r))
      (if (= (count l) size)
        (recur (+ offset size)
               (load offset)
               (concat r l))
        r))))



