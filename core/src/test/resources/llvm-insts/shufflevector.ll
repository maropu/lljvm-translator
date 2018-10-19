; ModuleID = 'shufflevector.bc'
source_filename = "./shufflevector.ll"

define <4 x i32> @shufflevector1(<4 x i32> %x) {
  %ret = shufflevector <4 x i32> %x, <4 x i32> undef, <4 x i32> <i32 1, i32 3, i32 2, i32 0>
  ret <4 x i32> %ret
}

define <8 x float> @shufflevector2(<8 x float> %x) {
  %ret = shufflevector <8 x float> %x, <8 x float> undef, <8 x i32> zeroinitializer
  ret <8 x float> %ret
}

define <4 x i32> @shufflevector3(<4 x i32> %x, <4 x i32> %y) {
  %ret = shufflevector <4 x i32> %x, <4 x i32> %y, <4 x i32> <i32 0, i32 4, i32 1, i32 5>
  ret <4 x i32> %ret
}

define <8 x i32> @shufflevector4(<4 x i32> %x, <4 x i32> %y) {
  %ret = shufflevector <4 x i32> %x, <4 x i32> %y, <8 x i32> <i32 3, i32 3, i32 1, i32 1, i32 5, i32 5, i32 7, i32 7>
  ret <8 x i32> %ret
}
