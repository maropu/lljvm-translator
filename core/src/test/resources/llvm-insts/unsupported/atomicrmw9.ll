; ModuleID = 'atomicrmw9.bc'
source_filename = "./atomicrmw9.ll"

define i32 @umin(i32* %x) {
  %old = atomicrmw umin i32* %x, i32 1 acquire
  ret i32 %old
}