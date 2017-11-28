#include <math.h>
#include <stdio.h>

#include "cfunc2.c"
#include "cfunc3.c"

double cfunc4_for2(double x[], size_t s) {
  double aggVal = 0.0;
  for (int i = 0; i < s; i++) {
    for (int j = 0; j < 3; j++) {
      double t1 = cfunc2(x[i], 2.0);
      double t2 = cfunc3(t1, x[i]);
      aggVal += t1 + t2;
    }
  }
  return aggVal;
}
