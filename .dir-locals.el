((clojure-mode
  . ((cider-clojure-cli-global-options . "-A:dev")
     (eval . (setenv "BOT_TOKEN" (password-store-get "telegram/testbot"))))
))
