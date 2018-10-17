; ModuleID = 'sdiv.bc'
source_filename = "./sdiv.ll"

define i32 @sdiv(i32 %x, i32 %y) {
  %1 = sdiv i32 %x, %y
  ret i32 %1
}

