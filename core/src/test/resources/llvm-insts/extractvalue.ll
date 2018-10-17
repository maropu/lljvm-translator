; ModuleID = 'extractvalue.bc'
source_filename = "./extractvalue.ll"

define i32 @extractvalue({ i32, i32 } %x) {
  %ret = extractvalue { i32, i32 } %x, 1
  ret i32 %ret
}

