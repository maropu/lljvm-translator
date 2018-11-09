; ModuleID = 'getelementptr1.bc'
source_filename = "./getelementptr1.ll"

; TODO: Revisit this pointer behaviour
define i32 @getelementptr1({ i32, { i32, i32 }* }* %x) {
  %1 = getelementptr inbounds { i32, { i32, i32 }* }, { i32, { i32, i32 }* }* %x, i64 1, i32 1
  %2 = load { i32, i32 }*, { i32, i32 }** %1, align 4
  %3 = load { i32, i32 }, { i32, i32 }* %2, align 4
  %ret = extractvalue { i32, i32 } %3, 1
  ret i32 %ret
}
