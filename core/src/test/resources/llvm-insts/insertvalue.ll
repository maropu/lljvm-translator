; ModuleID = 'insertvalue.bc'
source_filename = "./insertvalue.ll"

define { double, double } @insertvalue({ double, double } %x) {
  %ret = insertvalue {double, double} %x, double 7.000000e+00, 1
  ret { double, double} %ret
}

