; ModuleID = 'sitofp.bc'
source_filename = "./sitofp.ll"

define float @sitofp(i32 %x) {
  %ret = sitofp i32 %x to float
  ret float %ret
}

