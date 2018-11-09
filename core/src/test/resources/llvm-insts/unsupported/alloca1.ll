; ModuleID = 'alloca1.bc'
source_filename = "./alloca1.ll"

define { i32*, i64*, <4 x i32>* }* @alloca1() {
  %ret = alloca { i32*, i64*, <4 x i32>* }, align 4
  ret { i32*, i64*, <4 x i32>* }* %ret
}
