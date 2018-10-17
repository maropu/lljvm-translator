; ModuleID = 'insertelement.bc'
source_filename = "./insertelement.ll"

define <4 x i32> @insertelement(<4 x i32> %x) {
  %ret = insertelement <4 x i32> %x, i32 4, i32 3
  ret <4 x i32> %ret
}

