; ModuleID = 'alloca.bc'
source_filename = "./alloca.ll"

define i32 @alloca(i32 %x) {
  %1 = alloca i32, align 4
  store i32 %x, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  %ret = add nsw i32 %2, 4
  ret i32 %ret
}

