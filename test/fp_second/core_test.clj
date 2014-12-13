(ns fp-second.core-test
  (:require [clojure.test :refer :all]
            [fp-second.core :refer :all]))



(deftest bad-request
  (testing "Fail"
    (is (= false (is-working-url "http://habrahabr.ru/sdfsdfsadfsdf/")))))


(deftest find-urls-in-file
  (testing "Fail"
    (is (= 3 (count (parse-page (slurp "resources\\count-urls.html")))))))
