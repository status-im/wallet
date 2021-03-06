(ns token.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
  :name
  (fn [db]
    (reaction (:name @db))))

(re-frame/register-sub
  :active-panel
  (fn [db _]
    (reaction (:active-panel @db))))

(re-frame/register-sub
  :get-in
  (fn [db [_ path]]
    (reaction (get-in @db path))))

(re-frame/register-sub
  :get
  (fn [db [_ key]]
    (reaction (key @db))))

(re-frame/register-sub
  :get-balance
  (fn [db]
    (let [account-id (re-frame/subscribe [:get :current-wallet])]
      (reaction (get-in @db [:eth :balance @account-id])))))

(re-frame/register-sub
  :send-amount
  (fn []
    (let [send-amount    (re-frame/subscribe [:get :send-amount])
          send-amount-qr (re-frame/subscribe [:get :send-amount-qr])]
      (reaction @send-amount)
      #_(reaction (or @send-amount-qr @send-amount)))))

(re-frame/register-sub
  :pending-transactions
  (fn [db [_ wallet-id]]
    (reaction (vals (get-in @db [:pending-transactions wallet-id])))))
