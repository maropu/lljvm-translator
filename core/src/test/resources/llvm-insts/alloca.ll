; ModuleID = 'alloca.bc'
source_filename = "./alloca.ll"

define i32 @alloca1(i32 %x) {
  %1 = alloca i32, align 4
  store i32 %x, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  %ret = add nsw i32 %2, 4
  ret i32 %ret
}

define { i32*, i64*, <2 x i32> } @alloca2(<2 x i32> %x) {
  %1 = alloca { i32*, i64*, <2 x i32> }, align 4
  %2 = load { i32*, i64*, <2 x i32> }, { i32*, i64*, <2 x i32> }* %1, align 4
  %ret = insertvalue { i32*, i64*, <2 x i32> } %2, <2 x i32> %x, 2
  ret { i32*, i64*, <2 x i32> } %ret
}

define { i64*, [2 x double], [4 x i32] } @alloca3([2 x double] %x) {
  %1 = alloca { i64*, [2 x double], [4 x i32] }, align 4
  %2 = load { i64*, [2 x double], [4 x i32] }, { i64*, [2 x double], [4 x i32] }* %1, align 4
  %ret = insertvalue { i64*, [2 x double], [4 x i32] } %2, [2 x double] %x, 1
  ret { i64*, [2 x double], [4 x i32] } %ret
}

define { i32, { i32, double } } @alloca4({ i32, double } %x) {
  %1 = alloca { i32, { i32, double } }, align 4
  %2 = load { i32, { i32, double } }, { i32, { i32, double } }* %1, align 4
  %ret = insertvalue { i32, { i32, double } } %2, { i32, double } %x, 1
  ret { i32, { i32, double } } %ret
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
