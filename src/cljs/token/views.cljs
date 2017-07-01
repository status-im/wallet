(ns token.views
  (:require [re-frame.core :as re-frame]
            [token.wallets.page :refer [wallets]]
            [token.manage-wallets.page :refer [manage-wallets]]
            [token.wallet.page :refer [wallet]]
            [token.transaction.page :refer [transaction]]))

(defmulti panels identity)
(defmethod panels :wallets-panel [] [wallets])
(defmethod panels :wallet-panel [_ wallet-id] [wallet wallet-id])
(defmethod panels :transaction-panel [] [transaction])
(defmethod panels :default [] [:div])
(defmethod panels :manage-wallets-panel [] [manage-wallets])

(defn show-panel
  [[panel-name params]]
  [panels panel-name params])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [show-panel @active-panel])))
