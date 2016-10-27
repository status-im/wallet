(ns token.transaction.page
  (:require [re-frame.core :refer [subscribe dispatch]]
            [token.status :as status]
            [token.db :as db]))

(defn scan-qr-click
  []
  (status/send-message :webview-scan-qr
                       {}
                       (fn [params]
                         (println (str "callback " (.stringify js/JSON params))))))

(defn transaction []
  (let [wallet-id (subscribe [:get :current-wallet])
        balance (subscribe [:get-in (db/wallet-balance-path wallet-id)])
        send-amount (subscribe [:get :send-amount])]
    (fn []
      [:div
       [:div.top-nav
        {:class ""}
        [:span.nav-back {:on-click
                         (fn [_]
                           (.back js/history))}]
        [:h2 "Send funds"]]
       [:div.send-screen
        [:div.send-from
         [:span "From"]
         [:p (str "Main Account – " (or @balance 0) " ETH")]]

        [:div.send-to "To"
         [:div.scan-qr {:on-click #(scan-qr-click)}
          [:span.image-qr] "Scan QR"]]

        [:div.send-amount
         [:input.send-amount-input {:value       @send-amount
                                    :placeholder "Amount"
                                    :on-change   #(let [value (.-value (.-target %))]
                                                   (dispatch [:set :current-send-amount (if (empty? value)
                                                                                          nil
                                                                                          value)]))}]]
        [:div.send-currency "ETH" [:div.arrow-down]]
        [:div.transaction-fee
         [:div.send-fee
          "Fee"
          [:div.fee-amount "0.00043247823 " [:span "ETH"]]]
         [:div.send-range
          [:div.bar]
          [:div.range-control]]
         [:div.range-min-text "Cheaper"]
         [:div.range-max-text "Faster"]
         [:div.note
          (str "This is the most amount of money you might "
               "be used to process this transaction. "
               "Your transaction will be mined probably within 30 seconds")]]]
       [:div.button-send
        [:span.image-send] (str "Send " (or @send-amount 0) " ETH")]])))
