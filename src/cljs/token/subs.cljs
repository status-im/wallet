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
