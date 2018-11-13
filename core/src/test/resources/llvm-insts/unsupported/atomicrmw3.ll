; ModuleID = 'atomicrmw3.bc'
source_filename = "./atomicrmw3.ll"

define i32 @nand(i32* %x) {
  %old = atomicrmw nand i32* %x, i32 1 acquire
  ret i32 %old
}