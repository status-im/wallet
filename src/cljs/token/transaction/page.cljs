(ns token.transaction.page)

(defn transaction []
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
     [:p "Wallet name – 0.32039 ETH"]]

    [:div.send-to "To"
     [:div.scan-qr
      [:span.image-qr] "Scan QR"]]

    [:div.send-amount "Amount"]
    [:div.send-currency "ETH" [:div.arrow-down]]

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
       "Your transaction will be mined probably within 30 seconds")]]
   [:div.button-send
    [:span.image-send] "Send 0.2343 ETH"]])
