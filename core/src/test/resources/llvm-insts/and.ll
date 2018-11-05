; ModuleID = 'and.bc'
source_filename = "./and.ll"

define i32 @and(i32, i32) {
  %ret = and i32 %0, %1
  ret i32 %ret
}
