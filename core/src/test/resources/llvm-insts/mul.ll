; ModuleID = 'mul.bc'
source_filename = "./mul.ll"
target triple = "x86_64-apple-macosx10.12.0"

define i32 @mul(i32, i32) {
  %ret = mul nsw i32 %0, %1
  ret i32 %ret
}

