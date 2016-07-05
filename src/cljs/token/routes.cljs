(ns token.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute "/" []
            (re-frame/dispatch [:set-active-panel :wallets-panel]))

  (defroute "/wallet" []
            (re-frame/dispatch [:set-active-panel :wallet-panel]))

  (defroute "/wallet-state" []
            (re-frame/dispatch [:set-active-panel :wallet-state-panel]))

  (defroute "/transaction" []
            (re-frame/dispatch [:set-active-panel :transaction-panel]))
  ;; --------------------
  (hook-browser-navigation!))
