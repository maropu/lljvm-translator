import numpy as np

def numpy_logistic_regression(Y, X, w, iterations):
  for i in range(iterations):
    w -= np.dot(((1.0 / (1.0 + np.exp(-Y * np.dot(X, w))) - 1.0) * Y), X)
  return w
