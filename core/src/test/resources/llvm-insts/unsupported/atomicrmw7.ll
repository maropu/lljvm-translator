; ModuleID = 'atomicrmw7.bc'
source_filename = "./atomicrmw7.ll"

define i32 @min(i32* %x) {
  %old = atomicrmw min i32* %x, i32 1 acquire
  ret i32 %old
}