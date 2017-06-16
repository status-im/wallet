(ns token.components.qr
  (:require [reagent.core :as reagent]
            [qrcode]))

(defn qr-popup
  [label text]
  (let [show? (reagent/atom false)]
    (reagent/create-class
     {:display-name "clipboard-button"
      :component-did-mount
      #(let [dom-node (aget (js/document.getElementsByClassName "qr-popup") 0)
             qr (js/QRCode. dom-node
                            #js {:text text
                                 :width 512
                                 :height 512
                                 :colorDark "#000000"
                                 :colorLight  "#ffffff",
                                 :correctLevel js/QRCode.CorrectLevel.H})]
         qr)
      :reagent-render
      (fn []
        [:div
         [:div.button.button-clipboard
          {:on-click #(swap! show? (fn [] true))}
          label]
         [:div.qr-popup {:style {:display (if @show? "block" "none")}}
          [:span.qr-close {:on-click #(swap! show? (fn [] false))} "x"]
          [:span {:style {:display "none"}} @show?]]])})))
