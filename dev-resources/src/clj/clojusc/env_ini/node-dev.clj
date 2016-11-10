(require
  '[cljs.repl :as repl]
  '[cljs.repl.node :as node])

(repl/repl* (node/repl-env)
  {:target :nodejs
   :output-dir "target/node"
   :output-to "target/node/env_ini.js"
   :optimizations :none
   :print-input-delimiter true
   :cache-analysis true
   :source-map true})
