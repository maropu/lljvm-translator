/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <math.h>
#include <stdio.h>

int cfunc1(int x, int y) {
  return x + y;
}

double cfunc2(double x, double y) {
  return pow(x, y);
}

double cfunc3(double x, double y) {
  return 2 * y + pow(y, x);
}

double cfunc4(double x[], size_t s) {
  double sum = 0.0;
  for (int i = 0; i < s; i++) {
    sum += x[i];
  }
  return sum;
}

int cfunc5(int x) {
  if (x) {
    return 0;
  } else {
    return 1;
  }
}

int cfunc6(int x) {
  return x? 0 : 1;
}

// int cfunc7(double x, double y) {
//   return cfunc2(x, y) + cfunc3(x, y);
// }
