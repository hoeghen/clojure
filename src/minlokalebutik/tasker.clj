(ns minlokalebutik.tasker
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(defn put-sequence-on-channel [s]
  (let [ch (chan 5)]
    (go
       (a/onto-chan ch s))
    ch))




(defn execute-from-channel [ cnt channel fn]
  (let [dc (chan cnt)]
    (dotimes [n cnt]
      (a/go-loop [r (<! channel)]
        (if (not (= r nil))
          (do
            (fn r)
            (recur (<! channel)))
          (>! dc n))))
    dc))

(defn wait-for-count [cnt ch]
  (dotimes [n cnt]
    (<!! ch)
    (println "finished task" (inc n) " out of " cnt)))



(defn show [c]
  (.buf (.buf c)))