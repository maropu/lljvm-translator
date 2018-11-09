; ModuleID = 'fptoui.bc'
source_filename = "./fptoui.ll"

define i32 @fptoui(float %x) {
  %ret = fptoui float %x to i32
  ret i32 %ret
}

