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

declare float @llvm.log2.f32(float)
declare double @llvm.log2.f64(double)
declare float @llvm.round.f32(float)
declare double @llvm.round.f64(double)

