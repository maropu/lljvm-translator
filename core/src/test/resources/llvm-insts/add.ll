; ModuleID = 'add.bc'
source_filename = "./add.ll"
target triple = "x86_64-apple-macosx10.12.0"

define i32 @add(i32, i32) {
  %ret = add nsw i32 %0, %1
  ret i32 %ret
}

