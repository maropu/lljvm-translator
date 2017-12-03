#include <math.h>
#include <stdio.h>

float cfunc4_while2(float x[], size_t s) {
  float sum = 0;
  int i = 0;
  do { sum += x[i]; } while (++i < s );
  return sum;
}
