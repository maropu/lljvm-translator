import numpy as np

def logistic_regression(Y, X, w, iterations):
  for i in range(iterations):
    # TODO: We reordered the inputs because of incompatible matrix size
    # w -= np.dot(((1.0 / (1.0 + np.exp(-Y * np.dot(X, w))) - 1.0) * Y), X)
    w -= np.dot(X, ((1.0 / (1.0 + np.exp(-Y * np.dot(X, w))) - 1.0) * Y))
  return w
