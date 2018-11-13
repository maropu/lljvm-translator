; ModuleID = 'atomicrmw4.bc'
source_filename = "./atomicrmw4.ll"

define i32 @or(i32* %x) {
  %old = atomicrmw or i32* %x, i32 1 acquire
  ret i32 %old
}