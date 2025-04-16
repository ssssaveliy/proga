(ns user)  ; ← Используем пространство `user` вместо `example`

(defn hello [name]
  (str "Hello, " name "!"))

(defn add [& numbers]
  (apply + numbers))