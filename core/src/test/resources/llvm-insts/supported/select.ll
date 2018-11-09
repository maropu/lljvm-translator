; ModuleID = 'select.bc'
source_filename = "select.cc"

define double @select(i1 %x) {
  %ret = select i1 %x, double 1.000000e+00, double 9.000000e+00
  ret double %ret
}

