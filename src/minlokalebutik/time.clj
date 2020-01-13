(ns minlokalebutik.time
  (:import java.time.LocalDateTime)
  (:import java.time.LocalDate)
  (:import java.time.format.DateTimeFormatter
           (java.util Formatter$DateTime)
           (java.time ZonedDateTime Instant OffsetDateTime)))

(defn parseDateTimeString [string]
  (LocalDateTime/parse string (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ssZ")))

(defn parseDateString [string]
  (.atTime (LocalDate/parse string) 0 0))

(defn today []
  (LocalDateTime/now))

(defn week-ago []
  (.minusDays (today) 7))


(defn this-week? [date]
  (.isBefore (week-ago) date))

(defn after? [date limit-date]
  (or (.isAfter date limit-date)))

(defn before? [date limit-date]
  (or (.isBefore date limit-date) (.isEqual date limit-date)))
