(ns token.status
  (:require [re-frame.core :as re-frame]))

(defn send-message
  [event callback]
  (.dispatch (.-statusAPI js/window)
             (name event)
             (clj->js {:callback callback})))