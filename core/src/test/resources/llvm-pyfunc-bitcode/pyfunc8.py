import numpy as np
from numba import cfunc

def pyfunc8(x, y):
  return np.exp(-x) / y**2
