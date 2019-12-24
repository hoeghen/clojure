(ns minlokalebutik.main
  (:gen-class)
  (:require [minlokalebutik.shopgun :refer :all])
  (:require [minlokalebutik.firebase :refer :all]))


(defn -main
  [& args]
  (println "calling shopgun and returning all offers in a postcode")
  (map postTilbud (load-all-today)))


(-main)