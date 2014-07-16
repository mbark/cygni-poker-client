(ns poker-client.util)

(defn group-by-value [hand]
  (group-by :rank
            (sort-by :rank hand)))

(defn nr-of-a-kind [hand]
  (reduce #(max %1 (count (val %2)))
          0
          (group-by-value hand)))

(defn n-of-a-kind? [hand n]
  (= (nr-of-a-kind hand) n))

(defn four-of-a-kind?
  [hand]
  (n-of-a-kind? hand 4))

(defn full-house?
  [hand]
  (and (n-of-a-kind? hand 3) (n-of-a-kind? hand 2)))

(defn flush?
  [hand]
  (= 1 (count (group-by :suit hand))))

(defn reduce-some [f coll]
  (not (some nil? (reductions f coll))))

(defn straight?
  [hand]
  (reduce-some
   #(if (= (inc (:rank %1)) (:rank %2)) %2)
   (sort-by :rank hand)))

(defn straight-flush? [hand]
  (and
   (straight? hand)
   (flush? hand)))

(defn royal-straight-flush? [hand]
  (and
   (straight-flush? hand)
   (let [ranks (map :rank hand)]
     (= (apply max ranks) 14)
     (= (apply min ranks) 10))))

(defn three-of-a-kind?
  [hand]
  (n-of-a-kind? hand 3))

(defn two-pair?
  [hand]
  (= 2
     (count (filter
             #(= 2 (count (val %)))
             (group-by-value hand)))))

(defn pair? [hand]
  (n-of-a-kind? hand 2))
