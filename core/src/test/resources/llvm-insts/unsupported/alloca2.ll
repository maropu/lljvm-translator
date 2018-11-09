; ModuleID = 'alloca2.bc'
source_filename = "./alloca2.ll"

define { i32*, i64*, [3 x i32]* }* @alloca2() {
  %ret = alloca { i32*, i64*, [3 x i32]* }, align 4
  ret { i32*, i64*, [3 x i32]* }* %ret
}
