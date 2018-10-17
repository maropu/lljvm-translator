; ModuleID = 'or.bc'
source_filename = "./or.ll"

define i32 @or(i32, i32) {
  %ret = or i32 %0, %1
  ret i32 %ret
}

