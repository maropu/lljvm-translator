; ModuleID = 'frem.bc'
source_filename = "./frem.ll"

define double @frem(double %x, double %y) {
  %1 = frem double %x, %y
  ret double %1
}

