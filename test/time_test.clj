(ns time-test
  (:require [clojure.test :refer :all]
            [minlokalebutik.time :refer :all])
  (:import (java.time LocalDateTime)))


(def date "2020-01-08T07:00:00+0000")

(deftest smoke
  (testing "does test work"
    (is (= 1 1))))



(deftest parseDateFromShopgun
  (testing "parse a date from shopgun"
    (is (instance? LocalDateTime
                   (parseDateTimeString date)))
    (is (instance? LocalDateTime (parseDateString "2020-01-08")))))



