(ns token.status
  (:require [re-frame.core :as re-frame]))

(defn send-message
  [event data callback]
  (.dispatch (.-statusAPI js/window)
             (name event)
             (clj->js {:callback callback
                       :data data})))