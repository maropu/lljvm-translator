; ModuleID = 'trunc.bc'
source_filename = "./trunc.ll"

define i16 @trunc(i64 %x) {
  %ret = trunc i64 %x to i16
  ret i16 %ret
}

