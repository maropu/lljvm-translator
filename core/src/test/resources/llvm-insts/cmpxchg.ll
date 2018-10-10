; ModuleID = 'cmpxchg.bc'
source_filename = "./cmpxchg.ll"

define { i32, i1 } @cmpxchg(i32* %ptr, i32 %cmp, i32 %squared) {
  %success = cmpxchg i32* %ptr, i32 %cmp, i32 %squared acq_rel monotonic
  ret { i32, i1 } %success
}
