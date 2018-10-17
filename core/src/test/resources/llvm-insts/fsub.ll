; ModuleID = 'fsub.bc'
source_filename = "./fsub.ll"

define float @fsub(float %x, float %y) {
  %1 = fsub float %x, %y
  ret float %1
}

