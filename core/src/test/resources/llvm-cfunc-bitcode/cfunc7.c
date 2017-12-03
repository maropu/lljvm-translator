#include <math.h>
#include <stdio.h>

#include "cfunc2.c"
#include "cfunc3.c"

double cfunc7(double x, double y) {
  return cfunc2(x, y) + cfunc3(x, y);
}
