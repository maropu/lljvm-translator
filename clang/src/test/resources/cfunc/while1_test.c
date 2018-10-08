#include <math.h>
#include <stdio.h>

int while1_test(int x[], size_t s) {
  int sum = 0;
  int i = 0;
  while (i < s) {
    sum += x[i];
    i++;
  }
  return sum;
}
