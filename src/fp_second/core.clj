(ns fp-second.core
  (:gen-class)
  (:require [clj-http.client :as client])
  (:use [slingshot.slingshot :only [throw+ try+]])
  )

(import '(java.util.concurrent.CountDownLatch))
(use 'clojure.java.io)


(def urls (atom {}))
(defn read-urls [file-name]
  (with-open [rdr (reader file-name)]
             (vec (line-seq rdr)) ))


(defn is-working-url [url]
  (def cond-url true)
  (try+
    (def url-inf (client/get url))
    (catch Object _
      (def cond-url false)))
  cond-url
)

(defn parse-page [document]
  (distinct
    (map #(clojure.string/replace (re-find #"https?[^\"]+\"" %) #"\""  "")
         (filter #(.contains % (or "http" "https")) (re-seq #"<a[^>]+>" document)) )
    )
  )


(defn recur-parse [url cur-lvl max-lvl vec-urls ]

    (if (<= cur-lvl max-lvl)
      (do
        (def url-inf nil)
        (try+
          (def url-inf (client/get url))
        (catch Object _))

        (if (nil? url-inf)
          (swap! urls assoc-in (conj vec-urls :descr) "bad-url")

          (do
            (if (.contains (take-last 1 (:trace-redirects url-inf)) url)
              (swap! urls assoc-in (conj vec-urls :descr)
                     (str (count (parse-page (:body url-inf))) "  links"))

              (swap! urls assoc-in (conj vec-urls :descr)
                     (str  "redir" (take-last 1 (:trace-redirects url-inf))))
            )
            (swap! urls assoc-in vec-urls (reduce (fn [m url]
                                                    (assoc m url {})) (get-in @urls vec-urls) (parse-page (:body url-inf))))
            (if (< cur-lvl max-lvl)
              (do

               (doall  (pmap #(recur-parse %1 (inc cur-lvl) max-lvl (conj vec-urls %1))
                               (parse-page (:body url-inf))))
              )
            )
          )
        )
      )
    )
)



(defn -main
  [& args]

  "read urls frm file"
  (swap! urls  conj (reduce (fn [m url]
                             (assoc m url {})) {} (read-urls "resources\\urls.txt")))

  (doall (pmap #(recur-parse %1 1 2 [%1]) (keys @urls)))


  (clojure.pprint/pprint @urls)
  )
