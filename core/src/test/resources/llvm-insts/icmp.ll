; ModuleID = 'icmp.bc'
source_filename = "./icmp.ll"

define i1 @icmp(i32 %x, i32 %y) {
  %ret = icmp slt i32 %x, %y
  ret i1 %ret
}

