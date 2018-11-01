; ModuleID = 'alloca.bc'
source_filename = "./alloca.ll"

define i32 @alloca(i32 %x) {
  %1 = alloca i32, align 4
  store i32 %x, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  %ret = add nsw i32 %2, 4
  ret i32 %ret
}

define float @plus_finf() {
  %1 = alloca float, align 4
  store float 0x7FF0000000000000, float* %1, align 4
  %ret = load float, float* %1, align 4
  ret float %ret
}

define float @minus_finf() {
  %1 = alloca float, align 4
  store float 0xFFF0000000000000, float* %1, align 4
  %ret = load float, float* %1, align 4
  ret float %ret
}

define float @fnan() {
  %1 = alloca float, align 4
  store float 0xFFF8000000000000, float* %1, align 4
  %ret = load float, float* %1, align 4
  ret float %ret
}

define double @plus_dinf() {
  %1 = alloca double, align 4
  store double 0x7FF0000000000000, double* %1, align 4
  %ret = load double, double* %1, align 4
  ret double %ret
}

define double @minus_dinf() {
  %1 = alloca double, align 4
  store double 0xFFF0000000000000, double* %1, align 4
  %ret = load double, double* %1, align 4
  ret double %ret
}

define double @dnan() {
  %1 = alloca double, align 4
  store double 0xFFF8000000000000, double* %1, align 4
  %ret = load double, double* %1, align 4
  ret double %ret
}
