; ModuleID = 'atomicrmw8.bc'
source_filename = "./atomicrmw8.ll"

define i32 @umax(i32* %x) {
  %old = atomicrmw umax i32* %x, i32 1 acquire
  ret i32 %old
}