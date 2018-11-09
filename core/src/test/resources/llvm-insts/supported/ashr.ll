; ModuleID = 'ashr.bc'
source_filename = "./ashr.ll"

define i32 @ashr(i32 %x) {
  %1 = ashr i32 %x, 2
  ret i32 %1
}

