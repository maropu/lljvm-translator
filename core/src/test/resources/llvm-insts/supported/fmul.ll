; ModuleID = 'fmul.bc'
source_filename = "./fmul.ll"

define float @fmul(float %x, float %y) {
  %1 = fmul float %x, %y
  ret float %1
}

