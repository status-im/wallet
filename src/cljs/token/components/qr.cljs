(ns token.components.qr
  (:require [reagent.core :as reagent]
            [qrcode]))

(defn get-qr-size
  [w]
  (cond
    (< w 256) 128
    (< w 650) 256
    :else 512))

(defn qr-popup
  [label text]
  (let [show? (reagent/atom false)]
    (reagent/create-class
     {:display-name "clipboard-button"
      :component-did-mount
      #(let [dom-node (aget (js/document.getElementsByClassName "qr-popup-content-qr") 0)
             w (get-qr-size js/window.innerWidth)
             qr (js/QRCode. dom-node
                            #js {:text text
                                 :width w
                                 :height w
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
          [:div.qr-popup-content
           [:span.qr-popup-close {:on-click #(swap! show? (fn [] false))} "×"]
           [:div.qr-popup-content-qr]
           [:span {:style {:display "none"}} @show?]]]])})))
