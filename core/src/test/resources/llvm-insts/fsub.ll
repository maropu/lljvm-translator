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
  %ret = fsub <4 x float> <float -0.000000e+00, float undef, float undef, float undef>, %x
  ret <4 x float> %ret
}
