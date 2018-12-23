; ModuleID = 'intrinsics.bc'
source_filename = "./intrinsics.ll"

define float @log2_f32(float %x) {
  %1 = call float @llvm.log2.f32(float %x)
  ret float %1
}

define double @log2_f64(double %x) {
  %1 = call double @llvm.log2.f64(double %x)
  ret double %1
}

define float @round_f32(float %x) {
  %1 = call float @llvm.round.f32(float %x)
  ret float %1
}

define double @round_f64(double %x) {
  %1 = call double @llvm.round.f64(double %x)
  ret double %1
}

define i32 @ctlz_i32(i32 %x) {
  %1 = call i32 @llvm.ctlz.i32(i32 %x, i1 0)
  ret i32 %1
}

define i64 @ctlz_i64(i64 %x) {
  %1 = call i64 @llvm.ctlz.i64(i64 %x, i1 0)
  ret i64 %1
}

define <4 x i32> @ctlz_v2i32(<4 x i32> %x) {
  %1 = call <4 x i32> @llvm.ctlz.v2i32(<4 x i32> %x, i1 0)
  ret <4 x i32> %1
}

declare float @llvm.log2.f32(float)
declare double @llvm.log2.f64(double)
declare float @llvm.round.f32(float)
declare double @llvm.round.f64(double)
; declare i8 @llvm.ctlz.i8(i8, i1)
; declare i16 @llvm.ctlz.i16(i16, i1)
declare i32 @llvm.ctlz.i32(i32, i1)
declare i64 @llvm.ctlz.i64(i64, i1)
declare <4 x i32> @llvm.ctlz.v2i32(<4 x i32>, i1)

