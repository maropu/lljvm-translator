def bubblesort(X):
  N = X.shape[0]
  for end in range(N, 1, -1):
    for i in range(end - 1):
      if X[i] > X[i + 1]:
        X[i], X[i + 1] = X[i + 1], X[i]
