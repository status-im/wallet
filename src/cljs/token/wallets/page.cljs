(ns token.wallets.page)

(defn wallets []
  [:div
   [:div.wallets
    [:h2 {} "Wallets"]
    [:div.wallet
     [:div.wallet-image]
     [:div.wallet-name "Another wallet"]
     [:div.wallet-hash "0x332ce8091A76bb710BDec4a40663b66"]
     [:div.wallet-currencies
      [:div.currency-usd [:span.currency "USD"] "12.32"]
      [:div.currency-rub [:span.currency "RUB"] "204 532"]
      [:div.currency-usd [:span.currency "USD"] "34"]]
     [:a {:href "#"} "Open wallet"]]
    [:div.pagination
     [:div.dot.active]
     [:div.dot]
     [:div.dot]]]
   [:div.wallet-actions
    [:a.trade-eth {:href "#"} "Trade ETH"]
    [:a.create-wallet {:href "#"}
     [:span.add-wallet] "Create a wallet"]]])
