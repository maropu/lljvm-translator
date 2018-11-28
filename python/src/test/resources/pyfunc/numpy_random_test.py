import numpy as np

def numpy_random1_test():
  return np.random.ranf()

def numpy_random2_test(n):
  return np.random.ranf(n)

def numpy_random3_test():
  return np.random.randint(9, size=(3, 3))
