; ModuleID = 'srem.bc'
source_filename = "./srem.ll"

define i32 @srem(i32 %x, i32 %y) {
  %1 = srem i32 %x, %y
  ret i32 %1
}

