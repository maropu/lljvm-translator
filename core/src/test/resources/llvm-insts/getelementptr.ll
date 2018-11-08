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

define i32 @getelementptr4({ i32, { i32, i32 }* }* %x) {
  %1 = getelementptr inbounds { i32, { i32, i32 }* }, { i32, { i32, i32 }* }* %x, i64 1, i32 1
  %2 = load { i32, i32 }*, { i32, i32 }** %1, align 4
  %3 = load { i32, i32 }, { i32, i32 }* %2, align 4
  %ret = extractvalue { i32, i32 } %3, 1
  ret i32 %ret
}

define i32 @getelementptr5({ i32, [2 x i32] }* %x) {
  %1 = getelementptr inbounds { i32, [2 x i32] }, { i32, [2 x i32] }* %x, i64 1, i32 1, i32 0
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

define i32 @getelementptr6({ i32, <2 x i32> }* %x) {
  %1 = getelementptr inbounds { i32, <2 x i32> }, { i32, <2 x i32> }* %x, i64 1, i32 1, i32 0
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}
