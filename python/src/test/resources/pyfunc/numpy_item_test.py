import numpy as np

def numpy_item1_test(ar, x):
  return ar.item(x)

def numpy_item2_test(ar, x, y):
  return ar.item((x, y))
