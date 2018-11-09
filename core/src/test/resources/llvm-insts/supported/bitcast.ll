; ModuleID = 'bitcast.bc'
source_filename = "bitcast.cc"

define i64* @bitcast(double* %x) {
  %ret = bitcast double* %x to i64*
  ret i64* %ret
}

