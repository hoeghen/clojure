(ns minlokalebutik.shopgun
  (:require [clojure.set :as set])
  (:require [clojure.string :as str])
  (:require [clojure.pprint :refer [pprint print-table]])
  (:require [minlokalebutik.time :as time])
  (:require [minlokalebutik.shopgun-clj :refer :all]))

(def page-size 100)
(def locations {:dragør {:r_lat 55.591173 :r_lng 12.658146 :r_radius 10000}})

(defn extract-publish [s]
  (sort (map #(first (vals (select-keys % [:publish])))
             s)))



(defn extract-offer [m]
  (let [
        {{price    :price
          preprice :pre_price} :pricing
         {billede :view}       :images
         :keys                 [heading description run_from run_till store_id publish id]} m]

    {:id       id :kort heading :lang description :pris price :forpris preprice :slut run_till :start run_from
     :store_id store_id :billede1 billede :publish publish}))


(defn extract-store [m]
  (let [
        {{country :id} :country
         {name :name}  :branding
         :keys         [city street longitude latitude zip_code id]} m]

    {:id         id :navn name
     :adresse    (str street "," zip_code " " city " " country)
     :position   {:lng longitude :lat latitude}
     :postnummer zip_code}))


(defn paging [offset query]
  (if-not (nil? offset)
    (merge {:offset offset
            :limit  page-size} query)
    query))

(defn get-for-city
  [endpoint city query offset]
  (-> (doget endpoint (merge query (paging offset (locations city))))
      (:body)
      (parse)))

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
  (map extract-offer (mapcat #(get-for-city endpoint % {} offset) (keys locations))))

(defn get-store [id]
  (extract-store (first (parse(:body (doget "stores" {:store_id id}))))))

(def get-store-memoized
  (memoize get-store))

(defn add-store [offer]
  (assoc offer :butik (get-store-memoized (:store_id offer))))


(defn load-offers_after [limit-date offset]
  (filter #(time/after? (time/parseDateTimeString (:publish %)) limit-date) (map add-store (get-all "offers" offset))))


(defn load-all-after [date]
  (set (until-empty (partial load-offers_after date))))

(defn load-all-today []
  (load-all-after (time/today)))





