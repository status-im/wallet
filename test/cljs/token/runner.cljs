(ns token.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [token.core-test]))

(doo-tests 'token.core-test)
