def sum2d(arr):
  M, N = arr.shape
  result = 0.0
  for i in range(M):
    for j in range(N):
      result += arr[i, j]
  return result
