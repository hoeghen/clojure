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




(defn execute-from-channel [cnt channel fn]
  (dotimes [n cnt]
    (a/go-loop [r (<! channel)]
      (if (not (= r nil))
        (do
          (fn r)
          (recur (<! channel)))
        (println "done posting")))))

