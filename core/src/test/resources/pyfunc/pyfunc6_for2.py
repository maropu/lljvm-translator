def pyfunc6_for2(x):
  (s, ) = x.shape
  sum = 0.0
  for i in range(s):
    sum += x[i]
  return sum
