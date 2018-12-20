; ModuleID = 'icmp.bc'
source_filename = "./icmp.ll"

define i1 @icmp1(i32 %x, i32 %y) {
  %ret = icmp slt i32 %x, %y
  ret i1 %ret
}

define <4 x i1> @icmp2(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> %x, <i32 0, i32 0, i32 0, i32 0> ; right = ConstantAggregateZero
  ret <4 x i1> %ret
}

define <4 x i1> @icmp3(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> %x, zeroinitializer ; right = ConstantAggregateZero
  ret <4 x i1> %ret
}

define <4 x i1> @icmp4(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> %x, <i32 1, i32 -3, i32 5, i32 -7> ; right = ConstantDataVector
  ret <4 x i1> %ret
}

define <4 x i1> @icmp5(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> %x, <i32 4, i32 undef, i32 undef, i32 undef> ; right = ConstantVector
  ret <4 x i1> %ret
}

define <4 x i1> @icmp6(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> <i32 0, i32 0, i32 0, i32 0>, %x ; left = ConstantAggregateZero
  ret <4 x i1> %ret
}

define <4 x i1> @icmp7(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> zeroinitializer, %x ; left = ConstantAggregateZero
  ret <4 x i1> %ret
}

define <4 x i1> @icmp8(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> <i32 1, i32 -3, i32 5, i32 -7>, %x ; left = ConstantDataVector
  ret <4 x i1> %ret
}

define <4 x i1> @icmp9(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> <i32 4, i32 undef, i32 undef, i32 undef>, %x ; left = ConstantVector
  ret <4 x i1> %ret
}

define <4 x i1> @icmp10(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> undef, %x
  ret <4 x i1> %ret
}

define <4 x i1> @icmp11(<4 x i32> %x) {
  %ret = icmp slt <4 x i32> %x, undef
  ret <4 x i1> %ret
}

define <4 x i1> @icmp12(<4 x i32> %notused) {
  %ret = icmp slt <4 x i32> undef, undef
  ret <4 x i1> %ret
}
