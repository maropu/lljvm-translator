; ModuleID = 'switch.bc'
source_filename = "./switch.ll"

define i32 @switch(i32 %x) {
  switch i32 %x, label %3 [
    i32 0, label %1
    i32 1, label %2
  ]

; <label>:1:
  ret i32 1

; <label>:2:
  ret i32 2

; <label>:3:
  ret i32 3
}
