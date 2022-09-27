(ns telegram.updates-test
  (:require [telegram.updates :as sut]
            [telegram.schema :as s]
            [clojure.test.check.generators :as gen]
            [expectations.clojure.test
             :refer [defexpect expect expecting
                     approximately between between' functionally
                     side-effects]]
            [malli.generator :as mg]))

(def sample (gen/sample (mg/generator s/text-update) 100))

(mapv #(when-not (= :text (sut/update-type %)) (def u %)) sample)

;; => {:update-id 1471461107,
;;     :message
;;     {:message-id 1008857514,
;;      :from
;;      {:id -6361258868,
;;       :is-bot true,
;;       :first-name "eH1MSiE0MGL97u6jLWP3GqZF634YI4Y8z68N49",
;;       :username "ôT",
;;       :language-code "2BQ23KYH7WImW90CTb1G7G5Rf0lTzH57d8ry7Md97116FGFJTrt6358f8IqEo6Wy"},
;;      :chat {:id 1, :first-name "HqN1WkTdcLmy34J80JZNx7v", :username ">°", :type "private", :last-name "­"},
;;      :date 121809458924,
;;      :text "6V3z1813Ro3126v15qyTbV3Xv92s846Gj7Hz47n60Fei2",
;;      :entities
;;      [{:offset 360164, :length 21516678459075881, :type "bot_command"}
;;       {:offset 58875048145, :length 231878891054297, :type "bot_command"}
;;       {:offset 13, :length 902701799, :type "bot_command"}
;;       {:offset 195160859, :length 932743947529, :type "bot_command"}
;;       {:offset 21, :length 167126883206071473, :type "bot_command"}
;;       {:offset 843758665781431, :length 3536959338, :type "bot_command"}
;;       {:offset 4918888555555420482, :length 1914766, :type "bot_command"}
;;       {:offset 3520981991298, :length 65409502, :type "bot_command"}
;;       {:offset 46697, :length 64777393995, :type "bot_command"}
;;       {:offset 50579, :length 1, :type "bot_command"}
;;       {:offset 5479775562, :length 1822577, :type "bot_command"}
;;       {:offset 17034956329904, :length 1, :type "bot_command"}
;;       {:offset 1470588892462836355, :length 87344026912351, :type "bot_command"}
;;       {:offset 162675, :length 3000978, :type "bot_command"}
;;       {:offset 15602, :length 9083642, :type "bot_command"}
;;       {:offset 18244833681605, :length 90828571829627, :type "bot_command"}
;;       {:offset 2613696124276855961, :length 2248, :type "bot_command"}
;;       {:offset 15, :length 438418, :type "bot_command"}
;;       {:offset 1205910533, :length 96823927414391, :type "bot_command"}
;;       {:offset 3894125, :length 1054444695, :type "bot_command"}
;;       {:offset 16569, :length 5490030177, :type "bot_command"}
;;       {:offset 1443, :length 2046226428854976502, :type "bot_command"}
;;       {:offset 34401716826447536, :length 1011362610, :type "bot_command"}
;;       {:offset 31766136, :length 16, :type "bot_command"}
;;       {:offset 122496252, :length 1314429413190, :type "bot_command"}
;;       {:offset 803414, :length 4, :type "bot_command"}
;;       {:offset 12692618, :length 309981634344, :type "bot_command"}
;;       {:offset 766387, :length 885987973416, :type "bot_command"}
;;       {:offset 72, :length 2, :type "bot_command"}
;;       {:offset 1840, :length 903832782869987, :type "bot_command"}
;;       {:offset 109490590, :length 4, :type "bot_command"}
;;       {:offset 48, :length 3949840382600586575, :type "bot_command"}
;;       {:offset 1118, :length 1499404118, :type "bot_command"}
;;       {:offset 12198649576569987, :length 25752617601861454, :type "bot_command"}]}}
