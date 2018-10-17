; ModuleID = 'fadd.bc'
source_filename = "./fadd.ll"

define double @fadd(double %x, double %y) {
  %1 = fadd double %x, %y
  ret double %1
}

