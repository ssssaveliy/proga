(ns user)

(defn is-vector? [v]
  (and (vector? v)
       (every? number? v)))

(defn is-matrix? [m]
  (and (vector? m)
       (every? is-vector? m)
       (apply = (map count m))))

(defn same-length? [v1 v2]
  (and (is-vector? v1)
       (is-vector? v2)
       (== (count v1) (count v2))))

(defn same-dimensions? [m1 m2]
  (and (is-matrix? m1)
       (is-matrix? m2)
       (== (count m1) (count m2))
       (== (count (first m1)) (count (first m2)))))

(defn element-wise-vector-op [op & vectors]
  {:pre [(every? is-vector? vectors)]}
  (cond
    (empty? vectors) []
    (every? empty? vectors) []
    :else
    (let [lengths (map count vectors)]
      (assert (apply = lengths) "All vectors must have the same length")
      (try
        (apply mapv op vectors)
        (catch ClassCastException e
          (throw (IllegalArgumentException. "All arguments must be vectors of numbers")))))))

(defn element-wise-matrix-op [op & matrices]
  {:pre [(every? is-matrix? matrices)]}
  (cond
    (empty? matrices) []
    (every? empty? matrices) []
    :else
    (let [dims (map (juxt count (comp count first)) matrices)]
      (assert (apply = dims) "All matrices must have the same dimensions")
      (apply mapv (partial element-wise-vector-op op) matrices))))

(def v+ (partial element-wise-vector-op +))
(def v- (partial element-wise-vector-op -))
(def v* (partial element-wise-vector-op *))
(def vd (partial element-wise-vector-op /))
(def m+ (partial element-wise-matrix-op +))
(def m- (partial element-wise-matrix-op -))
(def m* (partial element-wise-matrix-op *))
(def md (partial element-wise-matrix-op /))

(defn v*s [v & scalars]
  {:pre [(is-vector? v) (every? number? scalars)]}
  (let [k (reduce * 1 scalars)]
    (mapv #(* % k) v)))

(defn scalar
  [& vectors]
  {:pre [(pos? (count vectors))
         (every? is-vector? vectors)
         (apply = (map count vectors))]}
  (if (= 1 (count vectors))
    (reduce + (first vectors))
    (->> vectors
         (apply mapv *)
         (reduce +))))

(defn vect
  [& vectors]
  {:pre [(pos? (count vectors))
         (every? #(and (is-vector? %) (= 3 (count %))) vectors)]}
  (if (= 1 (count vectors))
    (first vectors)
    (reduce
     (fn [[a1 a2 a3] [b1 b2 b3]]
       [(- (* a2 b3) (* a3 b2))
        (- (* a3 b1) (* a1 b3))
        (- (* a1 b2) (* a2 b1))])
     vectors)))

(defn m*s [m & scalars]
  {:pre [(is-matrix? m) (every? number? scalars)]}
  (let [k (reduce * 1 scalars)]
    (mapv #(v*s % k) m)))

(defn transpose [matrix]
  (apply mapv vector matrix))

(defn m*v [matrix v]
  (mapv #(scalar % v) matrix))

(defn m*m-2 [m1 m2]
  (let [m2-cols (transpose m2)]
    (mapv (fn [row] (mapv #(scalar row %) m2-cols)) m1)))

(defn m*m
  [& matrices]
  {:pre [(every? is-matrix? matrices)]}
  (reduce
   (fn [m1 m2]
     (assert (== (count (first m1)) (count m2))
             (str "Matrix dimensions mismatch: "
                  (count (first m1)) " != " (count m2)))
     (m*m-2 m1 m2))
   matrices))

;//:NOTE: modife ↓

(defn scal-to-vec [elem]
  (if (number? elem) [elem] elem))

(defn promote-to-vector [x]
  (if (every? number? x)
    x
    (mapv scal-to-vec x)))

(defn pad-to-length [v n filler]
  (vec (concat v (repeat (- n (count v)) filler))))

(defn filler-for [x op]
  (cond
    (= op /) 1
    :else 0))

(defn broadcast-recurse [op args]
  (let [args (promote-to-vector args)]

    (cond
      (every? number? args)
      (do
        (let [result (apply op args)]
          result))

      (every? #(and (vector? %) (every? number? %)) args)
      (do
        (let [max-length (apply max (map count args))
              padded (map #(pad-to-length % max-length 0) args)]
          (let [result (apply mapv op padded)]
            result)))

      (every? vector? args)
      (do
        (let [max-length (apply max (map count args))
              padded (map (fn [v]
                            (let [f (filler-for v op)
                                  padded-v (pad-to-length v max-length f)]
                              padded-v))
                          args)]
          (let [result (apply mapv (fn [& elems]
                                     (broadcast-recurse op elems)) padded)]
            result)))

      :else
      (do
        (throw (IllegalArgumentException. (str "Incompatible arguments: " args)))))))



(defn make-broadcast-op [op]
  (fn [& args]
    (let [result (broadcast-recurse op args)]
      (if (and (vector? result) (every? number? args))
        (first result)
        result))))

(def b+ (make-broadcast-op +))
(def b- (make-broadcast-op -))
(def b* (make-broadcast-op *))
(defn safe-div
  ([] 1.0)
  ([a] (double (/ (double a))))
  ([a b & more]
   (reduce (fn [acc x] (double (/ acc (double x))))
           (double (/ (double a) (double b)))
           more)))
(def bd (make-broadcast-op safe-div))
