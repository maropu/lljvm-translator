; ModuleID = 'atomicrmw.bc'
source_filename = "./atomicrmw.ll"

define i32 @add(i32* %x) {
  %old = atomicrmw add i32* %x, i32 1 acquire
  ret i32 %old
}

define i32 @sub(i32* %x) {
  %old = atomicrmw sub i32* %x, i32 1 acquire
  ret i32 %old
}
