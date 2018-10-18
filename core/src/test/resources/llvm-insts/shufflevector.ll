; ModuleID = 'shufflevector.bc'
source_filename = "./shufflevector.ll"

define <4 x i32> @shufflevector(<4 x i32> %x) {
  %ret = shufflevector <4 x i32> %x, <4 x i32> undef, <4 x i32> <i32 1, i32 3, i32 2, i32 0>
  ret <4 x i32> %ret
}

