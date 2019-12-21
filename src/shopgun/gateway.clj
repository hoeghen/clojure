(ns shopgun.gateway
  (:require [clj-http.client :as client])
  (:require [clojure.set :as set])
  (:require [cheshire.core :refer :all])
  (:require [clojure.string :as str])
  (:require [pandect.algo.sha256 :refer :all])
  (:require [clojure.pprint :refer [pprint print-table]])
  (:import java.time.LocalDateTime)
  (:import java.time.LocalDate)
  (:import java.time.format.DateTimeFormatter))




(def api-key "00ix2uliu8y2tobx8rowrshxwtr03g2b")
(def api-secret "00ix2uliu8w70qfdrafwhe4f1vukngas")
(def api-path "https://api.etilbudsavis.dk/v2")
(def page-size 10)
(def locations {:dragør {:r_lat 55.591173 :r_lng 12.658146 :r_radius 10000}})


(defn to-date [string]
  (.toLocalDate (LocalDateTime/parse string (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ssZ"))))
(defn to-day []
  (LocalDate/now))


(defn make-signature [token]
  (sha256 (str api-secret token)))


(defn dopost [endpoint form]
  (let [
        url (str api-path "/" endpoint)
        parms {:form-params  form
               :content-type :json}]
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



(defn doget [endpoint query]
  (let [
        parms {:form-params  (init)
               :content-type :json
               :debug false
               :query-params (merge query {:order_by "-created"})}
        url (str api-path "/" endpoint)]
    (client/get url parms)))


(defn extract-store [m]
  (let [
        {{country :id} :country
         {name :name}  :branding
         :keys         [city street longitude latitude zip_code id]} m]

    {:id   id :name name :country country :zip zip_code
     :city city :street street :long longitude :lat latitude}))

(defn extract-offer [m]
  (let [
        {{price :price
          preprice :pre_price}  :pricing
         {billede :view} :images
         :keys         [heading description run_from run_till store_id publish id]}   m]

    {:id id :kort heading :lang description :pris price :forpris preprice :slut run_till :start run_from
     :store_id store_id :billede1 billede :publish (to-date publish)}))


(defn paging [offset query]
  (if-not (nil? offset)
    (merge {:offset offset
            :limit page-size} query)
    query))

(defn get-for-city
  [endpoint city query offset]
  (-> (doget endpoint (merge query (paging offset (locations city))))
      (:body)
      (parse-string true)))

(defn get-all-for-city [endpoint city]
  (let [size page-size
        load (partial get-for-city endpoint city {})]
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

(defn get-all [endpoint offset]
  (map extract-offer (mapcat #(get-for-city "offers" % {} offset) (keys locations))))

(defn get-store [id]
  (extract-store(first (parse-string (:body (doget "stores" {:store_id id})) true))))

(defn add-store [offer]
  (assoc offer :butik (get-store (:store_id offer))))


(defn load-offers [offset]
  (map add-store (get-all "offers" offset)))



