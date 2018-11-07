; ModuleID = 'getelementptr.bc'
source_filename = "getelementptr.cc"

define i32 @getelementptr1(i32* %x) {
  %1 = getelementptr inbounds i32, i32* %x, i64 3
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

define i32 @getelementptr2({ i32, i32 }* %x) {
  %1 = getelementptr inbounds { i32, i32 }, { i32, i32 }* %x, i64 1, i32 1
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

define double @getelementptr3({ i32, { double } }* %x) {
  %1 = getelementptr inbounds { i32, { double } }, { i32, { double } }* %x, i64 1, i32 1, i32 0
  %ret = load double, double* %1, align 4
  ret double %ret
}

define i64 @getelementptr4({ [2 x i64], double, i32 }* %x) {
  %1 = getelementptr inbounds { [2 x i64], double, i32 }, { [2 x i64], double, i32 }* %x, i64 1, i32 0, i32 1
  %ret = load i64, i64* %1, align 4
  ret i64 %ret
}

define i32 @getelementptr5([3 x i32]* %x) {
  %1 = getelementptr inbounds [3 x i32], [3 x i32]* %x, i64 1, i32 2
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

define float @getelementptr6(<2 x float>* %x) {
  %1 = getelementptr inbounds <2 x float>, <2 x float>* %x, i64 1, i32 1
  %ret = load float, float* %1, align 4
  ret float %ret
}

define i64 @getelementptr7(i64* %x) {
  %1 = getelementptr inbounds i64, i64* %x, i64 3
  %ret = load i64, i64* %1, align 4
  ret i64 %ret
}

define float @getelementptr8(float* %x) {
  %1 = getelementptr inbounds float, float* %x, i64 3
  %ret = load float, float* %1, align 4
  ret float %ret
}

define double @getelementptr9(double* %x) {
  %1 = getelementptr inbounds double, double* %x, i64 3
  %ret = load double, double* %1, align 4
  ret double %ret
}

define i64 @getelementptr10(i64* %x, i64 %idx) {
  %1 = getelementptr inbounds i64, i64* %x, i64 %idx
  %ret = load i64, i64* %1, align 4
  ret i64 %ret
}

define i64 @getelementptr11(i64* %x, i32 %idx) {
  %1 = getelementptr inbounds i64, i64* %x, i32 %idx
  %ret = load i64, i64* %1, align 4
  ret i64 %ret
}

define i32 @getelementptr12({ i32, i32 }* %x, i64 %idx1) {
  %1 = getelementptr inbounds { i32, i32 }, { i32, i32 }* %x, i64 %idx1, i32 1
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}
