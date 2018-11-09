; ModuleID = 'urem.bc'
source_filename = "./urem.ll"

define i32 @urem(i32 %x, i32 %y) {
  %1 = urem i32 %x, %y
  ret i32 %1
}

