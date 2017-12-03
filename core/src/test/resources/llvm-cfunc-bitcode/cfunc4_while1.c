#include <math.h>
#include <stdio.h>

int cfunc4_while1(int x[], size_t s) {
  int sum = 0;
  int i = 0;
  while (i < s) {
    sum += x[i];
    i++;
  }
  return sum;
}
