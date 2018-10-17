; ModuleID = 'getelementptr.bc'
source_filename = "getelementptr.cc"

define i32 @getelementptr(i32* %x) {
  %1 = getelementptr inbounds i32, i32* %x, i64 3
  %ret = load i32, i32* %1, align 4
  ret i32 %ret
}

