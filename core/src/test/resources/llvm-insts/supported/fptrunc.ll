; ModuleID = 'fptrunc.bc'
source_filename = "./fptrunc.ll"

define float @fptrunc(double %x) {
  %ret = fptrunc double %x to float
  ret float %ret
}

