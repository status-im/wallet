(ns token.status)

(defn send-message
  ([event data] (send-message event data nil))
  ([event data callback]
   (.dispatch js/statusAPI
              (name event)
              (clj->js {:callback callback
                        :data     data}))))
