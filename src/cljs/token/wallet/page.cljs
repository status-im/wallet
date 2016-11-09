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
  (when (pos? amount)
    (status/send-message :webview-receive-transaction
                         {:amount amount}
                         (fn [params]
                           #_(println (str "callback " (.stringify js/JSON params)))))))

(defn wallet-info [wallet-id]
  (let [balance        (subscribe [:get-in (db/wallet-balance-path wallet-id)])
        send-amount    (subscribe [:get :send-amount])
        request-amount (subscribe [:get :request-amount])
        text           (subscribe [:get :text])]
    (fn [wallet-id]
      (let [balance-fmt (format-wei (unit->wei @balance "ether"))]
        [:div.wallet-container
         [:div.wallet
          [:div.wallet-amount
           [:p (to-fixed (:amount balance-fmt) 6)]
           [:span (:unit balance-fmt)]]
          [:div.wallet-send.row
           [:p.title "Send money"]
           [:div.amount-controls
            [:input.amount {:placeholder "Enter amount"
                            :value       @send-amount
                            :on-change   #(dispatch [:set :send-amount (u/value %)])
                            :type        :number}]
            [:div.column
             [:span {:on-click (when (pos? (js/parseFloat @balance))
                                 #(send-money @send-amount))} "SEND"]]]]
          [:div.wallet-request.row
           [:p.title "Request money"]
           [:div.amount-controls
            [:input.amount {:placeholder "Enter amount"
                            :value       @request-amount
                            :on-change   #(dispatch [:set :request-amount (u/value %)])
                            :type        :number}]
            [:div.column
             [:span {:on-click #(request-money @request-amount)}
              "RECEIVE"]]]]]]))))

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
  (let [{:keys [from to value timeStamp hash timestamp confirmations]}
        (js->clj tx :keywordize-keys true)
        {:keys [amount unit]} (format-wei value)
        timestamp'   (or timestamp (* 1000 timeStamp))
        incoming?    (= to wallet-id)
        action-class {:class (if incoming? "action-add" "action-remove")}
        amount-class {:class (if incoming? "green" "red")}
        sign         (if incoming? "+" "-")]
    ^{:key hash}
    [:div.transaction
     [:div.transaction-action [:div action-class]]
     [:div.transaction-details
      [:div.transaction-name (account-name (if incoming? from to))]
      [:div.transaction-date (str (format-date "d MMM 'at' hh:mm" timestamp')
                                  " "
                                  (when-not confirmations "(pending...)"))]
      [:div.transaction-amount
       [:span amount-class
        (str sign amount " " unit)]]]]))

(defn transactions [wallet-id]
  (let [transactions         (subscribe [:get-in (db/wallet-transactions-path wallet-id)])
        pending-transactions (subscribe [:pending-transactions wallet-id])]
    [:div.wallet-transactions-container
     [:div.wallet-transactions
      [:span "Transactions"]
      (if (and (empty @pending-transactions) (empty? @transactions))
        [:div.no-transactions "No transactions found"]
        (concat
          (doall
            (map (partial transaction wallet-id) @pending-transactions))
          (doall
            (map (partial transaction wallet-id) @transactions))))]]))

(defn wallet [wallet-id]
  (dispatch [:get-transactions wallet-id])
  [:div.wallet-screen
   [nav]
   [wallet-info wallet-id]
   [address wallet-id]
   [transactions wallet-id]])
