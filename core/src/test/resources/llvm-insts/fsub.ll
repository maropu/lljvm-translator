; ModuleID = 'fsub.bc'
source_filename = "./fsub.ll"

define float @fsub1(float %x, float %y) {
  %ret = fsub float %x, %y
  ret float %ret
}

define <4 x float> @fsub2(<4 x float> %x) {
  %ret = fsub <4 x float> <float -0.000000e+00, float -0.000000e+00, float -0.000000e+00, float -0.000000e+00>, %x
  ret <4 x float> %ret
}

define <4 x float> @fsub3(<4 x float> %x) {
  %ret = fsub <4 x float> zeroinitializer, %x
  ret <4 x float> %ret
}

define <4 x float> @fsub4(<4 x float> %x) {
  %ret = fsub <4 x float> <float -1.000000e+00, float -3.000000e+00, float -0.000000e+00, float -2.000000e+00>, %x
  ret <4 x float> %ret
}

define <4 x float> @fsub5(<4 x float> %x) {
  %ret = fsub <4 x float> <float -0.000000e+00, float undef, float undef, float undef>, %x
  ret <4 x float> %ret
}

define <4 x float> @fsub6(<4 x float> %x) {
  %ret = fsub <4 x float> %x, <float -0.000000e+00, float -0.000000e+00, float -0.000000e+00, float -0.000000e+00>
  ret <4 x float> %ret
}

define <4 x float> @fsub7(<4 x float> %x) {
  %ret = fsub <4 x float> %x, zeroinitializer
  ret <4 x float> %ret
}

define <4 x float> @fsub8(<4 x float> %x) {
  %ret = fsub <4 x float> %x, <float -1.000000e+00, float -3.000000e+00, float -0.000000e+00, float -2.000000e+00>
  ret <4 x float> %ret
}

; define <4 x float> @fsub9(<4 x float> %x) {
;   %ret = fsub <4 x float> %x, <float -0.000000e+00, float undef, float undef, float undef>
;   ret <4 x float> %ret
; }

