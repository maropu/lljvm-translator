; ModuleID = 'extractelement.bc'
source_filename = "./extractelement.ll"

define i32 @extractelement(<4 x i32> %x) {
  %ret = extractelement <4 x i32> %x, i32 2
  ret i32 %ret
}

