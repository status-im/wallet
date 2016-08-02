(ns token.wallet.page
  (:require [re-frame.core :refer [subscribe dispatch]]
            [token.routes :as routes]
            [token.ethereum :refer [wei->ether]]
            [token.components.clipboard :refer [clipboard-button]]
            [goog.i18n.DateTimeFormat]
            [re-frame.core :as re-frame]
            [token.db :as db]))

(defn nav []
  [:div.top-nav
   [:span.nav-back
    {:on-click
     (fn [_]
       (.back js/history))}]
   [:a.nav-update
    {:on-click
     (fn [_]
       (re-frame/dispatch [:initialize-wallet]))}]
   [:h2 "Wallet name"]])

(defn wallet-info [wallet-id]
  (let [balance (subscribe [:get-in (db/wallet-balance-path wallet-id)])]
    [:div.wallet
     [:div.wallet-amount
      [:p @balance]
      [:span "ETH"]]
     [:div.wallet-controls
      [:div.button
       {:on-click (fn [_]
                    (routes/nav! "/transaction"))}
       "Send"]
      [:div.button "Receive"]]]))

(defn pagination []
  [:div.pagination
   [:div.dot.active]
   [:div.dot]
   [:div.dot]])

(defn address [wallet-id]
  [:div.wallet-address
   [:span "Address"]
   [:p wallet-id]
   [:div.wallet-controls
    [clipboard-button "Copy" wallet-id]
    [:div.button [:span.button-qr] "Show QR"]
    [:div.button-full "Backup or restore your account"]]])

(defn format-date [date-format date]
  (.format (goog.i18n.DateTimeFormat. date-format)
           (js/Date. date)))

(defn account-name [account]
  (str (.substring account 0 20) "..."))

(defn transaction [wallet-id tx]
  (let [web3         (subscribe [:get-in [:eth :web3]])
        sender       (.-sender tx)
        recipient    (.-recipient tx)
        amount       (.-amount tx)
        incoming?    (= recipient wallet-id)
        action-class {:class (if incoming? "action-add" "action-remove")}
        amount-class {:class (if incoming? "green" "red")}
        sign         (if incoming? "+" "-")]
    ^{:key (gensym "tx__")}
    [:div.transaction
     [:div.transaction-action [:div action-class]]
     [:div.transaction-details
      [:div.transaction-name (account-name (if incoming? sender recipient))]
      [:div.transaction-date (format-date "d MMM 'at' hh:mm" (.-time tx))]
      [:div.transaction-amount
       [:span amount-class
        (str sign (wei->ether @web3 amount) " ETH")]]]]))

(defn transactions [wallet-id]
  (let [transactions (subscribe [:get-in (db/wallet-transactions-path wallet-id)])]
    [:div.wallet-transactions
     [:span "Transactions"]
     (doall
       (map (partial transaction wallet-id) @transactions))]))

(defn wallet [wallet-id]
  ;; todo: uses wallet id with non-empty transactions list
  (let [test-wallet-id "0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae"]
    (dispatch [:get-transactions test-wallet-id])
    [:div.wallet-screen
     [nav]
     [wallet-info wallet-id]
     [address wallet-id]
     [transactions test-wallet-id]]))
