; ModuleID = 'xor.bc'
source_filename = "./xor.ll"

define i32 @xor(i32, i32) {
  %ret = xor i32 %0, %1
  ret i32 %ret
}

