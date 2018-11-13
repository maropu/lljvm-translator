; ModuleID = 'atomicrmw6.bc'
source_filename = "./atomicrmw6.ll"

define i32 @max(i32* %x) {
  %old = atomicrmw max i32* %x, i32 1 acquire
  ret i32 %old
}