(ns minlokalebutik.shopgun
  (:require [clj-http.client :as client])
  (:require [clojure.set :as set])
  (:require [cheshire.core :refer :all])
  (:require [clojure.string :as str])
  (:require [pandect.algo.sha256 :refer :all])
  (:require [clojure.pprint :refer [pprint print-table]])
  (:require [minlokalebutik.time :refer :all]))





(def api-key "00ix2uliu8y2tobx8rowrshxwtr03g2b")
(def api-secret "00ix2uliu8w70qfdrafwhe4f1vukngas")
(def api-path "https://api.etilbudsavis.dk/v2")
(def page-size 100)
(def locations {:dragør {:r_lat 55.591173 :r_lng 12.658146 :r_radius 10000}})




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
               :debug        false
               :query-params (merge query {:order_by "-created"})}
        url (str api-path "/" endpoint)]
    (client/get url parms)))


(defn extract-store [m]
  (let [
        {{country :id} :country
         {name :name}  :branding
         :keys         [city street longitude latitude zip_code id]} m]

    {:id   id :navn name
     :adresse (str  street "," zip_code " " city " " country)
     :position {:lng longitude :lat latitude}
     :postnummer zip_code}))


(defn extract-offer [m]
  (let [
        {{price    :price
          preprice :pre_price} :pricing
         {billede :view}       :images
         :keys                 [heading description run_from run_till store_id publish id]} m]

    {:id       id :kort heading :lang description :pris price :forpris preprice :slut run_till :start run_from
     :store_id store_id :billede1 billede :publish publish}))


(defn paging [offset query]
  (if-not (nil? offset)
    (merge {:offset offset
            :limit  page-size} query)
    query))

(defn get-for-city
  [endpoint city query offset]
  (-> (doget endpoint (merge query (paging offset (locations city))))
      (:body)
      (parse-string true)))

(defn until-empty [f]
  (loop [
         offset 0
         l (f offset)
         r []]
    (println "loading " (count l) " items")
    (println "has loaded " (count r) " items")
    (if (> (count l) 0)
      (recur (+ offset page-size)
             (f offset)
             (concat r l))
      r)))

(defn get-all [endpoint offset]
  (map extract-offer (mapcat #(get-for-city "offers" % {} offset) (keys locations))))

(defn get-store [id]
  (extract-store (first (parse-string (:body (doget "stores" {:store_id id})) true))))

(def get-store-memoized
  (memoize get-store))

(defn add-store [offer]
  (assoc offer :butik (get-store-memoized (:store_id offer))))


(defn load-offers_after [limit-date offset]
  (filter #(after? (parse (:publish %)) limit-date ) (map add-store (get-all "offers" offset))))


(defn load-all-after [date]
  (set (until-empty (partial load-offers_after date))))

(defn load-all-today []
  (load-all-after (today)))





