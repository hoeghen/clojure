(ns minlokalebutik.main
  (:gen-class)
  (:require [minlokalebutik.shopgun :refer :all])
  (:require [minlokalebutik.firebase :refer :all])
  (:require [minlokalebutik.time :refer :all]))


(defn -main
  [& args]
  (println "calling shopgun and returning all offers in a postcode")
  ;; load tilbud fra idag
  (map postTilbud (load-all-today))

  (delete-tilbud-before (.toString (today))))




