(ns poker-client.util-test
  (:require [clojure.test :refer :all]
            [poker-client.util :refer :all]))

(def hands
  {:pair [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 2} {:suit "HEARTS" :rank 5} {:suit "DIAMONDS" :rank 6} {:suit "DIAMONDS" :rank 10}]
   :two-pair [{:suit "CLUBS" :rank 2} {:suit "HEARTS" :rank 2} {:suit "SPADES" :rank 3} {:suit "DIAMONDS" :rank 3} {:suit "DIAMONDS" :rank 11}]
   :three-of-a-kind [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 2} {:suit "HEARTS" :rank 2} {:suit "SPADES" :rank 3} {:suit "DIAMONDS" :rank 10}]
   :straight [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 3} {:suit "HEARTS" :rank 4} {:suit "HEARTS" :rank 5} {:suit "HEARTS" :rank 6}]
   :straight-with-ace [{:suit "CLUBS" :rank 14} {:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 3} {:suit "HEARTS" :rank 4} {:suit "HEARTS" :rank 5}]
   :flush [{:suit "CLUBS" :rank 8} {:suit "CLUBS" :rank 3} {:suit "CLUBS" :rank 4} {:suit "CLUBS" :rank 5} {:suit "CLUBS" :rank 6}]
   :full-house [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 2} {:suit "HEARTS" :rank 4} {:suit "HEARTS" :rank 4} {:suit "DIAMONDS" :rank 4}]
   :four-of-a-kind [{:suit "CLUBS" :rank 3} {:suit "SPADES" :rank 3} {:suit "HEARTS" :rank 3} {:suit "DIAMONDS" :rank 3} {:suit "HEARTS" :rank 6}]
   :straight-flush [{:suit "HEARTS" :rank 2} {:suit "HEARTS" :rank 3} {:suit "HEARTS" :rank 4} {:suit "HEARTS" :rank 5} {:suit "HEARTS" :rank 6}]
   :royal-straight-flush [{:suit "HEARTS" :rank 10} {:suit "HEARTS" :rank 11} {:suit "HEARTS" :rank 12} {:suit "HEARTS" :rank 13} {:suit "HEARTS" :rank 14}]})

(defn find-and-eval [hand-name]
  (:pre [(contains? hands hand-name)])
  (is (= hand-name (eval-hand (hand-name hands)))))

(deftest test-pair
  (testing "Pair"
    (find-and-eval :pair)))

(deftest test-two-pair
  (testing "Two pair"
    (find-and-eval :two-pair)))

(deftest test-three-of-a-kind
  (testing "Three of a kind"
    (find-and-eval :three-of-a-kind)))

(deftest test-straight
  (testing "Straight"
    (find-and-eval :straight)))

(deftest test-straight-with-ace
  (testing "Straight with an ace counted as 1"
    (find-and-eval :straight)))

(deftest test-flush
  (testing "flush"
    (find-and-eval :flush)))

(deftest test-full-house
  (testing "Full house"
    (find-and-eval :full-house)))

(deftest test-four-of-a-kind
  (testing "Four of a kind"
    (find-and-eval :four-of-a-kind)))

(deftest test-straight-flush
  (testing "Straight flush"
    (find-and-eval :straight-flush)))

(deftest test-royal-straight-flush
  (testing "Royal straight flush"
    (find-and-eval :royal-straight-flush)))

(deftest test-compare-hands
  (testing "Compare hands"
    (are [hand1 hand2] (= hand1 hand2)
         true (compare-hands < (:pair hands) (:two-pair hands))
         false (compare-hands > (:pair hands) (:two-pair hands))
         true (compare-hands = (:pair hands) (:pair hands))
         true (compare-hands
               >
               [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 2} {:suit "HEARTS" :rank 5} {:suit "DIAMONDS" :rank 6} {:suit "DIAMONDS" :rank 11}]
               [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 2} {:suit "HEARTS" :rank 5} {:suit "DIAMONDS" :rank 6} {:suit "DIAMONDS" :rank 10}])
         true (compare-hands
               >
               [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 2} {:suit "HEARTS" :rank 5} {:suit "DIAMONDS" :rank 6} {:suit "SPADES" :rank 10}]
               [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 2} {:suit "HEARTS" :rank 5} {:suit "DIAMONDS" :rank 6} {:suit "HEARTS" :rank 10}])
         true (compare-hands
               =
               [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 2} {:suit "HEARTS" :rank 5} {:suit "DIAMONDS" :rank 6} {:suit "HEARTS" :rank 10}]
               [{:suit "CLUBS" :rank 2} {:suit "SPADES" :rank 2} {:suit "HEARTS" :rank 5} {:suit "DIAMONDS" :rank 6} {:suit "HEARTS" :rank 10}]))))

(deftest test-best-hand
  (testing "Best hand"
    (is (=
         [{:rank 14, :suit "CLUBS"} {:rank 8, :suit "DIAMONDS"} {:rank 14, :suit "DIAMONDS"} {:rank 14, :suit "HEARTS"} {:rank 8, :suit "HEARTS"}]
         (best-hand
          [{:rank 14, :suit "CLUBS"} {:rank 8, :suit "DIAMONDS"}]
          [{:rank 14, :suit "DIAMONDS"} {:rank 14, :suit "HEARTS"} {:rank 7, :suit "DIAMONDS"} {:rank 8, :suit "HEARTS"}])))))
