(ns token.manage-wallets.page
  (:require [re-frame.core :refer [subscribe dispatch]]
            [token.ethereum :refer [to-fixed unit->wei format-wei]]
            [reagent.core :as r]
            [token.components.slick :as s]
            [re-frame.core :as re-frame]
            [token.db :as db]))

(defn wallet [wallet-id]
  (let [balance (subscribe [:get-in (db/wallet-balance-path wallet-id)])]
    (fn [wallet-id]
      (let [balance-fmt (format-wei (unit->wei @balance "ether"))]
        [:a.wallet-btn {:href (str "#/wallet/" wallet-id)}
         [:div.manage-wallets-item
          [:div.wallet-name.manage-wallet "Main wallet" [:span.manage-wallet-cur (:unit balance-fmt)]]
          [:span.wallet-currencies
           [:div.currency-usd.manage-wallets
            (to-fixed (:amount balance-fmt) 4) [:span.manage-wallet-cur (:unit balance-fmt)]]]
            [:span.wallet-points.manage-wallets "..."]
          ] [:div.clearfix]]))))

(defn manage-wallets []
  (let [accounts (subscribe [:get-in db/wallet-accounts-path])]
    [:div
     [:div.top-nav
      [:h2 {} "Wallet"]
      [:div.nav-left
       [:a.nav-back
        {:on-click
         (fn [_]
           (.back js/history))}]]
      [:div.nav-right
       [:a.nav-update
        {:on-click
         (fn [_]
           (re-frame/dispatch [:refresh-accounts]))}]]]
     [:div.wallets.manage-wallets
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
                   @accounts))]]
     [:a.manage-wallet-btn {:href (str "#/")}
      [:span "Add wallets"]
      [:div.clearfix]]]))