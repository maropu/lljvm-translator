; ModuleID = 'unreachable.bc'
source_filename = "./unreachable.ll"

define i32 @unreachable() {
  ret i32 3
  unreachable
}

