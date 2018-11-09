; ModuleID = 'br.bc'
source_filename = "./br.ll"

define i32 @br(i1 %x) {
  br i1 %x, label %1, label %2

; <label>:1:
  ret i32 1

; <label>:2:
  ret i32 0
}

