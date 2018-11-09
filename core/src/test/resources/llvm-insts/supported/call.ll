; ModuleID = 'call.bc'
source_filename = "./call.ll"

define double @call(double %x) {
  %1 = call double @llvm.pow.f64(double %x, double 2.000000e+00)
  ret double %1
}

declare double @llvm.pow.f64(double, double)

