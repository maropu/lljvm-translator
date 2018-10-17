; ModuleID = 'fcmp.bc'
source_filename = "./fcmp.ll"

define i1 @fcmp(float %x, float %y) {
  %ret = fcmp oge float %x, %y
  ret i1 %ret
}

