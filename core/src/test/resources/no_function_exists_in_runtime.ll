; ModuleID = 'no_function_exists_in_runtime.bc'
source_filename = "./no_function_exists_in_runtime.bc"

define i32 @no_function_exists_in_runtime() {
  call void @no_existent_function()
  ret i32 0
}

declare void @no_existent_function() local_unnamed_addr

