(ns token.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]))

(defonce history (History.))

(defn nav! [token]
  (.setToken history (secretary/locate-route-value token)))

(defn hook-browser-navigation! []
  (doto history
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
            (re-frame/dispatch [:set-active-panel :wallets-panel]))

  (defroute "/wallet/:wallet-id" [wallet-id]
            (re-frame/dispatch [:set-active-panel :wallet-panel wallet-id]))

  (defroute "/transaction" []
            (re-frame/dispatch [:set-active-panel :transaction-panel]))

  (hook-browser-navigation!))
