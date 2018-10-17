; ModuleID = 'uitofp.bc'
source_filename = "./uitofp.ll"

define float @uitofp(i32 %x) {
  %ret = uitofp i32 %x to float
  ret float %ret
}

