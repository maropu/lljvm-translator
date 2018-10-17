; ModuleID = 'fdiv.bc'
source_filename = "./fdiv.ll"

define double @fdiv(double %x, double %y) {
  %1 = fdiv double %x, %y
  ret double %1
}

