def for2_test(x):
  (s, ) = x.shape
  sum = 0.0
  for i in range(s):
    sum += x[i]
  return sum
