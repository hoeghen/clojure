(defproject minlokalebutik "0.1.1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-http "3.10.0"]
                 [cheshire "5.9.0"]
                 [pandect "0.6.1"]
                 [org.clojure/core.async "0.6.532"]]

  :main ^:skip-aot minlokalebutik.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
