; ModuleID = 'sext.bc'
source_filename = "./sext.ll"

define i64 @sext(i32 %x) {
  %ret = sext i32 %x to i64
  ret i64 %ret
}

