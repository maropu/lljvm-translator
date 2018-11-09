; ModuleID = 'zext.bc'
source_filename = "./zext.ll"

define i64 @zext(i32 %x) {
  %ret = zext i32 %x to i64
  ret i64 %ret
}

