def jacobi_relax_core(A, Anew):
  error = 0.0
  n = A.shape[0]
  m = A.shape[1]

  for j in range(1, n - 1):
    for i in range(1, m - 1):
      Anew[j, i] = 0.25 * ( A[j, i + 1] + A[j, i - 1] + A[j - 1, i] + A[j + 1, i])
      error = max(error, abs(Anew[j, i] - A[j, i]))
  return error
