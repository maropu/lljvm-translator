; ModuleID = 'lshr.bc'
source_filename = "./lshr.ll"

define i32 @lshr(i32 %x) {
  %1 = lshr i32 %x, 2
  ret i32 %1
}

