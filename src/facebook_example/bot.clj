(ns facebook-example.bot
  (:gen-class)
  (:require [clojure.string :as s]
            [environ.core :refer [env]]
            [facebook-example.facebook :as fb]
            [facebook-example.activities.mad-sports :as sports]
            [facebook-example.activities.mad-creativity :as creativity]
            [facebook-example.activities.mad-entertainment :as entertainment]
            [facebook-example.activities.mild-quotes :as quotes]
            [facebook-example.activities.mild-relaxation :as relaxation]
            [facebook-example.activities.mild-meditation :as meditation]))

(defn on-message [payload]
  (println "on-message payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        message-text (get-in payload [:message :text])]

    (cond
      (s/includes? (s/lower-case message-text) "owl") (fb/send-message sender-id (fb/text-message (format "Welcome to your personal bi%clar advisor, doomed being!" (int 129417))))
      :else (fb/send-message sender-id (fb/quick-reply-message "I don't understand text since I'm an owl. You can guide me with simple buttons. How would you like to spend your last days?" [{:content_type "text" :title (format "%c Fun Activities" (int 127881)) :payload "TREE_MAD"} {:content_type "text" :title (format "%c Relax Practices" (int 128524)) :payload "TREE_MILD"}])))))

(defn on-postback [payload]
  (println "on-postback payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        postback (get-in payload [:postback :payload])
        referral (get-in payload [:postback :referral :ref])]

    (cond
      (= postback "GET_STARTED") (fb/send-message sender-id (fb/button-message (format "Welcome to your personal bip%clar advisor, doomed being! You are in an apocalyptic situation %c and want to know how to proceed with life? I provide help with all the lost ones out there." (int 129417) (int 9760))
                                                                               [{:type "postback" :title "Let's save my life" :payload "LETS_SAVE_MY_LIFE"}]))

      (= postback "RESET") (fb/send-message sender-id (fb/button-message (format "%c I'm back in my initial stand." (int 129417))
                                                                         [{:type "postback" :title "Let's start again" :payload "LETS_SAVE_MY_LIFE"}]))

      (= postback "HELP") (fb/send-message sender-id (fb/button-message (format "%c I'm trying my best to guide you. You do know how a button works, don't you? If not, try to send some text." (int 129417))
                                                                        [{:type "postback" :title "Let's start again" :payload "LETS_SAVE_MY_LIFE"}]))


      (= postback "LETS_SAVE_MY_LIFE") [
                                        (fb/send-message sender-id (fb/text-message "Days like these are spent best with some activities."))
                                        (fb/send-message sender-id (fb/quick-reply-message "How would you like to spend these days?" [{:content_type "text" :title (format "%c Fun Activities" (int 127881)) :payload "TREE_MAD"} {:content_type "text" :title (format "%c Relax Practices" (int 128524)) :payload "TREE_MILD"}]))]

      ; MADy part
      (= postback "TREE_MAD_SPORTS") (fb/send-message sender-id (fb/quick-reply-message (sports/randomActivity) [{:content_type "text" :title (format "%c Done" (int 128077)) :payload "MAD_DONE"} {:content_type "text" :title (format "%c Skip" (int 9193)) :payload "MAD_SKIP"} {:content_type "text" :title (format "Speak to MILDy %c" (int 129417)) :payload "TREE_MILD"}]))
      (= postback "TREE_MAD_CREATIVITY") (fb/send-message sender-id (fb/quick-reply-message (creativity/randomActivity) [{:content_type "text" :title (format "%c Done" (int 128077)) :payload "MAD_DONE"} {:content_type "text" :title (format "%c Skip" (int 9193)) :payload "MAD_SKIP"} {:content_type "text" :title (format "Speak to MILDy %c" (int 129417)) :payload "TREE_MILD"}]))
      (= postback "TREE_MAD_ENTERTAINMENT") (fb/send-message sender-id (fb/quick-reply-message (entertainment/randomActivity) [{:content_type "text" :title (format "%c Done" (int 128077)) :payload "MAD_DONE"} {:content_type "text" :title (format "%c Skip" (int 9193)) :payload "MAD_SKIP"} {:content_type "text" :title (format "Speak to MILDy %c" (int 129417)) :payload "TREE_MILD"}]))

      ; MILDy part
      (= postback "TREE_MILD_QUOTE")      [
                                            (fb/send-message sender-id (fb/image-message (quotes/randomImage)))
                                            (fb/send-message sender-id (fb/quick-reply-message (format "What a great quote! Already feel relaxed? %c" (int 128578)) [{:content_type "text" :title (format "%c Done" (int 128077)) :payload "MILD_DONE"} {:content_type "text" :title (format "%c Skip" (int 9193)) :payload "MILD_SKIP"} {:content_type "text" :title (format "Speak to MADy %c" (int 129417)) :payload "TREE_MAD"}]))]
      (= postback "TREE_MILD_MEDITATION") (fb/send-message sender-id (fb/quick-reply-message (meditation/randomActivity) [{:content_type "text" :title (format "%c Done" (int 128077)) :payload "MILD_DONE"} {:content_type "text" :title (format "%c Skip" (int 9193)) :payload "MILD_SKIP"} {:content_type "text" :title (format "Speak to MADy %c" (int 129417)) :payload "TREE_MAD"}]))
      (= postback "TREE_MILD_RELAXATION") (fb/send-message sender-id (fb/quick-reply-message (relaxation/randomActivity) [{:content_type "text" :title (format "%c Done" (int 128077)) :payload "MILD_DONE"} {:content_type "text" :title (format "%c Skip" (int 9193)) :payload "MILD_SKIP"} {:content_type "text" :title (format "Speak to MADy %c" (int 129417)) :payload "TREE_MAD"}]))

      :else (fb/send-message sender-id (fb/text-message "Sorry, I don't know how to handle that postback")))))

(defn on-quickreply [payload]
  (println "on-quickreply payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        message (get-in payload [:message])
        quick-reply (get-in payload [:message :quick_reply])
        quick-reply-payload (get-in payload [:message :quick_reply :payload])]

    (cond
      (= quick-reply-payload "TREE_MAD") (fb/send-message sender-id (fb/button-message (format "Super great %c let's guide you through some MAD activities." (int 128165))
                                                                            [{:type "postback" :title (format "%c Sports" (int 127947)) :payload "TREE_MAD_SPORTS"}
                                                                             {:type "postback" :title (format "%c Creativity" (int 127912)) :payload "TREE_MAD_CREATIVITY"}
                                                                             {:type "postback" :title (format "%c Entertainment" (int 127922)) :payload "TREE_MAD_ENTERTAINMENT"}]))

      (= quick-reply-payload "TREE_MILD") (fb/send-message sender-id (fb/button-message (format "Calming down with some MILD practices %c sounds like a great idea." (int 127752))
                                                                           [{:type "postback" :title (format "%c Quote" (int 128173)) :payload "TREE_MILD_QUOTE"}
                                                                            {:type "postback" :title (format "%c Meditation" (int 128591)) :payload "TREE_MILD_MEDITATION"}
                                                                            {:type "postback" :title (format "%c Relaxation" (int 128134)) :payload "TREE_MILD_RELAXATION"}]))

      (= quick-reply-payload "MAD_DONE") (fb/send-message sender-id (fb/button-message (format "You are great %c" (int 127941))
                                                                            [{:type "postback" :title (format "%c Sports" (int 127947)) :payload "TREE_MAD_SPORTS"}
                                                                             {:type "postback" :title (format "%c Creativity" (int 127912)) :payload "TREE_MAD_CREATIVITY"}
                                                                             {:type "postback" :title (format "%c Entertainment" (int 127922)) :payload "TREE_MAD_ENTERTAINMENT"}]))

      (= quick-reply-payload "MAD_SKIP") (fb/send-message sender-id (fb/button-message (format "Maybe try something else? %c" (int 129300))
                                                                           [{:type "postback" :title (format "%c Sports" (int 127947)) :payload "TREE_MAD_SPORTS"}
                                                                            {:type "postback" :title (format "%c Creativity" (int 127912)) :payload "TREE_MAD_CREATIVITY"}
                                                                            {:type "postback" :title (format "%c Entertainment" (int 127922)) :payload "TREE_MAD_ENTERTAINMENT"}]))


      (= quick-reply-payload "MILD_DONE") (fb/send-message sender-id (fb/button-message (format "Namaste %c You are one step closer to your zen level. " (int 128524))
                                                                          [{:type "postback" :title (format "%c Quote" (int 128173)) :payload "TREE_MILD_QUOTE"}
                                                                           {:type "postback" :title (format "%c Meditation" (int 128591)) :payload "TREE_MILD_MEDITATION"}
                                                                           {:type "postback" :title (format "%c Relaxation" (int 128134)) :payload "TREE_MILD_RELAXATION"}]))

      (= quick-reply-payload "MILD_SKIP") (fb/send-message sender-id (fb/button-message (format "Maybe try something else? %c" (int 129300))
                                                                          [{:type "postback" :title (format "%c Quote" (int 128173)) :payload "TREE_MILD_QUOTE"}
                                                                           {:type "postback" :title (format "%c Meditation" (int 128591)) :payload "TREE_MILD_MEDITATION"}
                                                                           {:type "postback" :title (format "%c Relaxation" (int 128134)) :payload "TREE_MILD_RELAXATION"}]))

      :else (fb/send-message sender-id (fb/text-message "Sorry, I don't know how to handle that quick reply.")))))


(defn on-attachments [payload]
  (println "on-attachment payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        attachments (get-in payload [:message :attachments])]

      (fb/send-message sender-id (fb/quick-reply-message "Thanks for your attachment! But I don't understand them. You can guide me with simple buttons. How would you like to spend your last days?" [{:content_type "text" :title (format "%c Fun Activities" (int 127881)) :payload "TREE_MAD"} {:content_type "text" :title (format "%c Relax Practices" (int 128524)) :payload "TREE_MILD"}]))))
