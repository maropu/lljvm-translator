#include <math.h>
#include <stdio.h>

#include "math_pow1_test.c"
#include "math_pow2_test.c"

double func_call_test(double x, double y) {
  return math_pow1_test(x, y) + math_pow2_test(x, y);
}
