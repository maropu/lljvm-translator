; ModuleID = 'sub.bc'
source_filename = "./sub.ll"

define i32 @sub(i32, i32) {
  %ret = sub nsw i32 %0, %1
  ret i32 %ret
}

