import numpy as np

def kde(X):
  b = 0.5
  points = np.array([-1.0, 2.0, 5.0])
  N = points.shape[0]
  n = X.shape[0]
  exps = 0
  for i in range(n):
    p = X[i]
    d = (-(p - points)**2) / (2 * b**2)
    m = np.min(d)
    exps += m - np.log(b * N) + np.log(np.sum(np.exp(d - m)))
  return exps
