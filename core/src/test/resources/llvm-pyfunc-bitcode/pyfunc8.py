import numpy as np
from numba import cfunc

def pyfunc8(x, y):
  return np.power(-x, 4) / y
