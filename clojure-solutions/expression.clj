(ns user
  (:require [clojure.edn :as edn]))

(defn constant [value]
  (fn [_] value))

(defn variable [name]
  (fn [vars] (get vars name 0.0)))

(defn- apply-operation [op identity]
  (fn [& args]
    (fn [vars]
      (let [vals (map #(% vars) args)]
        (cond
          (empty? vals) identity
          (= 1 (count vals)) (first vals)
          :else (reduce op vals))))))

(defn safe-div
  ([] 1.0)
  ([a] (double (/ (double a))))
  ([a b & more]
   (reduce (fn [acc x] (double (/ acc (double x))))
           (double (/ (double a) (double b)))
           more)))

(defn negate [x]
  (fn [vars] (- (x vars))))

(defn add [& args]
  (apply (apply-operation + 0.0) args))

(defn multiply [& args]
  (apply (apply-operation * 1.0) args))

(defn subtract
  [& args]
  (cond
    (empty? args) (constant 0.0)
    (= 1 (count args)) (negate (first args))
    :else (apply (apply-operation - 0.0) args)))

(defn divide [& args]
  (cond
    (empty? args) (constant 1.0)
    (= 1 (count args)) (fn [vars] (/ 1.0 ((first args) vars)))
    :else (apply (apply-operation safe-div 1.0) args)))

(defn sumSinh [& args]
  (fn [vars]
    (reduce + (map #(Math/sinh (% vars)) args))))

(defn sumCosh [& args]
  (fn [vars]
    (reduce + (map #(Math/cosh (% vars)) args))))

(defn meanSinh [& args]
  (fn [vars]
    (let [vals (map #(Math/sinh (% vars)) args)]
      (if (empty? vals)
        0.0
        (/ (reduce + vals) (count vals))))))

(defn meanCosh [& args]
  (fn [vars]
    (let [vals (map #(Math/cosh (% vars)) args)]
      (if (empty? vals)
        0.0
        (/ (reduce + vals) (count vals))))))


(def operations
  {'+ add
   '- subtract
   '* multiply
   '/ divide
   'negate negate
   'sumSinh sumSinh
   'sumCosh sumCosh
   'meanSinh meanSinh
   'meanCosh meanCosh})

(defn parse [expr]
  (cond
    (number? expr) (constant (double expr))
    (symbol? expr) (variable (name expr))
    (list? expr)
    (let [[op & args] expr
          parsed-args (map parse args)
          operation (get operations op)]
      (if (nil? operation)
        (throw (IllegalArgumentException. (str "Unknown operator: " op)))
        (apply operation parsed-args)))
    :else (throw (IllegalArgumentException. (str "Invalid expression: " expr)))))


(defn parseFunction [s]
  (parse (read-string s)))

(defn -main [& args]

)
(-main)