(ns token.views
  (:require [re-frame.core :as re-frame]
            [token.wallets.page :refer [wallets]]
            [token.wallet.page :refer [wallet wallet-state]]
            [token.transaction.page :refer [transaction]]))

(defmulti panels identity)
(defmethod panels :wallets-panel [] [wallets])
(defmethod panels :wallet-panel [] [wallet])
(defmethod panels :wallet-state-panel [] [wallet-state])
(defmethod panels :transaction-panel [] [transaction])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [show-panel @active-panel])))
