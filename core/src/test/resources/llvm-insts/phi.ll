; ModuleID = 'phi.bc'
source_filename = "./phi.ll"

define i32 @phi(i32 %x) {
entry:
  %0 = icmp eq i32 %x, 0
  br i1 %0, label %phi_label, label %label1

label1:
  br label %phi_label

phi_label:
  %ret = phi i32 [-3, %entry], [3, %label1]
  ret i32 %ret
}

