; ModuleID = 'inttoptr.bc'
source_filename = "./inttoptr.ll"

define i32* @inttoptr(i64 %x) {
  %ret = inttoptr i64 %x to i32*
  ret i32* %ret
}

