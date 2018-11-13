; ModuleID = 'atomicrmw2.bc'
source_filename = "./atomicrmw2.ll"

define i32 @and(i32* %x) {
  %old = atomicrmw and i32* %x, i32 1 acquire
  ret i32 %old
}
