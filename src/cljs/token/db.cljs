(ns token.db)

(def default-db {:eth {:rpc-url      "http://localhost:8545"
                       :web3         nil
                       :balance      {}
                       :transactions {}
                       :accounts     []}})

(def wallet-accounts-path [:eth :accounts])

(defn wallet-balance-path [wallet-id]
  [:eth :balance wallet-id])

(defn wallet-transactions-path [wallet-id]
  [:eth :transactions wallet-id])
