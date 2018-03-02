import numpy as np

def move_mean(a, window_arr, out):
  window_width = window_arr[0]
  asum = 0.0
  count = 0
  for i in range(window_width):
    asum += a[i]
    count += 1
    out[i] = asum / count
  for i in range(window_width, len(a)):
    asum += a[i] - a[i - window_width]
    out[i] = asum / count