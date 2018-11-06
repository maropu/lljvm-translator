; ModuleID = 'insertvalue.bc'
source_filename = "./insertvalue.ll"

define { double, double } @insertvalue1({ double, double } %x) {
  %ret = insertvalue { double, double } %x, double 7.000000e+00, 1
  ret { double, double } %ret
}

define { i32, double, i32 } @insertvalue2({ i32, double, i32 } %x) {
  %ret = insertvalue { i32, double, i32 } %x, i32 5, 0
  ret { i32, double, i32 } %ret
}

define { i32, { i32 }, i32 } @insertvalue3({ i32, { i32 }, i32 } %x, { i32 } %y) {
  %ret = insertvalue { i32, { i32 }, i32 } %x, { i32 } %y, 1
  ret { i32, { i32 }, i32 } %ret
}

define { i32, [2 x i32] } @insertvalue4({ i32, [2 x i32] } %x, [2 x i32] %y) {
  %ret = insertvalue { i32, [2 x i32] } %x, [2 x i32] %y, 1
  ret { i32, [2 x i32] } %ret
}

define { i32, [3 x i64] } @insertvalue5({ i32, [3 x i64] } %x) {
  %ret = insertvalue { i32, [3 x i64] } %x, i32 4, 0
  ret { i32, [3 x i64] } %ret
}

define { i32, i32, i32 } @insertvalue6() {
  %ret = insertvalue { i32, i32, i32 } undef, i32 3, 1
  ret { i32, i32, i32 } %ret
}

define { i32, <3 x i32> } @insertvalue7({ i32, <3 x i32> } %x) {
  %ret = insertvalue { i32, <3 x i32> } %x, i32 4, 0
  ret { i32, <3 x i32> } %ret
}

define { i64, <2 x i32> } @insertvalue8({ i64, <2 x i32> } %x, <2 x i32> %y) {
  %ret = insertvalue { i64, <2 x i32> } %x, <2 x i32> %y, 1
  ret { i64, <2 x i32> } %ret
}

define [3 x i32] @insertvalue9([3 x i32] %x) {
  %ret = insertvalue [3 x i32] %x, i32 9, 2
  ret [3 x i32] %ret
}

; Test for the reference of old snapshot variables
define i32 @insertvalue10({ i32, i32 } %x) {
  %1 = insertvalue { i32, i32 } %x, i32 2, 1
  %2 = insertvalue { i32, i32 } %1, i32 2, 0
  %3 = extractvalue { i32, i32 } %x, 0
  %4 = extractvalue { i32, i32 } %x, 1
  %5 = extractvalue { i32, i32 } %1, 0
  %6 = extractvalue { i32, i32 } %1, 1
  %7 = extractvalue { i32, i32 } %2, 0
  %8 = extractvalue { i32, i32 } %2, 1
  %9 = add nsw i32 %3, %4
  %10 = add nsw i32 %9, %5
  %11 = add nsw i32 %10, %6
  %12 = add nsw i32 %11, %7
  %ret = add nsw i32 %12, %8
  ret i32 %ret
}

