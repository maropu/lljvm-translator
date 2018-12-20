; ModuleID = 'alloca.bc'
source_filename = "./alloca.ll"

define i32 @alloca1(i32 %x) {
  %1 = alloca i32, align 4
  store i32 %x, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  %ret = add nsw i32 %2, 4
  ret i32 %ret
}

define { i32*, i64*, <3 x i32> }* @alloca2() {
  %ret = alloca { i32*, i64*, <3 x i32> }, align 4
  ret { i32*, i64*, <3 x i32> }* %ret
}

define { i32*, <2 x i32> }* @alloca3() {
  %1 = alloca { i32*, <2 x i32> }*, align 4
  %2 = alloca { i32*, <2 x i32> }, align 4
  store { i32*, <2 x i32> }* %2, { i32*, <2 x i32> }** %1, align 4
  %ret = load { i32*, <2 x i32> }*, { i32*, <2 x i32> }** %1, align 4
  ret { i32*, <2 x i32> }* %ret
}

define { i32*, i64*, { i32, i64} }* @alloca4() {
  %ret = alloca { i32*, i64*, { i32, i64 } }, align 4
  ret { i32*, i64*, { i32, i64 } }* %ret
}

define { i32*, i64*, [3 x i32] }* @alloca5() {
  %ret = alloca { i32*, i64*, [3 x i32] }, align 4
  ret { i32*, i64*, [3 x i32] }* %ret
}

define [3 x i32]* @alloca6() {
  %ret = alloca [3 x i32], align 4
  ret [3 x i32]* %ret
}

define [3 x i32]* @alloca7() {
  %1 = alloca [3 x i32]*, align 4
  %2 = alloca [3 x i32], align 4
  store [3 x i32]* %2, [3 x i32]** %1, align 4
  %ret = load [3 x i32]*, [3 x i32]** %1, align 4
  ret [3 x i32]* %ret
}

define <3 x i32>* @alloca8() {
  %ret = alloca <3 x i32>, align 4
  ret <3 x i32>* %ret
}

define <3 x i32>* @alloca9() {
  %1 = alloca <3 x i32>*, align 4
  %2 = alloca <3 x i32>, align 4
  store <3 x i32>* %2, <3 x i32>** %1, align 4
  %ret = load <3 x i32>*, <3 x i32>** %1, align 4
  ret <3 x i32>* %ret
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

define <4 x double>* @alloca10() {
  %1 = alloca <4 x double>, align 8
  store <4 x double> <double 1.000000e+00, double 2.000000e+00, double 3.000000e+00, double 4.000000e+00>, <4 x double>* %1, align 8
  ret <4 x double>* %1
}

define <4 x double>* @alloca11() {
  %1 = alloca <4 x double>, align 8
  store <4 x double> <double 5.000000e+00, double undef, double undef, double undef>, <4 x double>* %1, align 8
  ret <4 x double>* %1
}

define <4 x double>* @alloca12() {
  %1 = alloca <4 x double>, align 8
  store <4 x double> undef, <4 x double>* %1, align 8
  ret <4 x double>* %1
}

define <4 x double>* @alloca13() {
  %1 = alloca <4 x double>, align 8
  store <4 x double> <double 0.000000e+00, double 0.000000e+00, double 0.000000e+00, double 0.000000e+00>, <4 x double>* %1, align 8
  ret <4 x double>* %1
}

define <4 x double>* @alloca14(<4 x double> %x) {
  %1 = alloca <4 x double>, align 8
  store <4 x double> %x, <4 x double>* %1, align 8
  ret <4 x double>* %1
}
