(ns token.components.slick
  (:require react-slick
            [reagent.core :as r]))

(def slick (r/adapt-react-class js/Slider))
