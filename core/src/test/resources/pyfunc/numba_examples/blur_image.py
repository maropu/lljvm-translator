import numpy
from numba import jit, float64

@jit("void(float64[:,:], float64[:,:], float64[:,:])")
def filter2d_core(image, filt, result):
  M, N = image.shape
  Mf, Nf = filt.shape
  Mf2 = Mf // 2
  Nf2 = Nf // 2
  for i in range(Mf2, M - Mf2):
    for j in range(Nf2, N - Nf2):
      num = 0
      for ii in range(Mf):
        for jj in range(Nf):
          num += (filt[Mf - 1 - ii, Nf - 1 - jj] * image[i - Mf2 + ii, j - Nf2 + jj])
      result[i, j] = num

def filter2d(image, filt):
  result = numpy.zeros_like(image)
  filter2d_core(image, filt, result)
  return result
