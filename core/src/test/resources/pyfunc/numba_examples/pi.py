import numpy as np

def calc_pi(n):
  x = 2 * np.random.ranf(n) - 1
  y = 2 * np.random.ranf(n) - 1
  return 4 * np.sum(x**2 + y**2 < 1) / n
