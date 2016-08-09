(ns token.ethereum
  (:require [cljsjs.web3]
            [clojure.browser.event :as event]
            [clojure.browser.net :as net]))

(defn connect
  [rpc-url]
  (println (str "connecting to " rpc-url))
  (->> (js/Web3.providers.HttpProvider. rpc-url)
       (js/Web3.)))

(defn wei->ether
  [web3 wei]
  (.toString (.fromWei web3 wei "ether")))

(defn balance
  [web3 account callback]
  (.eth.getBalance web3 account callback))

(defn accounts
  [web3 callback]
  (.getAccounts (.-eth web3) callback))

(defn transactions
  [account callback]
  (let [api-url (str "http://api.etherscan.io/api?module=account&action=txlist&address="
                     account
                     "&startblock=0&endblock=99999999&sort=asc&apikey=YourApiKeyToken")
        xhr     (net/xhr-connection)]
    (event/listen xhr :error #(.log js/console "Error" %1))
    (event/listen xhr :success (fn [ev]
                                 (let [response (.-result (.getResponseJson (.-target ev)))]
                                   (callback response))))
    (net/transmit xhr api-url "GET" {:q "json"})))
