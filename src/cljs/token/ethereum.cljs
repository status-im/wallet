(ns token.ethereum
  (:require [cljsjs.web3]
            [clojure.browser.event :as event]
            [clojure.browser.net :as net]))

(defn connect
  [rpc-url]
  #_(println (str "connecting to " rpc-url))
  (if (exists? js/web3)
    js/web3
    (->> (js/Web3.providers.HttpProvider. rpc-url)
         (js/Web3.))))

(defn to-fixed
  [amount precision]
  (.toFixed (js/parseFloat amount) precision))

(defn wei->unit
  [wei unit]
  (.toString (.fromWei js/Web3.prototype wei unit)))

(defn unit->wei
  [amount unit]
  (.toString (.toWei js/Web3.prototype amount unit)))

(defn wei->ether
  [wei]
  (wei->unit wei "ether"))

(defn format-unit
  [unit]
  (case unit
    "ether" "ETH"
    "wei" "WEI"
    unit))

(defn format-wei-unit
  [wei]
  (let [tens (count (str wei))]
    (cond
      (> tens 18) "ether"
      (> tens 15) "finney"
      (> tens 12) "szabo"
      (> tens 9) "Gwei"
      (> tens 6) "Mwei"
      (> tens 3) "Kwei"
      :else "wei")))

(defn format-wei
  [wei]
  (let [unit (format-wei-unit wei)]
    {:amount (wei->unit wei unit)
     :unit   (format-unit unit)}))

(defn balance
  [web3 account callback]
  (.getBalance web3.eth account callback))

(defn accounts
  [web3 callback]
  (.getAccounts web3.eth callback))

(defn transactions
  [account callback]
  (let [api-url (str "https://ropsten.etherscan.io/api?module=account&action=txlist&address="
                     account
                     "&startblock=0&endblock=99999999&sort=desc&apikey=YourApiKeyToken")
        xhr     (net/xhr-connection)]
    (event/listen xhr "error" #(.log js/console "Error" %1))
    (event/listen xhr "success" (fn [ev]
                                  (let [response (.-result (.getResponseJson (.-target ev)))]
                                    (callback response))))
    (net/transmit xhr api-url "GET" {:q "json"})))
