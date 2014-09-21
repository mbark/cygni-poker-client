(ns poker-client.util)

(defn- group-by-value [hand]
  (group-by :rank
            (sort-by :rank hand)))

(defn- n-of-a-kind? [hand n]
  (not (nil?
        (some (fn [[k v]] (= (count v) n))
              (group-by-value hand)))))

(defn four-of-a-kind?
  [hand]
  (n-of-a-kind? hand 4))

(defn full-house?
  [hand]
  (let [grouped-hand (group-by-value hand)]
    (and (n-of-a-kind? hand 2)
         (n-of-a-kind? hand 3))))

(defn flush?
  [hand]
  (= 1 (count (group-by :suit hand))))

(defn reduce-some [f coll]
  (not (some nil? (reductions f coll))))

(defn straight?
  [hand]
  (letfn [(ace->one
           [card]
           (if (= (:rank card) 14)
             {:rank 1 :suit (:suit card)}
             card))
          (is-straight?
           [coll]
           (reduce-some
            #(if (= (inc %1) %2) %2)
            (sort (map :rank coll))))]
    (or (is-straight? hand)
        (is-straight? (map ace->one hand)))))

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

(defn- hand-name->hand-value [hand-name]
  ({:royal-straight-flush 10
    :straight-flush 9
    :four-of-a-kind 8
    :full-house     7
    :flush 6
    :straight 5
    :three-of-a-kind 4
    :two-pair 3
    :pair 2
    :high-card 1} hand-name))

(defn- eval-full-hand [hand]
  (if (< (count hand) 5)
    nil
    (cond
     (royal-straight-flush? hand) :royal-straight-flush
     (straight-flush? hand) :straight-flush
     (four-of-a-kind? hand) :four-of-a-kind
     (full-house? hand) :full-house
     (flush? hand) :flush
     (straight? hand) :straight)))

(defn eval-hand [hand]
  (if-let [r (eval-full-hand hand)]
    r
    (cond
     (three-of-a-kind? hand) :three-of-a-kind
     (two-pair? hand) :two-pair
     (pair? hand) :pair
     :else :high-card)))


(defn compare-hands [pred hand1 hand2]
  (letfn [(hand-val [hand]
                    (hand-name->hand-value
                     (if (keyword? hand) hand (eval-hand hand))))]
    (pred (hand-val hand1) (hand-val hand2))))
