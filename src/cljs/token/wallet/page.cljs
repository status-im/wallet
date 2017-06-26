(ns token.wallet.page
  (:require [re-frame.core :refer [subscribe dispatch]]
            [token.ethereum :refer [to-fixed unit->wei format-wei]]
            [token.components.clipboard :refer [clipboard-button]]
            [secretary.core :as secretary :refer [dispatch!]]
            [goog.i18n.DateTimeFormat]
            [re-frame.core :as re-frame]
            [token.status :as status]
            [token.db :as db]
            [token.utils :as u]))

(defn nav [wallet-id]
  [:div.top-nav
   [:h2 "Wallet"]
   [:div.nav-left
    [:a.nav-back
     {:on-click
      (fn [_]
        (.back js/history))}]]
   [:div.nav-right
    [:a.nav-update
     {:on-click
      (fn [_]
        (re-frame/dispatch [:refresh-account wallet-id]))}]]])

(defn send-money [amount]
  #_(println (str "send amount " amount))
  (when (pos? amount)
    (status/send-message :webview-send-transaction
                         {:amount amount}
                         (fn [params]
                           (let [{:keys [address amount] :as params'}
                                 (js->clj params :keywordize-keys true)]

                             #_(println (str "callback " params'))
                             (dispatch [:set :send-address address])
                             (dispatch [:set :send-amount-qr amount])
                             (dispatch! "/transaction"))))))

(defn request-money [amount]
  #_(println (str "request amount " amount))
  (status/send-message :webview-receive-transaction
                       {:amount amount}
                       (fn [params]
                         #_(println (str "callback " (.stringify js/JSON params))))))

(defn wallet-info [wallet-id]
  (let [balance (subscribe [:get-in (db/wallet-balance-path wallet-id)])
        send-amount (subscribe [:get :send-amount])
        request-amount (subscribe [:get :request-amount])
        text           (subscribe [:get :text])]
    (fn [wallet-id]
      (let [balance-fmt (format-wei (unit->wei @balance "ether"))]
        [:div.wallet-container
         [:div.wallet
          [:div.wallet-amount.detail
           [:div.left-block "Main wallet"
            [:span.wallet-points.detail ""]
            ]
            [:p (to-fixed (:amount balance-fmt) 2) [:span.unitDetail (:unit balance-fmt)]]
            [:div.address-detail "Address"]
            [:p.walletAddress wallet-id]
           ]
          [:div.wallets-detail
            [:div.wallet-btn-left
             [:div.btn-wallet
              [:div.wallet-href-name [clipboard-button "Copy Address" wallet-id]]
              ] [:div.clearfix]]
            [:a.wallet-btn-right {:href (str "#/")}
             [:div.btn-wallet
              [:div.wallet-href-name "Show CR"]
              ] [:div.clearfix]]
            [:div.clearfix]
           ]]]))))

(defn format-date [date-format date]
  (.format (goog.i18n.DateTimeFormat. date-format)
           (js/Date. date)))

(defn account-name [account]
  (str (.substring account 0 20) "..."))

(defn transaction [wallet-id tx]
  (let [{:keys [from to value timeStamp hash timestamp confirmations]}
        (js->clj tx :keywordize-keys true)
        {:keys [amount unit]} (format-wei value)
        timestamp' (or timestamp (* 1000 timeStamp))
        incoming? (= to wallet-id)
        action-class {:class (if incoming? "action-add" "action-remove")}]
    ^{:key hash}
    [:div.transaction
     [:div.transaction-action [:div action-class]]
     [:div.transaction-details
      [:div.transaction-amount
       [:span [:span.amount amount] unit]]
      [:div.transaction-name [:span.transaction-type (if incoming? "from" "to")] (account-name (if incoming? from to))]
      [:div.transaction-date (str (format-date "d MMM hh:mm" timestamp')
                                  " "
                                  (when-not confirmations "(pending...)"))]
]]))

(defn transactions [wallet-id]
  (let [transactions (subscribe [:get-in (db/wallet-transactions-path wallet-id)])
        pending-transactions (subscribe [:pending-transactions wallet-id])]
    [:div.wallet-transactions-container
     [:div.wallet-transactions
      [:span "Transaction history"]
      (if (and (empty @pending-transactions) (empty? @transactions))
        [:div.no-transactions "No transactions found"]
        (concat
          (doall
            (map (partial transaction wallet-id) @pending-transactions))
          (doall
            (map (partial transaction wallet-id) @transactions))))]]))

(defn wallet [wallet-id]
  [:div.wallet-screen
   [nav wallet-id]
   [wallet-info wallet-id]
   [transactions wallet-id]])
