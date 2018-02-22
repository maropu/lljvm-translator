#include <math.h>
#include <stdio.h>

#include "math_pow1_test.c"
#include "math_pow2_test.c"

double for2_test(double x[], size_t s) {
  double aggVal = 0.0;
  for (int i = 0; i < s; i++) {
    for (int j = 0; j < 3; j++) {
      double t1 = math_pow1_test(x[i], 2.0);
      double t2 = math_pow2_test(t1, x[i]);
      aggVal += t1 + t2;
    }
  }
  return aggVal;
}
