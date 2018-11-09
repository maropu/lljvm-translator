; ModuleID = 'fpext.bc'
source_filename = "./fpext.ll"

define double @fpext(float %x) {
  %ret = fpext float %x to double
  ret double %ret
}

