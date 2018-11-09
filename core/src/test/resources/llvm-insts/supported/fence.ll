; ModuleID = 'fence.bc'
source_filename = "./fence.ll"

define i32 @fence() {
  fence acquire
  ret i32 5
}

