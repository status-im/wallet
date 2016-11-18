(ns token.wallets.page
  (:require [re-frame.core :refer [subscribe dispatch]]
            [token.ethereum :refer [to-fixed unit->wei format-wei]]
            [reagent.core :as r]
            [token.components.slick :as s]
            [re-frame.core :as re-frame]
            [token.db :as db]))

(defn wallet-uri [wallet-id]
  [:a.wallet-btn {:href (str "#/wallet/" wallet-id)} "Open wallet"])

(defn wallet [wallet-id]
  (let [balance     (subscribe [:get-in (db/wallet-balance-path wallet-id)])]
    (fn [wallet-id]
      (let [balance-fmt (format-wei (unit->wei @balance "ether"))]
        [:div.wallet
         [:div.wallet-image]
         [:div.wallet-name "Main wallet"]
         [:div.wallet-hash wallet-id]
         [:div.wallet-currencies
          [:div.currency-usd [:span.currency (:unit balance-fmt)]
           (to-fixed (:amount balance-fmt) 6)]]
         (wallet-uri wallet-id)]))))

(defn wallets []
  (let [accounts (subscribe [:get-in db/wallet-accounts-path])]
    [:div
     [:div.top-nav
      [:h2 {} "Wallets"]
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
                   @accounts))]]
     [:div.wallet-actions
      [:a.trade-eth {:href "#"} "Trade ETH"]
      [:a.create-wallet {:href "#"}
       [:span.add-wallet] "Create a wallet"]]]))
