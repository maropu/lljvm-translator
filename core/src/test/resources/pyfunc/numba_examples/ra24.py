import numpy as np
import math

def ra_numba(doy, lat):
  ra = np.zeros_like(lat)
  Gsc = 0.0820

  pi = math.pi

  dr = 1 + 0.033 * math.cos( 2 * pi / 365 * doy)
  decl = 0.409 * math.sin(2 * pi / 365 * doy - 1.39)
  tan_decl = math.tan(decl)
  cos_decl = math.cos(decl)
  sin_decl = math.sin(decl)

  for idx, latval in np.ndenumerate(lat):
    ws = math.acos(-math.tan(latval) * tan_decl)
    ra[idx] = 24 * 60 / pi * Gsc * dr * ( ws * math.sin(latval) * sin_decl + math.cos(latval) * cos_decl * math.sin(ws)) * 11.6

  return ra
