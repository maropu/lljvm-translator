; ModuleID = 'udiv.bc'
source_filename = "./udiv.ll"

define i32 @udiv(i32 %x, i32 %y) {
  %1 = udiv i32 %x, %y
  ret i32 %1
}

