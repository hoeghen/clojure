(ns minlokalebutik.main
  (:gen-class
    :methods [^:static [handler [] void]])

  (:require [minlokalebutik.shopgun :refer :all])
  (:require [minlokalebutik.firebase :refer :all])
  (:require [minlokalebutik.time :refer :all])
  (:require [minlokalebutik.tasker :refer :all]))



(defn -handler
  []
  (println "get latest from firebase")
  (def latest-date (load-last-published-offer))
  (println "latest date is " latest-date)

  (println "loading....")

  (def concurency 20)

  (def ch (put-sequence-on-channel (load-all-after latest-date)))
  (def out (execute-from-channel concurency ch postTilbud))


  (println "deleting....")
  (delete-tilbud-before (.toString (today)))

  (wait-for-count concurency out)

  (println "done"))


(defn -main [& args]
  (println "starting")
  (-handler))

