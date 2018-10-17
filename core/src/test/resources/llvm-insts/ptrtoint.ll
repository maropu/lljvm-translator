; ModuleID = 'ptrtoint.bc'
source_filename = "ptrtoint.cc"

define i64 @ptrtoint(i32* %x) {
  %ret = ptrtoint i32* %x to i64
  ret i64 %ret
}

