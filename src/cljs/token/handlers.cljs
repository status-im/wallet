(ns token.handlers
  (:require [re-frame.core :as re-frame]
            [token.db :as db]
            [token.ethereum :refer [connect accounts balance transactions wei->ether]]))

(re-frame/register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/register-handler
  :initialize-wallet
  (fn initialize-wallet
    [{:keys [eth] :as db} _]
    (let [rpc-url (:rpc-url eth)
          web3    (connect rpc-url)]
      (accounts web3
                (fn [error result]
                  (when error
                    (println (str "error: " error)))
                  (re-frame/dispatch
                    [:set-accounts result])))
      (assoc-in db [:eth :web3] web3))))

(re-frame/register-handler
  :set-accounts
  (fn set-accounts
    [db [_ accounts]]
    (doall (map #(re-frame/dispatch [:get-balance %]) accounts))
    (assoc-in db db/wallet-accounts-path accounts)))

(re-frame/register-handler
  :get-balance
  (fn get-balance
    [db [_ account]]
    (let [web3 (get-in db [:eth :web3])]
      (balance web3 account
               (fn [error result]
                 (when error
                   (println (str "error: " error)))
                 (re-frame/dispatch
                   [:set-balance account (wei->ether result)])))
      db)))

(re-frame/register-handler
  :set-balance
  (fn set-balance
    [db [_ account balance]]
    (assoc-in db (db/wallet-balance-path account) balance)))

(re-frame/register-handler
  :get-transactions
  (fn get-transactions
    [db [_ account]]
    (transactions account
                  (fn [txs]
                    (re-frame/dispatch
                      ;; todo: takes 10 transactions
                      [:set-transactions account (take 10 txs)])))
    db))

(re-frame/register-handler
  :set-transactions
  (fn set-transactions
    [db [_ account transactions]]
    (assoc-in db (db/wallet-transactions-path account) transactions)))

(re-frame/register-handler
  :set-active-panel
  (fn [db [_ active-panel params]]
    (assoc db :active-panel [active-panel params])))
