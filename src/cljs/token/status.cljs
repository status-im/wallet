(ns token.status)

(defn send-message
  ([event data] (send-message event data nil))
  ([event data callback]
   (.dispatch (.-statusAPI js/window)
              (name event)
              (clj->js {:callback callback
                        :data     data}))))
