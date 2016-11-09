(ns token.handlers
  (:require [re-frame.core :as re-frame]
            [token.db :as db]
            [token.ethereum :refer [connect accounts balance transactions
                                    wei->ether unit->wei]]
            [secretary.core :refer [dispatch!]]))

(defn register-handler
  ([id handler]
   (re-frame/register-handler id #_[re-frame/debug] handler))
  ([id middleware handler]
   (re-frame/register-handler id [re-frame/debug middleware] handler)))

(defn side-effect!
  [handler]
  (fn [db params]
    (handler db params)
    db))

(register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

(register-handler
  :initialize-wallet
  (fn initialize-wallet
    [{:keys [eth] :as db} _]
    (let [rpc-url (:rpc-url eth)
          web3    (connect rpc-url)]
      (assoc-in db [:eth :web3] web3))))

(register-handler
  :start-auto-refreshing
  (fn [db [_ refresh-timer]]
    (js/setInterval
      #(re-frame/dispatch [:refresh-accounts]) refresh-timer)
    db))

(register-handler
  :refresh-accounts
  (fn [{:keys [eth] :as db} _]
    (let [web3 (:web3 eth)]
      (when web3
        (accounts web3
                  (fn [error result]
                    (when error
                      #_(println (str "error: " error)))
                    (re-frame/dispatch
                      [:set-accounts result]))))
    db)))

(register-handler
  :refresh-account
  (fn [db [_ account refresh-transactions?]]
    #_(println (str "refreshing wallet " account refresh-transactions?))
    (re-frame/dispatch [:get-balance account])
    (when refresh-transactions?
      (re-frame/dispatch [:get-transactions account]))
    db))

(register-handler
  :set-accounts
  (fn set-accounts
    [db [_ accounts]]
    (doall (map #(re-frame/dispatch [:refresh-account %]) accounts))
    (assoc-in db db/wallet-accounts-path accounts)))

(register-handler
  :get-balance
  (fn get-balance
    [db [_ account]]
    (let [web3 (get-in db [:eth :web3])]
      (balance web3 account
               (fn [error result]
                 (when error
                   #_(println (str "error: " error)))
                 (re-frame/dispatch
                   [:set-balance account (wei->ether result)])))
      db)))

(register-handler
  :set-balance
  (fn set-balance
    [db [_ account balance]]
    (assoc-in db (db/wallet-balance-path account) balance)))

(register-handler
  :set
  (fn set
    [db [_ key value]]
    (assoc db key value)))

(register-handler
  :get-transactions
  (side-effect!
    (fn get-transactions
      [_ [_ account]]
      (transactions account #(re-frame/dispatch [:set-transactions account %])))))

(register-handler
  :set-transactions
  (re-frame/after
    (fn [db [_ account]]
      (let [transactions (get-in db (db/wallet-transactions-path account))
            hashes       (map :hash transactions)]
        (when (not-empty hashes)
          (re-frame/dispatch [:clear-pending-transactions account hashes])))))
  (fn set-transactions
    [db [_ account transactions]]
    (let [transactions' (js->clj transactions :keywordize-keys true)]
      (assoc-in db (db/wallet-transactions-path account) transactions'))))

(register-handler
  :set-active-panel
  (fn [db [_ active-panel params]]
    (assoc db :active-panel [active-panel params])))

(defn send-transaction-callback [{:keys [from] :as params}]
  (fn [error data]
    ;; todo show error
    (when-not error
      (re-frame/dispatch [:add-pending-transaction (assoc params :hash data)])
      (re-frame/dispatch [:set :send-amount nil])
      (dispatch! (str "/wallet/" from)))))

(register-handler
  :send-eth
  (side-effect!
    (fn [{:keys [send-amount send-address current-wallet eth]}]
      (let [web3       (:web3 eth)
            wei-amount (unit->wei send-amount "ether")
            params     {:from  current-wallet
                        :to    send-address
                        :value wei-amount}]
        (.sendTransaction web3.eth
                          (clj->js params)
                          (send-transaction-callback params))))))

(register-handler
  :add-pending-transaction
  (fn [db [_ {:keys [from hash] :as transaction}]]
    #_(println :add-pending-transaction transaction)
    (let [timestamp (.getTime (js/Date.))]
      (assoc-in db [:pending-transactions from hash]
                (assoc transaction :timestamp timestamp)))))

(register-handler
  :clear-pending-transactions
  (fn [db [_ account hashes]]
    (update-in db [:pending-transactions account] apply dissoc hashes)))
