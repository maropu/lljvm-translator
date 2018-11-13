; ModuleID = 'atomicrmw1.bc'
source_filename = "./atomicrmw1.ll"

define i32 @xchg(i32* %x) {
  %old = atomicrmw xchg i32* %x, i32 1 acquire
  ret i32 %old
}
