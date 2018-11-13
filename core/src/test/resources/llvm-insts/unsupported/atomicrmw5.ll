; ModuleID = 'atomicrmw5.bc'
source_filename = "./atomicrmw5.ll"

define i32 @xor(i32* %x) {
  %old = atomicrmw xor i32* %x, i32 1 acquire
  ret i32 %old
}