; ModuleID = 'shl.bc'
source_filename = "./shl.ll"

define i32 @shl(i32 %x) {
  %1 = shl i32 %x, 2
  ret i32 %1
}

