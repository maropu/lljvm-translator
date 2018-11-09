; ModuleID = 'fptosi.bc'
source_filename = "./fptosi.ll"

define i32 @fptosi(float %x) {
  %ret = fptosi float %x to i32
  ret i32 %ret
}

