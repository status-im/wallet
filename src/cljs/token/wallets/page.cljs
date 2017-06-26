(ns token.wallets.page
  (:require [re-frame.core :refer [subscribe dispatch]]
            [token.ethereum :refer [to-fixed unit->wei format-wei]]
            [reagent.core :as r]
            [token.components.slick :as s]
            [re-frame.core :as re-frame]
            [token.db :as db]))

(defn all-transactions []
  [:div.wallet-transactions-container
   [:div.wallet-transactions
    [:span "Transaction history"]]])

(defn wallet [wallet-id]
  (let [balance (subscribe [:get-in (db/wallet-balance-path wallet-id)])]
    (fn [wallet-id]
      (let [balance-fmt (format-wei (unit->wei @balance "ether"))]
        [:a.wallet-btn {:href (str "#/wallet/" wallet-id)}
         [:div.wallet
          [:div.wallet-name "Main wallet" [:span.cur (:unit balance-fmt)]]
          [:span.wallet-currencies
           [:div.currency-usd
            (to-fixed (:amount balance-fmt) 4) [:span.cur (:unit balance-fmt)]]]
          ] [:div.clearfix]]))))

(defn wallets []
  (let [accounts (subscribe [:get-in db/wallet-accounts-path])]
    [:div
     [:div.top-nav
      [:h2 {} "Wallet"]
      [:div.nav-left
       [:a.nav-back-hp [:span "W"]]]
      [:div.nav-right
       [:a.nav-update
        {:on-click
         (fn [_]
           (re-frame/dispatch [:refresh-accounts]))}]]]
     [:div.wallets
      [s/slick {:dots           true
                :infinite       false
                :slidesToShow   1
                :slidesToScroll 1
                :arrows         false
                :adaptiveHeight false
                :centerMode     true}
       (doall (map (fn [id]
                     ^{:key id}
                     [:div [wallet id]])
                   @accounts))]
      [:a.wallet-btn {:href (str "#/")}
       [:div.wallet.manage-wallet
        [:div.wallet-name "Manage wallets"]
        [:span.wallet-points ""]
        ] [:div.clearfix]]
      [:a.wallet-btn-left {:href (str "#/")}
       [:div.wallet.btn-wallet
        [:div.wallet-href-name "Recieve"]
        ] [:div.clearfix]]
      [:a.wallet-btn-right {:href (str "#/")}
       [:div.wallet.btn-wallet
        [:div.wallet-href-name "Send"]
        ] [:div.clearfix]]]
     [:div.wallet-transactions-container
      [:div.wallet-transactions
       [:span "Transaction History"]]]]))
