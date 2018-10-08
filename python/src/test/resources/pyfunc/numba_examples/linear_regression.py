import numpy as np

def linear_regression(Y, X, w, iterations, alphaN):
  for i in range(iterations):
    w -= alphaN * np.dot(X.T, np.dot(X, w) - Y)
  return w
