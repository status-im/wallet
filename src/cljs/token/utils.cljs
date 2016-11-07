(ns token.utils)

(defn value [e]
  (.-value (.-target e)))
