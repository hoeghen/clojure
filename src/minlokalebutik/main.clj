(ns minlokalebutik.main
  (:gen-class)
  (:require [minlokalebutik.shopgun :refer :all])
  (:require [minlokalebutik.firebase :refer :all])
  (:require [minlokalebutik.time :refer :all]
            [minlokalebutik.time :as time]))


(defn -main
  [& args]
  (println "loading....")
  ;; load tilbud fra idag
  (dorun (map postTilbud (load-all-today)))
  (println "deleting....")
  (delete-tilbud-before (.toString (today)))
  (println "done"))


