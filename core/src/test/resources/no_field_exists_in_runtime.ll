; ModuleID = 'no_field_exists_in_runtime.bc'
source_filename = "./no_field_exists_in_runtime.bc"

@no_existent_field = external global i32

define i32* @no_field_exists_in_runtime() {
  ret i32* @no_existent_field
}

