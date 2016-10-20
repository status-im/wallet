(ns token.wallet.page
  (:require [re-frame.core :refer [subscribe dispatch]]
            [token.ethereum :refer [to-fixed unit->wei format-wei]]
            [token.components.clipboard :refer [clipboard-button]]
            [goog.i18n.DateTimeFormat]
            [re-frame.core :as re-frame]
            [token.db :as db]))

(defn nav []
  [:div.top-nav
   [:h2 "Wallet name"]
   [:div.nav-left
    [:a.nav-back
     {:on-click
      (fn [_]
        (.back js/history))}]]
   [:div.nav-right
    [:a.nav-update
     {:on-click
      (fn [_]
        (re-frame/dispatch [:initialize-wallet]))}]]])

(defn wallet-info [wallet-id]
  (let [balance (subscribe [:get-in (db/wallet-balance-path wallet-id)])]
    (fn [wallet-id]
      (let [balance-fmt (format-wei (unit->wei @balance "ether"))]
        [:div.wallet-container
         [:div.wallet
          [:div.wallet-amount
           [:p (to-fixed (:amount balance-fmt) 6)]
           [:span (:unit balance-fmt)]]
          [:div.wallet-controls
           [:div.button.wallet-btn
            {:on-click (fn [_]
                         (.dispatch (.-statusAPI js/window) (name :webview-send-transaction)
                                    (clj->js {:callback (fn [params]
                                                          (println (str "callback " (.stringify js/JSON params))))})))}
            "Send"]
           [:div.button.wallet-btn
            "Receive"]]]]))))

(defn address [wallet-id]
  [:div.wallet-address-container
   [:div.wallet-address
    [:span "Address"]
    [:p wallet-id]]])

(defn format-date [date-format date]
  (.format (goog.i18n.DateTimeFormat. date-format)
           (js/Date. date)))

(defn account-name [account]
  (str (.substring account 0 20) "..."))

(defn transaction [wallet-id tx]
  (let [sender       (.-from tx)
        recipient    (.-to tx)
        amount       (.-value tx)
        amount-fmt   (format-wei amount)
        timestamp    (* 1000 (.-timeStamp tx))
        incoming?    (= recipient wallet-id)
        action-class {:class (if incoming? "action-add" "action-remove")}
        amount-class {:class (if incoming? "green" "red")}
        sign         (if incoming? "+" "-")]
    ^{:key (gensym "tx__")}
    [:div.transaction
     [:div.transaction-action [:div action-class]]
     [:div.transaction-details
      [:div.transaction-name (account-name (if incoming? sender recipient))]
      [:div.transaction-date (format-date "d MMM 'at' hh:mm" timestamp)]
      [:div.transaction-amount
       [:span amount-class
        (str sign (:amount amount-fmt) " " (:unit amount-fmt))]]]]))

(defn transactions [wallet-id]
  (let [transactions (subscribe [:get-in (db/wallet-transactions-path wallet-id)])]
    [:div.wallet-transactions-container
     [:div.wallet-transactions
      [:span "Transactions"]
      (if (empty? @transactions)
        [:div.no-transactions "No transactions found"]
        (doall
          (map (partial transaction wallet-id) @transactions)))]]))

(defn wallet [wallet-id]
  (dispatch [:get-transactions wallet-id])
  [:div.wallet-screen
   [nav]
   [wallet-info wallet-id]
   [address wallet-id]
   [transactions wallet-id]])
