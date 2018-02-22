import numpy as np
from numba import cfunc

def numpy_power_test(x, y):
  return np.power(-x, 4) / y
