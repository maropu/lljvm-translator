from numba import jit, float64

@jit("float64(float64, float64)")
def func1(x, y):
  return x + y

@jit("float64(float64, float64)")
def func2(x, y):
  return x * y

def func_call_test(x, y):
  return func1(x, y) + func2(x, y)
