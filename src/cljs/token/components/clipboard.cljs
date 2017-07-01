(ns token.components.clipboard
  (:require [reagent.core :as reagent]
            [cljsjs.clipboard]))

(defn clipboard-button
  [label text]
  (let [clipboard-atom (atom nil)]
    (reagent/create-class
      {:display-name "clipboard-button"
       :component-did-mount
                     #(let [clipboard (new js/Clipboard (reagent/dom-node %))]
                       (reset! clipboard-atom clipboard))
       :component-will-unmount
                     #(when-not (nil? @clipboard-atom)
                       (.destroy @clipboard-atom)
                       (reset! clipboard-atom nil))
       :reagent-render
                     (fn []
                       [:div.button.button-clipboard {:data-clipboard-text text} label])})))