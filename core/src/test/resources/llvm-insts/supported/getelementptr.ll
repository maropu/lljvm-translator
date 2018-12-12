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

define i32 @getelementptr3({ i32, { i32, i32 } }* %x) {
  %1 = getelementptr inbounds { i32, { i32, i32 } }, { i32, { i32, i32 } }* %x, i64 1, i32 1, i32 0
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

define i32 @getelementptr4({ i32, [2 x i32] }* %x) {
  %1 = getelementptr inbounds { i32, [2 x i32] }, { i32, [2 x i32] }* %x, i64 1, i32 1, i32 0
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

define i32 @getelementptr5({ i32, <2 x i32> }* %x) {
  %1 = getelementptr inbounds { i32, <2 x i32> }, { i32, <2 x i32> }* %x, i64 1, i32 1, i32 0
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

define i32 @getelementptr6(i32* %x) {
  %1 = getelementptr i32, i32* %x, i64 3
  %2 = getelementptr i32, i32* %1, i64 -2
  %ret = load i32, i32* %2, align 4
  ret i32 %ret
}

define i32 @getelementptr7(i32* %x) {
  %1 = getelementptr i32, i32* %x, i32 2
  %2 = getelementptr i32, i32* %1, i32 -1
  %ret = load i32, i32* %2, align 4
  ret i32 %ret
}

define i32 @getelementptr8(i32* %x) {
  %1 = getelementptr i32, i32* %x, i32 3
  %2 = getelementptr i32, i32* %1, i32 -3
  %ret = load i32, i32* %2, align 4
  ret i32 %ret
}

define i32 @getelementptr9(i32* %x) {
  %1 = getelementptr i32, i32* %x, i16 2
  %2 = getelementptr i32, i32* %1, i16 -1
  %ret = load i32, i32* %2, align 4
  ret i32 %ret
}

define i32 @getelementptr10(i32* %x) {
  %1 = getelementptr i32, i32* %x, i8 5
  %2 = getelementptr i32, i32* %1, i8 -2
  %ret = load i32, i32* %2, align 4
  ret i32 %ret
}

define i1 @getelementptr11(i1* %x) {
  %1 = getelementptr i1, i1* %x, i32 3
  %2 = getelementptr i1, i1* %1, i32 -1
  %ret = load i1, i1* %2, align 4
  ret i1 %ret
}

define i32 @getelementptr12(i32* %x) {
  %1 = getelementptr i32, i32* %x, i32 undef
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

define i32 @getelementptr13({ i32, [5 x i32] }* %x, i64 %i) {
  %1 = getelementptr inbounds { i32, [5 x i32] }, { i32, [5 x i32] }* %x, i64 0, i32 1, i64 %i
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

define i32 @getelementptr14({ i32, [5 x i32] }* %x, i32 %i) {
  %1 = getelementptr inbounds { i32, [5 x i32] }, { i32, [5 x i32] }* %x, i64 0, i32 1, i32 %i
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}
