; ModuleID = 'extractvalue.bc'
source_filename = "./extractvalue.ll"

define i32 @extractvalue1({ i32, i32 } %x) {
  %ret = extractvalue { i32, i32 } %x, 1
  ret i32 %ret
}

define i64 @extractvalue2([2 x i64] %x) {
  %1 = extractvalue [2 x i64] %x, 0
  %2 = extractvalue [2 x i64] %x, 1
  %ret = add nsw i64 %1, %2
  ret i64 %ret
}

define i32 @extractvalue3({ i32, <2 x i32>, <3 x i32> } %x) {
  %1 = extractvalue { i32, <2 x i32>, <3 x i32> } %x, 0
  %2 = extractvalue { i32, <2 x i32>, <3 x i32> } %x, 1
  %3 = extractelement <2 x i32> %2, i32 0
  %4 = extractelement <2 x i32> %2, i32 1
  %5 = extractvalue { i32, <2 x i32>, <3 x i32> } %x, 2
  %6 = extractelement <3 x i32> %5, i32 0
  %7 = extractelement <3 x i32> %5, i32 1
  %8 = extractelement <3 x i32> %5, i32 2
  %9 = add nsw i32 %1, %3
  %10 = add nsw i32 %4, %9
  %11 = add nsw i32 %6, %10
  %12 = add nsw i32 %7, %11
  %ret = add nsw i32 %8, %12
  ret i32 %ret
}
