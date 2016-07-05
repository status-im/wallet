(ns token.wallet.page)

(defn nav []
  [:div.top-nav
   [:a.nav-back {:href "#"}]
   [:a.nav-update {:href "#"}]
   [:h2 "Wallet name"]])

(defn wallet-info []
  [:div.wallet
   [:div.wallet-amount
    [:p "435.43434"]
    [:span "BTC"]]
   [:div.wallet-controls
    [:div.button "Send"]
    [:div.button "Receive"]]])

(defn pagination []
  [:div.pagination
   [:div.dot.active]
   [:div.dot]
   [:div.dot]])

(defn address []
  [:div.wallet-address
   [:span "Address"]
   [:p "0x332ce8091A76bb710BDec4a40663b66"]
   [:div.wallet-controls
    [:div.button [:span.button-copy] "Copy"]
    [:div.button [:span.button-qr] "Show QR"]
    [:div.button-full "Backup or restore your account"]]])

(defn transactions []
  [:div.wallet-transactions
   [:span "Transactions"]
   [:div.transaction
    [:div.transaction-action [:div.action-remove]]
    [:div.transaction-details
     [:div.transaction-name "Amazon inc."]
     [:div.transaction-name "Today at 19:43"]
     [:div.transaction-amount
      [:span.red "-0.9843 BTC"]]]]

   [:div.transaction
    [:div.transaction-action [:div.action-remove]]
    [:div.transaction-details
     [:div.transaction-name "App store"]
     [:div.transaction-date "Today at 16:43"]
     [:div.transaction-amount [:span.red "-1.3984 BTC"]]]]

   [:div.transaction
    [:div.transaction-action [:div.action-add]]
    [:div.transaction-details
     [:div.transaction-name "CVS Pharmacy"]
     [:div.transaction-date "1 Mar at 19:43"]
     [:div.transaction-amount [:span.green "+9.3843 BTC"]]]]])

(defn wallet []
  [:div.wallet-screen
   [nav]
   [wallet-info]
   [pagination]
   [address]
   [transactions]])

(defn wallet-state []
  [:div.wallet-screen
   [nav]
   [wallet-info]
   [address]
   [transactions]])
