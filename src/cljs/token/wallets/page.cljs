(ns token.wallets.page
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
            [token.components.slick :as s]
            [re-frame.core :as re-frame]
            [token.db :as db]))

(defn wallet-uri [wallet-id]
  [:a {:href (str "#/wallet/" wallet-id)} "Open wallet"])

(defn account-name [account]
  (str (.substring account 0 20) "..."))

(defn wallet [wallet-id]
  (let [balance (subscribe [:get-in (db/wallet-balance-path wallet-id)])]
    (fn [wallet-id]
      [:div.wallet
       [:div.wallet-image]
       [:div.wallet-name "Another wallet"]
       [:div.wallet-hash (account-name wallet-id)]
       [:div.wallet-currencies
        [:div.currency-usd [:span.currency "ETH"]
         (when-let [balance @balance]
           (.toFixed (js/parseFloat balance) 4))]]
       (wallet-uri wallet-id)])))

(defn wallets []
  (let [accounts (subscribe [:get-in db/wallet-accounts-path])]
    [:div
     [:div.top-nav
      [:a.nav-update
       {:on-click
        (fn [_]
          (re-frame/dispatch [:initialize-wallet]))}]
      [:h2 {} "Wallets"]]
     [:div.wallets
      [s/slick {:dots           true
                :infinite       false
                :slidesToShow   1
                :slidesToScroll 1
                :arrows         false
                :adaptiveHeight false
                :centerMode     true
                :variableWidth  true}
       (doall (map (fn [id]
                     ^{:key id}
                     [:div [wallet id]])
                   @accounts))]]
     [:div.wallet-actions
      [:a.trade-eth {:href "#"} "Trade ETH"]
      [:a.create-wallet {:href "#"}
       [:span.add-wallet] "Create a wallet"]]]))
