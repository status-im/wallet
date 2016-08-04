(defproject token "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [reagent "0.5.1"]
                 [binaryage/devtools "0.6.1"]
                 [re-frame "0.7.0"]
                 [secretary "1.2.3"]
                 [hickory "0.6.0"]
                 [cljsjs/web3 "0.16.0-0"]
                 [cljsjs/clipboard "1.5.9-0"]]

  :plugins [[lein-cljsbuild "1.1.3"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel {:css-dirs         ["resources/public/css"]
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
             :server-port      3450}

  :profiles
  {:dev
   {:dependencies [[com.cemerick/piggieback "0.2.1"]
                   [figwheel-sidecar "0.5.4-5"]]

    :plugins      [[lein-figwheel "0.5.4-5"]
                   [lein-doo "0.1.6"]]
    :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "token.core/mount-root"}
     :compiler     {:main                 token.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :foreign-libs         [{:file     "resources/js/react-slick.js"
                                            :provides ["react-slick"]}]
                    :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            token.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :foreign-libs    [{:file     "resources/js/react-slick.min.js"
                                       :provides ["react-slick"]}]
                    :pretty-print    false}}
    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:output-to     "resources/public/js/compiled/test.js"
                    :foreign-libs  [{:file     "resources/js/react-slick.js"
                                     :provides ["react-slick"]}]
                    :main          token.runner
                    :optimizations :none}}]})
