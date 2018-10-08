#include <math.h>
#include <stdio.h>

float while2_test(float x[], size_t s) {
  float sum = 0;
  int i = 0;
  do { sum += x[i]; } while (++i < s );
  return sum;
}
