(time 
  (let [x (range 1.0 1000000.0 1.1)]
    (reduce + 0.0 x)))

(System/exit 0)
