; ModuleID = 'insertelement.bc'
source_filename = "./insertelement.ll"

define <4 x i32> @insertelement1(<4 x i32> %x) {
  %ret = insertelement <4 x i32> %x, i32 4, i32 3
  ret <4 x i32> %ret
}

; Test for the reference of old snapshot variables
define i64 @insertelement2(<3 x i64> %x) {
  %1 = insertelement <3 x i64> %x, i64 1, i32 1
  %2 = insertelement <3 x i64> %1, i64 1, i32 0
  %3 = insertelement <3 x i64> %2, i64 1, i32 2
  %4 = add nsw <3 x i64> %3, %x
  %5 = extractelement <3 x i64> %4, i32 0
  %6 = extractelement <3 x i64> %4, i32 1
  %7 = extractelement <3 x i64> %4, i32 2
  %8 = add nsw i64 %5, %6
  %ret = add nsw i64 %8, %7
  ret i64 %ret
}
