(defproject fp-second "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-http "1.0.1"]
                 [slingshot "0.12.1"]
                 ]
  :main ^:skip-aot fp-second.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
