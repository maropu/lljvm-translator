; ModuleID = 'calc_pi-numba-cfunc-float32.bc'
source_filename = "<string>"
target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-apple-darwin15.3.0"

@.const.picklebuf.4509254792 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([69 x i8], [69 x i8]* @.const.pickledata.4509254792, i32 0, i32 0), i32 69 }
@.const.picklebuf.4509323848 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([197 x i8], [197 x i8]* @.const.pickledata.4509323848, i32 0, i32 0), i32 197 }
@.const.picklebuf.26009893776 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([69 x i8], [69 x i8]* @.const.pickledata.26009893776, i32 0, i32 0), i32 69 }
@.const.pickledata.26009893776 = internal constant [69 x i8] c"\80\02cexceptions\0AZeroDivisionError\0Aq\00U\18integer division by zeroq\01\85q\02\86q\03."
@.const.pickledata.4509323848 = internal constant [197 x i8] c"\80\02cexceptions\0AValueError\0Aq\00U\9Funable to broadcast argument 1 to output array\0AFile \22/Users/maropu/IdeaProjects/lljvm-translator/core/src/test/resources/pyfunc/numba_examples/pi.py\22, line 5, q\01\85q\02\86q\03."
@.const.pickledata.4509254792 = internal constant [69 x i8] c"\80\02cexceptions\0AValueError\0Aq\00U\1Fnegative dimensions not allowedq\01\85q\02\86q\03."
@".const.<Numba C callback 'calc_pi'>" = internal constant [29 x i8] c"<Numba C callback 'calc_pi'>\00"
@PyExc_StopIteration = external global i8
@PyExc_SystemError = external global i8
@".const.unknown error when calling native function" = internal constant [43 x i8] c"unknown error when calling native function\00"

define i32 @"_ZN14numba_examples2pi12calc_pi$2467Ei"(float* noalias nocapture %retptr, { i8*, i32 }** noalias nocapture %excinfo, i8* noalias nocapture readnone %env, i32 %arg.n) local_unnamed_addr {
entry:
  %.28 = sext i32 %arg.n to i64
  %.29 = icmp slt i32 %arg.n, 0
  br i1 %.29, label %B0.if, label %B0.endif, !prof !0

B0.if:                                            ; preds = %entry
  store { i8*, i32 }* @.const.picklebuf.4509254792, { i8*, i32 }** %excinfo, align 8
  ret i32 1

B0.endif:                                         ; preds = %entry
  %.36 = shl nsw i64 %.28, 3
  %.37 = tail call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.36, i32 32)
  %.5.i = getelementptr i8, i8* %.37, i64 24
  %0 = bitcast i8* %.5.i to double**
  %.6.i116 = load double*, double** %0, align 8
  %.61151 = icmp eq i32 %arg.n, 0
  br i1 %.61151, label %B127.i, label %for.body.lr.ph

for.body.lr.ph:                                   ; preds = %B0.endif
  %.63 = tail call { i32, [624 x i32], i32, double, i32 }* @numba_get_np_random_state()
  br label %for.body

for.body:                                         ; preds = %for.body.endif.endif, %for.body.lr.ph
  %loop.index152 = phi i64 [ 0, %for.body.lr.ph ], [ %.121, %for.body.endif.endif ]
  %1 = bitcast { i32, [624 x i32], i32, double, i32 }* %.63 to i32*
  %.65 = load i32, i32* %1, align 4
  %.66 = icmp ugt i32 %.65, 623
  br i1 %.66, label %for.body.if, label %for.body.endif, !prof !0

B115.i:                                           ; preds = %for.body.endif.endif
  %.204.i = icmp eq i32 %arg.n, 1
  br i1 %.204.i, label %for.end.endif.endif, label %B127.i

B127.i:                                           ; preds = %B115.i, %B0.endif
  br label %for.end.endif.endif

for.body.if:                                      ; preds = %for.body
  %2 = bitcast { i32, [624 x i32], i32, double, i32 }* %.63 to i32*
  tail call void @numba_rnd_shuffle({ i32, [624 x i32], i32, double, i32 }* nonnull %.63)
  store i32 0, i32* %2, align 4
  br label %for.body.endif

for.body.endif:                                   ; preds = %for.body.if, %for.body
  %.71 = phi i32 [ 0, %for.body.if ], [ %.65, %for.body ]
  %3 = bitcast { i32, [624 x i32], i32, double, i32 }* %.63 to i32*
  %4 = sext i32 %.71 to i64
  %.73 = getelementptr inbounds { i32, [624 x i32], i32, double, i32 }, { i32, [624 x i32], i32, double, i32 }* %.63, i64 0, i32 1, i64 %4
  %.74 = load i32, i32* %.73, align 4
  %.75 = add i32 %.71, 1
  store i32 %.75, i32* %3, align 4
  %.77 = lshr i32 %.74, 11
  %.78 = xor i32 %.77, %.74
  %.79 = shl i32 %.78, 7
  %.80 = and i32 %.79, -1658038656
  %.81 = xor i32 %.80, %.78
  %.82 = shl i32 %.81, 15
  %.83 = and i32 %.82, -272236544
  %.84 = xor i32 %.83, %.81
  %.85 = lshr i32 %.84, 18
  %.86 = xor i32 %.85, %.84
  %.87 = lshr i32 %.86, 5
  %.90 = icmp ugt i32 %.75, 623
  br i1 %.90, label %for.body.endif.if, label %for.body.endif.endif, !prof !0

for.body.endif.if:                                ; preds = %for.body.endif
  %5 = bitcast { i32, [624 x i32], i32, double, i32 }* %.63 to i32*
  tail call void @numba_rnd_shuffle({ i32, [624 x i32], i32, double, i32 }* nonnull %.63)
  store i32 0, i32* %5, align 4
  br label %for.body.endif.endif

for.body.endif.endif:                             ; preds = %for.body.endif.if, %for.body.endif
  %.95 = phi i32 [ 0, %for.body.endif.if ], [ %.75, %for.body.endif ]
  %6 = bitcast { i32, [624 x i32], i32, double, i32 }* %.63 to i32*
  %7 = sext i32 %.95 to i64
  %.97 = getelementptr inbounds { i32, [624 x i32], i32, double, i32 }, { i32, [624 x i32], i32, double, i32 }* %.63, i64 0, i32 1, i64 %7
  %.98 = load i32, i32* %.97, align 4
  %.99 = add i32 %.95, 1
  store i32 %.99, i32* %6, align 4
  %.101 = lshr i32 %.98, 11
  %.102 = xor i32 %.101, %.98
  %.103 = shl i32 %.102, 7
  %.104 = and i32 %.103, -1658038656
  %.105 = xor i32 %.104, %.102
  %.106 = shl i32 %.105, 15
  %.107 = and i32 %.106, -272236544
  %.108 = xor i32 %.107, %.105
  %.109 = lshr i32 %.108, 18
  %.110 = xor i32 %.109, %.108
  %.111 = lshr i32 %.110, 6
  %.112 = uitofp i32 %.87 to double
  %.113 = uitofp i32 %.111 to double
  %.114 = fmul double %.112, 0x4190000000000000
  %.115 = fadd double %.114, %.113
  %.116 = fmul double %.115, 0x3CA0000000000000
  %scevgep312 = getelementptr double, double* %.6.i116, i64 %loop.index152
  store double %.116, double* %scevgep312, align 8
  %.121 = add nuw nsw i64 %loop.index152, 1
  %.61 = icmp slt i64 %.121, %.28
  br i1 %.61, label %for.body, label %B115.i

for.end.endif.endif:                              ; preds = %B127.i, %B115.i
  %.173173 = phi i64 [ %.28, %B127.i ], [ 1, %B115.i ]
  %.177 = shl nsw i64 %.173173, 3
  %.178 = call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.177, i32 32)
  %.5.i25 = getelementptr i8, i8* %.178, i64 24
  %8 = bitcast i8* %.5.i25 to double**
  %.6.i26117 = load double*, double** %8, align 8
  %.213148 = icmp sgt i64 %.173173, 0
  br i1 %.213148, label %for.body.1.endif.endif.lr.ph, label %for.end.1

for.body.1.endif.endif.lr.ph:                     ; preds = %for.end.endif.endif
  %.215 = icmp ugt i32 %arg.n, 1
  br i1 %.215, label %for.body.1.endif.endif.us.preheader, label %for.body.1.endif.endif.preheader

for.body.1.endif.endif.preheader:                 ; preds = %for.body.1.endif.endif.lr.ph
  %9 = icmp ult i64 %.173173, 16
  br i1 %9, label %for.body.1.endif.endif.preheader492, label %min.iters.checked

for.body.1.endif.endif.preheader492:              ; preds = %middle.block, %vector.memcheck, %min.iters.checked, %for.body.1.endif.endif.preheader
  %loop.index.1150.ph = phi i64 [ 0, %vector.memcheck ], [ 0, %min.iters.checked ], [ 0, %for.body.1.endif.endif.preheader ], [ %n.vec, %middle.block ]
  %10 = sub nsw i64 %.173173, %loop.index.1150.ph
  %11 = add nsw i64 %.173173, -1
  %12 = sub nsw i64 %11, %loop.index.1150.ph
  %xtraiter524 = and i64 %10, 7
  %lcmp.mod525 = icmp eq i64 %xtraiter524, 0
  br i1 %lcmp.mod525, label %for.body.1.endif.endif.prol.loopexit, label %for.body.1.endif.endif.prol.preheader

for.body.1.endif.endif.prol.preheader:            ; preds = %for.body.1.endif.endif.preheader492
  %13 = sub i64 0, %xtraiter524
  br label %for.body.1.endif.endif.prol

for.body.1.endif.endif.prol:                      ; preds = %for.body.1.endif.endif.prol, %for.body.1.endif.endif.prol.preheader
  %lsr.iv296 = phi i64 [ %lsr.iv.next297, %for.body.1.endif.endif.prol ], [ %13, %for.body.1.endif.endif.prol.preheader ]
  %loop.index.1150.prol = phi i64 [ %.244.prol, %for.body.1.endif.endif.prol ], [ %loop.index.1150.ph, %for.body.1.endif.endif.prol.preheader ]
  %.223.prol = load double, double* %.6.i116, align 8
  %.15.i.prol = fmul double %.223.prol, 2.000000e+00
  %.29.i.prol = fadd double %.15.i.prol, -1.000000e+00
  %scevgep295 = getelementptr double, double* %.6.i26117, i64 %loop.index.1150.prol
  store double %.29.i.prol, double* %scevgep295, align 8
  %.244.prol = add nuw nsw i64 %loop.index.1150.prol, 1
  %lsr.iv.next297 = add nsw i64 %lsr.iv296, 1
  %prol.iter526.cmp = icmp eq i64 %lsr.iv.next297, 0
  br i1 %prol.iter526.cmp, label %for.body.1.endif.endif.prol.loopexit, label %for.body.1.endif.endif.prol, !llvm.loop !1

for.body.1.endif.endif.prol.loopexit:             ; preds = %for.body.1.endif.endif.prol, %for.body.1.endif.endif.preheader492
  %loop.index.1150.unr = phi i64 [ %loop.index.1150.ph, %for.body.1.endif.endif.preheader492 ], [ %.244.prol, %for.body.1.endif.endif.prol ]
  %14 = icmp ult i64 %12, 7
  br i1 %14, label %for.end.1, label %for.body.1.endif.endif.preheader492.new

for.body.1.endif.endif.preheader492.new:          ; preds = %for.body.1.endif.endif.prol.loopexit
  %15 = sub i64 %.173173, %loop.index.1150.unr
  %16 = add i64 %loop.index.1150.unr, 7
  %scevgep285 = getelementptr double, double* %.6.i26117, i64 %16
  br label %for.body.1.endif.endif

min.iters.checked:                                ; preds = %for.body.1.endif.endif.preheader
  %n.vec = and i64 %.173173, -16
  %cmp.zero = icmp eq i64 %n.vec, 0
  br i1 %cmp.zero, label %for.body.1.endif.endif.preheader492, label %vector.memcheck

vector.memcheck:                                  ; preds = %min.iters.checked
  %scevgep = getelementptr double, double* %.6.i26117, i64 %.173173
  %bound0 = icmp ult double* %.6.i26117, %.6.i116
  %bound1 = icmp ult double* %.6.i116, %scevgep
  %memcheck.conflict = and i1 %bound0, %bound1
  br i1 %memcheck.conflict, label %for.body.1.endif.endif.preheader492, label %vector.body.preheader

vector.body.preheader:                            ; preds = %vector.memcheck
  %17 = add nsw i64 %n.vec, -16
  %18 = lshr exact i64 %17, 4
  %19 = and i64 %18, 1
  %lcmp.mod528 = icmp eq i64 %19, 0
  br i1 %lcmp.mod528, label %vector.body.prol, label %vector.body.prol.loopexit

vector.body.prol:                                 ; preds = %vector.body.preheader
  %20 = load double, double* %.6.i116, align 8, !alias.scope !3
  %21 = insertelement <4 x double> undef, double %20, i32 0
  %22 = shufflevector <4 x double> %21, <4 x double> undef, <4 x i32> zeroinitializer
  %23 = insertelement <4 x double> undef, double %20, i32 0
  %24 = shufflevector <4 x double> %23, <4 x double> undef, <4 x i32> zeroinitializer
  %25 = insertelement <4 x double> undef, double %20, i32 0
  %26 = shufflevector <4 x double> %25, <4 x double> undef, <4 x i32> zeroinitializer
  %27 = insertelement <4 x double> undef, double %20, i32 0
  %28 = shufflevector <4 x double> %27, <4 x double> undef, <4 x i32> zeroinitializer
  %29 = fmul <4 x double> %22, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %30 = fmul <4 x double> %24, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %31 = fmul <4 x double> %26, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %32 = fmul <4 x double> %28, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %33 = fadd <4 x double> %29, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %34 = fadd <4 x double> %30, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %35 = fadd <4 x double> %31, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %36 = fadd <4 x double> %32, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %37 = bitcast double* %.6.i26117 to <4 x double>*
  store <4 x double> %33, <4 x double>* %37, align 8, !alias.scope !6, !noalias !3
  %38 = getelementptr double, double* %.6.i26117, i64 4
  %39 = bitcast double* %38 to <4 x double>*
  store <4 x double> %34, <4 x double>* %39, align 8, !alias.scope !6, !noalias !3
  %40 = getelementptr double, double* %.6.i26117, i64 8
  %41 = bitcast double* %40 to <4 x double>*
  store <4 x double> %35, <4 x double>* %41, align 8, !alias.scope !6, !noalias !3
  %42 = getelementptr double, double* %.6.i26117, i64 12
  %43 = bitcast double* %42 to <4 x double>*
  store <4 x double> %36, <4 x double>* %43, align 8, !alias.scope !6, !noalias !3
  br label %vector.body.prol.loopexit

vector.body.prol.loopexit:                        ; preds = %vector.body.prol, %vector.body.preheader
  %index.unr = phi i64 [ 0, %vector.body.preheader ], [ 16, %vector.body.prol ]
  %44 = icmp eq i64 %18, 0
  br i1 %44, label %middle.block, label %vector.body.preheader.new

vector.body.preheader.new:                        ; preds = %vector.body.prol.loopexit
  %45 = load double, double* %.6.i116, align 8, !alias.scope !3
  %46 = insertelement <4 x double> undef, double %45, i32 0
  %47 = shufflevector <4 x double> %46, <4 x double> undef, <4 x i32> zeroinitializer
  %48 = insertelement <4 x double> undef, double %45, i32 0
  %49 = shufflevector <4 x double> %48, <4 x double> undef, <4 x i32> zeroinitializer
  %50 = insertelement <4 x double> undef, double %45, i32 0
  %51 = shufflevector <4 x double> %50, <4 x double> undef, <4 x i32> zeroinitializer
  %52 = insertelement <4 x double> undef, double %45, i32 0
  %53 = shufflevector <4 x double> %52, <4 x double> undef, <4 x i32> zeroinitializer
  %54 = fmul <4 x double> %47, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %55 = fmul <4 x double> %49, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %56 = fmul <4 x double> %51, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %57 = fmul <4 x double> %53, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %58 = fadd <4 x double> %54, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %59 = fadd <4 x double> %55, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %60 = fadd <4 x double> %56, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %61 = fadd <4 x double> %57, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %62 = load double, double* %.6.i116, align 8, !alias.scope !3
  %63 = insertelement <4 x double> undef, double %62, i32 0
  %64 = shufflevector <4 x double> %63, <4 x double> undef, <4 x i32> zeroinitializer
  %65 = insertelement <4 x double> undef, double %62, i32 0
  %66 = shufflevector <4 x double> %65, <4 x double> undef, <4 x i32> zeroinitializer
  %67 = insertelement <4 x double> undef, double %62, i32 0
  %68 = shufflevector <4 x double> %67, <4 x double> undef, <4 x i32> zeroinitializer
  %69 = insertelement <4 x double> undef, double %62, i32 0
  %70 = shufflevector <4 x double> %69, <4 x double> undef, <4 x i32> zeroinitializer
  %71 = fmul <4 x double> %64, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %72 = fmul <4 x double> %66, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %73 = fmul <4 x double> %68, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %74 = fmul <4 x double> %70, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %75 = fadd <4 x double> %71, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %76 = fadd <4 x double> %72, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %77 = fadd <4 x double> %73, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %78 = fadd <4 x double> %74, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %79 = sub i64 %n.vec, %index.unr
  %80 = add i64 %index.unr, 28
  %scevgep300 = getelementptr double, double* %.6.i26117, i64 %80
  br label %vector.body

vector.body:                                      ; preds = %vector.body, %vector.body.preheader.new
  %lsr.iv301 = phi double* [ %scevgep302, %vector.body ], [ %scevgep300, %vector.body.preheader.new ]
  %lsr.iv298 = phi i64 [ %lsr.iv.next299, %vector.body ], [ %79, %vector.body.preheader.new ]
  %lsr.iv301303 = bitcast double* %lsr.iv301 to <4 x double>*
  %scevgep311 = getelementptr <4 x double>, <4 x double>* %lsr.iv301303, i64 -7
  store <4 x double> %58, <4 x double>* %scevgep311, align 8, !alias.scope !6, !noalias !3
  %scevgep310 = getelementptr <4 x double>, <4 x double>* %lsr.iv301303, i64 -6
  store <4 x double> %59, <4 x double>* %scevgep310, align 8, !alias.scope !6, !noalias !3
  %scevgep309 = getelementptr <4 x double>, <4 x double>* %lsr.iv301303, i64 -5
  store <4 x double> %60, <4 x double>* %scevgep309, align 8, !alias.scope !6, !noalias !3
  %scevgep308 = getelementptr <4 x double>, <4 x double>* %lsr.iv301303, i64 -4
  store <4 x double> %61, <4 x double>* %scevgep308, align 8, !alias.scope !6, !noalias !3
  %scevgep306 = getelementptr <4 x double>, <4 x double>* %lsr.iv301303, i64 -3
  store <4 x double> %75, <4 x double>* %scevgep306, align 8, !alias.scope !6, !noalias !3
  %scevgep305 = getelementptr <4 x double>, <4 x double>* %lsr.iv301303, i64 -2
  store <4 x double> %76, <4 x double>* %scevgep305, align 8, !alias.scope !6, !noalias !3
  %scevgep304 = getelementptr <4 x double>, <4 x double>* %lsr.iv301303, i64 -1
  store <4 x double> %77, <4 x double>* %scevgep304, align 8, !alias.scope !6, !noalias !3
  store <4 x double> %78, <4 x double>* %lsr.iv301303, align 8, !alias.scope !6, !noalias !3
  %lsr.iv.next299 = add i64 %lsr.iv298, -32
  %scevgep302 = getelementptr double, double* %lsr.iv301, i64 32
  %81 = icmp eq i64 %lsr.iv.next299, 0
  br i1 %81, label %middle.block, label %vector.body, !llvm.loop !8

middle.block:                                     ; preds = %vector.body, %vector.body.prol.loopexit
  %cmp.n = icmp eq i64 %.173173, %n.vec
  br i1 %cmp.n, label %for.end.1, label %for.body.1.endif.endif.preheader492

for.body.1.endif.endif.us.preheader:              ; preds = %for.body.1.endif.endif.lr.ph
  %82 = icmp ult i64 %.173173, 16
  br i1 %82, label %for.body.1.endif.endif.us.preheader491, label %min.iters.checked207

min.iters.checked207:                             ; preds = %for.body.1.endif.endif.us.preheader
  %n.vec209 = and i64 %.173173, -16
  %cmp.zero210 = icmp eq i64 %n.vec209, 0
  br i1 %cmp.zero210, label %for.body.1.endif.endif.us.preheader491, label %vector.memcheck220

vector.memcheck220:                               ; preds = %min.iters.checked207
  %scevgep212 = getelementptr double, double* %.6.i26117, i64 %.173173
  %scevgep214 = getelementptr double, double* %.6.i116, i64 %.173173
  %bound0216 = icmp ult double* %.6.i26117, %scevgep214
  %bound1217 = icmp ult double* %.6.i116, %scevgep212
  %memcheck.conflict219 = and i1 %bound0216, %bound1217
  br i1 %memcheck.conflict219, label %for.body.1.endif.endif.us.preheader491, label %vector.body203.preheader

vector.body203.preheader:                         ; preds = %vector.memcheck220
  %83 = add nsw i64 %n.vec209, -16
  %84 = lshr exact i64 %83, 4
  %85 = and i64 %84, 1
  %lcmp.mod523 = icmp eq i64 %85, 0
  br i1 %lcmp.mod523, label %vector.body203.prol, label %vector.body203.prol.loopexit

vector.body203.prol:                              ; preds = %vector.body203.preheader
  %86 = bitcast double* %.6.i116 to <4 x double>*
  %wide.load.prol = load <4 x double>, <4 x double>* %86, align 8, !alias.scope !11
  %87 = getelementptr double, double* %.6.i116, i64 4
  %88 = bitcast double* %87 to <4 x double>*
  %wide.load232.prol = load <4 x double>, <4 x double>* %88, align 8, !alias.scope !11
  %89 = getelementptr double, double* %.6.i116, i64 8
  %90 = bitcast double* %89 to <4 x double>*
  %wide.load233.prol = load <4 x double>, <4 x double>* %90, align 8, !alias.scope !11
  %91 = getelementptr double, double* %.6.i116, i64 12
  %92 = bitcast double* %91 to <4 x double>*
  %wide.load234.prol = load <4 x double>, <4 x double>* %92, align 8, !alias.scope !11
  %93 = fmul <4 x double> %wide.load.prol, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %94 = fmul <4 x double> %wide.load232.prol, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %95 = fmul <4 x double> %wide.load233.prol, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %96 = fmul <4 x double> %wide.load234.prol, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %97 = fadd <4 x double> %93, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %98 = fadd <4 x double> %94, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %99 = fadd <4 x double> %95, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %100 = fadd <4 x double> %96, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %101 = bitcast double* %.6.i26117 to <4 x double>*
  store <4 x double> %97, <4 x double>* %101, align 8, !alias.scope !14, !noalias !11
  %102 = getelementptr double, double* %.6.i26117, i64 4
  %103 = bitcast double* %102 to <4 x double>*
  store <4 x double> %98, <4 x double>* %103, align 8, !alias.scope !14, !noalias !11
  %104 = getelementptr double, double* %.6.i26117, i64 8
  %105 = bitcast double* %104 to <4 x double>*
  store <4 x double> %99, <4 x double>* %105, align 8, !alias.scope !14, !noalias !11
  %106 = getelementptr double, double* %.6.i26117, i64 12
  %107 = bitcast double* %106 to <4 x double>*
  store <4 x double> %100, <4 x double>* %107, align 8, !alias.scope !14, !noalias !11
  br label %vector.body203.prol.loopexit

vector.body203.prol.loopexit:                     ; preds = %vector.body203.prol, %vector.body203.preheader
  %index222.unr = phi i64 [ 0, %vector.body203.preheader ], [ 16, %vector.body203.prol ]
  %108 = icmp eq i64 %84, 0
  br i1 %108, label %middle.block204, label %vector.body203.preheader.new

vector.body203.preheader.new:                     ; preds = %vector.body203.prol.loopexit
  %109 = sub i64 %n.vec209, %index222.unr
  %110 = add i64 %index222.unr, 28
  %scevgep260 = getelementptr double, double* %.6.i26117, i64 %110
  %scevgep271 = getelementptr double, double* %.6.i116, i64 %110
  br label %vector.body203

vector.body203:                                   ; preds = %vector.body203, %vector.body203.preheader.new
  %lsr.iv272 = phi double* [ %scevgep273, %vector.body203 ], [ %scevgep271, %vector.body203.preheader.new ]
  %lsr.iv261 = phi double* [ %scevgep262, %vector.body203 ], [ %scevgep260, %vector.body203.preheader.new ]
  %lsr.iv258 = phi i64 [ %lsr.iv.next259, %vector.body203 ], [ %109, %vector.body203.preheader.new ]
  %lsr.iv272274 = bitcast double* %lsr.iv272 to <4 x double>*
  %lsr.iv261263 = bitcast double* %lsr.iv261 to <4 x double>*
  %scevgep282 = getelementptr <4 x double>, <4 x double>* %lsr.iv272274, i64 -7
  %wide.load = load <4 x double>, <4 x double>* %scevgep282, align 8, !alias.scope !11
  %scevgep281 = getelementptr <4 x double>, <4 x double>* %lsr.iv272274, i64 -6
  %wide.load232 = load <4 x double>, <4 x double>* %scevgep281, align 8, !alias.scope !11
  %scevgep280 = getelementptr <4 x double>, <4 x double>* %lsr.iv272274, i64 -5
  %wide.load233 = load <4 x double>, <4 x double>* %scevgep280, align 8, !alias.scope !11
  %scevgep279 = getelementptr <4 x double>, <4 x double>* %lsr.iv272274, i64 -4
  %wide.load234 = load <4 x double>, <4 x double>* %scevgep279, align 8, !alias.scope !11
  %111 = fmul <4 x double> %wide.load, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %112 = fmul <4 x double> %wide.load232, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %113 = fmul <4 x double> %wide.load233, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %114 = fmul <4 x double> %wide.load234, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %115 = fadd <4 x double> %111, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %116 = fadd <4 x double> %112, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %117 = fadd <4 x double> %113, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %118 = fadd <4 x double> %114, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %scevgep270 = getelementptr <4 x double>, <4 x double>* %lsr.iv261263, i64 -7
  store <4 x double> %115, <4 x double>* %scevgep270, align 8, !alias.scope !14, !noalias !11
  %scevgep269 = getelementptr <4 x double>, <4 x double>* %lsr.iv261263, i64 -6
  store <4 x double> %116, <4 x double>* %scevgep269, align 8, !alias.scope !14, !noalias !11
  %scevgep268 = getelementptr <4 x double>, <4 x double>* %lsr.iv261263, i64 -5
  store <4 x double> %117, <4 x double>* %scevgep268, align 8, !alias.scope !14, !noalias !11
  %scevgep267 = getelementptr <4 x double>, <4 x double>* %lsr.iv261263, i64 -4
  store <4 x double> %118, <4 x double>* %scevgep267, align 8, !alias.scope !14, !noalias !11
  %scevgep278 = getelementptr <4 x double>, <4 x double>* %lsr.iv272274, i64 -3
  %wide.load.1 = load <4 x double>, <4 x double>* %scevgep278, align 8, !alias.scope !11
  %scevgep277 = getelementptr <4 x double>, <4 x double>* %lsr.iv272274, i64 -2
  %wide.load232.1 = load <4 x double>, <4 x double>* %scevgep277, align 8, !alias.scope !11
  %scevgep275 = getelementptr <4 x double>, <4 x double>* %lsr.iv272274, i64 -1
  %wide.load233.1 = load <4 x double>, <4 x double>* %scevgep275, align 8, !alias.scope !11
  %wide.load234.1 = load <4 x double>, <4 x double>* %lsr.iv272274, align 8, !alias.scope !11
  %119 = fmul <4 x double> %wide.load.1, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %120 = fmul <4 x double> %wide.load232.1, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %121 = fmul <4 x double> %wide.load233.1, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %122 = fmul <4 x double> %wide.load234.1, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %123 = fadd <4 x double> %119, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %124 = fadd <4 x double> %120, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %125 = fadd <4 x double> %121, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %126 = fadd <4 x double> %122, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %scevgep266 = getelementptr <4 x double>, <4 x double>* %lsr.iv261263, i64 -3
  store <4 x double> %123, <4 x double>* %scevgep266, align 8, !alias.scope !14, !noalias !11
  %scevgep265 = getelementptr <4 x double>, <4 x double>* %lsr.iv261263, i64 -2
  store <4 x double> %124, <4 x double>* %scevgep265, align 8, !alias.scope !14, !noalias !11
  %scevgep264 = getelementptr <4 x double>, <4 x double>* %lsr.iv261263, i64 -1
  store <4 x double> %125, <4 x double>* %scevgep264, align 8, !alias.scope !14, !noalias !11
  store <4 x double> %126, <4 x double>* %lsr.iv261263, align 8, !alias.scope !14, !noalias !11
  %lsr.iv.next259 = add i64 %lsr.iv258, -32
  %scevgep262 = getelementptr double, double* %lsr.iv261, i64 32
  %scevgep273 = getelementptr double, double* %lsr.iv272, i64 32
  %127 = icmp eq i64 %lsr.iv.next259, 0
  br i1 %127, label %middle.block204, label %vector.body203, !llvm.loop !16

middle.block204:                                  ; preds = %vector.body203, %vector.body203.prol.loopexit
  %cmp.n225 = icmp eq i64 %.173173, %n.vec209
  br i1 %cmp.n225, label %for.end.1, label %for.body.1.endif.endif.us.preheader491

for.body.1.endif.endif.us.preheader491:           ; preds = %middle.block204, %vector.memcheck220, %min.iters.checked207, %for.body.1.endif.endif.us.preheader
  %loop.index.1150.us.ph = phi i64 [ 0, %vector.memcheck220 ], [ 0, %min.iters.checked207 ], [ 0, %for.body.1.endif.endif.us.preheader ], [ %n.vec209, %middle.block204 ]
  %128 = sub nsw i64 %.173173, %loop.index.1150.us.ph
  %129 = add nsw i64 %.173173, -1
  %130 = sub nsw i64 %129, %loop.index.1150.us.ph
  %xtraiter519 = and i64 %128, 7
  %lcmp.mod520 = icmp eq i64 %xtraiter519, 0
  br i1 %lcmp.mod520, label %for.body.1.endif.endif.us.prol.loopexit, label %for.body.1.endif.endif.us.prol.preheader

for.body.1.endif.endif.us.prol.preheader:         ; preds = %for.body.1.endif.endif.us.preheader491
  %131 = sub i64 0, %xtraiter519
  br label %for.body.1.endif.endif.us.prol

for.body.1.endif.endif.us.prol:                   ; preds = %for.body.1.endif.endif.us.prol, %for.body.1.endif.endif.us.prol.preheader
  %lsr.iv256 = phi i64 [ %lsr.iv.next257, %for.body.1.endif.endif.us.prol ], [ %131, %for.body.1.endif.endif.us.prol.preheader ]
  %loop.index.1150.us.prol = phi i64 [ %.244.us.prol, %for.body.1.endif.endif.us.prol ], [ %loop.index.1150.us.ph, %for.body.1.endif.endif.us.prol.preheader ]
  %scevgep255 = getelementptr double, double* %.6.i116, i64 %loop.index.1150.us.prol
  %.223.us.prol = load double, double* %scevgep255, align 8
  %.15.i.us.prol = fmul double %.223.us.prol, 2.000000e+00
  %.29.i.us.prol = fadd double %.15.i.us.prol, -1.000000e+00
  %scevgep254 = getelementptr double, double* %.6.i26117, i64 %loop.index.1150.us.prol
  store double %.29.i.us.prol, double* %scevgep254, align 8
  %.244.us.prol = add nuw nsw i64 %loop.index.1150.us.prol, 1
  %lsr.iv.next257 = add nsw i64 %lsr.iv256, 1
  %prol.iter521.cmp = icmp eq i64 %lsr.iv.next257, 0
  br i1 %prol.iter521.cmp, label %for.body.1.endif.endif.us.prol.loopexit, label %for.body.1.endif.endif.us.prol, !llvm.loop !17

for.body.1.endif.endif.us.prol.loopexit:          ; preds = %for.body.1.endif.endif.us.prol, %for.body.1.endif.endif.us.preheader491
  %loop.index.1150.us.unr = phi i64 [ %loop.index.1150.us.ph, %for.body.1.endif.endif.us.preheader491 ], [ %.244.us.prol, %for.body.1.endif.endif.us.prol ]
  %132 = icmp ult i64 %130, 7
  br i1 %132, label %for.end.1, label %for.body.1.endif.endif.us.preheader491.new

for.body.1.endif.endif.us.preheader491.new:       ; preds = %for.body.1.endif.endif.us.prol.loopexit
  %133 = sub i64 %.173173, %loop.index.1150.us.unr
  %134 = add i64 %loop.index.1150.us.unr, 7
  %scevgep233 = getelementptr double, double* %.6.i26117, i64 %134
  %scevgep243 = getelementptr double, double* %.6.i116, i64 %134
  br label %for.body.1.endif.endif.us

for.body.1.endif.endif.us:                        ; preds = %for.body.1.endif.endif.us, %for.body.1.endif.endif.us.preheader491.new
  %lsr.iv244 = phi double* [ %scevgep246, %for.body.1.endif.endif.us ], [ %scevgep243, %for.body.1.endif.endif.us.preheader491.new ]
  %lsr.iv234 = phi double* [ %scevgep235, %for.body.1.endif.endif.us ], [ %scevgep233, %for.body.1.endif.endif.us.preheader491.new ]
  %lsr.iv231 = phi i64 [ %lsr.iv.next232, %for.body.1.endif.endif.us ], [ %133, %for.body.1.endif.endif.us.preheader491.new ]
  %scevgep253 = getelementptr double, double* %lsr.iv244, i64 -7
  %.223.us = load double, double* %scevgep253, align 8
  %.15.i.us = fmul double %.223.us, 2.000000e+00
  %.29.i.us = fadd double %.15.i.us, -1.000000e+00
  %scevgep242 = getelementptr double, double* %lsr.iv234, i64 -7
  store double %.29.i.us, double* %scevgep242, align 8
  %scevgep252 = getelementptr double, double* %lsr.iv244, i64 -6
  %.223.us.1 = load double, double* %scevgep252, align 8
  %.15.i.us.1 = fmul double %.223.us.1, 2.000000e+00
  %.29.i.us.1 = fadd double %.15.i.us.1, -1.000000e+00
  %scevgep241 = getelementptr double, double* %lsr.iv234, i64 -6
  store double %.29.i.us.1, double* %scevgep241, align 8
  %scevgep251 = getelementptr double, double* %lsr.iv244, i64 -5
  %.223.us.2 = load double, double* %scevgep251, align 8
  %.15.i.us.2 = fmul double %.223.us.2, 2.000000e+00
  %.29.i.us.2 = fadd double %.15.i.us.2, -1.000000e+00
  %scevgep240 = getelementptr double, double* %lsr.iv234, i64 -5
  store double %.29.i.us.2, double* %scevgep240, align 8
  %scevgep250 = getelementptr double, double* %lsr.iv244, i64 -4
  %.223.us.3 = load double, double* %scevgep250, align 8
  %.15.i.us.3 = fmul double %.223.us.3, 2.000000e+00
  %.29.i.us.3 = fadd double %.15.i.us.3, -1.000000e+00
  %scevgep239 = getelementptr double, double* %lsr.iv234, i64 -4
  store double %.29.i.us.3, double* %scevgep239, align 8
  %scevgep249 = getelementptr double, double* %lsr.iv244, i64 -3
  %.223.us.4 = load double, double* %scevgep249, align 8
  %.15.i.us.4 = fmul double %.223.us.4, 2.000000e+00
  %.29.i.us.4 = fadd double %.15.i.us.4, -1.000000e+00
  %scevgep238 = getelementptr double, double* %lsr.iv234, i64 -3
  store double %.29.i.us.4, double* %scevgep238, align 8
  %scevgep248 = getelementptr double, double* %lsr.iv244, i64 -2
  %.223.us.5 = load double, double* %scevgep248, align 8
  %.15.i.us.5 = fmul double %.223.us.5, 2.000000e+00
  %.29.i.us.5 = fadd double %.15.i.us.5, -1.000000e+00
  %scevgep237 = getelementptr double, double* %lsr.iv234, i64 -2
  store double %.29.i.us.5, double* %scevgep237, align 8
  %scevgep247 = getelementptr double, double* %lsr.iv244, i64 -1
  %.223.us.6 = load double, double* %scevgep247, align 8
  %.15.i.us.6 = fmul double %.223.us.6, 2.000000e+00
  %.29.i.us.6 = fadd double %.15.i.us.6, -1.000000e+00
  %scevgep236 = getelementptr double, double* %lsr.iv234, i64 -1
  store double %.29.i.us.6, double* %scevgep236, align 8
  %.223.us.7 = load double, double* %lsr.iv244, align 8
  %.15.i.us.7 = fmul double %.223.us.7, 2.000000e+00
  %.29.i.us.7 = fadd double %.15.i.us.7, -1.000000e+00
  store double %.29.i.us.7, double* %lsr.iv234, align 8
  %lsr.iv.next232 = add i64 %lsr.iv231, -8
  %scevgep235 = getelementptr double, double* %lsr.iv234, i64 8
  %scevgep246 = getelementptr double, double* %lsr.iv244, i64 8
  %exitcond168.7 = icmp eq i64 %lsr.iv.next232, 0
  br i1 %exitcond168.7, label %for.end.1, label %for.body.1.endif.endif.us, !llvm.loop !18

for.end.1:                                        ; preds = %for.body.1.endif.endif, %for.body.1.endif.endif.us, %for.body.1.endif.endif.us.prol.loopexit, %middle.block204, %middle.block, %for.body.1.endif.endif.prol.loopexit, %for.end.endif.endif
  call void @NRT_decref(i8* %.37)
  %.292 = call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.36, i32 32)
  %.5.i27 = getelementptr i8, i8* %.292, i64 24
  %135 = bitcast i8* %.5.i27 to double**
  %.6.i28118 = load double*, double** %135, align 8
  %.316146 = icmp sgt i32 %arg.n, 0
  br i1 %.316146, label %for.body.2.lr.ph, label %B127.i48

for.body.2.lr.ph:                                 ; preds = %for.end.1
  %.318 = tail call { i32, [624 x i32], i32, double, i32 }* @numba_get_np_random_state()
  br label %for.body.2

for.body.1.endif.endif:                           ; preds = %for.body.1.endif.endif, %for.body.1.endif.endif.preheader492.new
  %lsr.iv286 = phi double* [ %scevgep287, %for.body.1.endif.endif ], [ %scevgep285, %for.body.1.endif.endif.preheader492.new ]
  %lsr.iv283 = phi i64 [ %lsr.iv.next284, %for.body.1.endif.endif ], [ %15, %for.body.1.endif.endif.preheader492.new ]
  %.223 = load double, double* %.6.i116, align 8
  %.15.i = fmul double %.223, 2.000000e+00
  %.29.i = fadd double %.15.i, -1.000000e+00
  %scevgep294 = getelementptr double, double* %lsr.iv286, i64 -7
  store double %.29.i, double* %scevgep294, align 8
  %.223.1 = load double, double* %.6.i116, align 8
  %.15.i.1 = fmul double %.223.1, 2.000000e+00
  %.29.i.1 = fadd double %.15.i.1, -1.000000e+00
  %scevgep293 = getelementptr double, double* %lsr.iv286, i64 -6
  store double %.29.i.1, double* %scevgep293, align 8
  %.223.2 = load double, double* %.6.i116, align 8
  %.15.i.2 = fmul double %.223.2, 2.000000e+00
  %.29.i.2 = fadd double %.15.i.2, -1.000000e+00
  %scevgep292 = getelementptr double, double* %lsr.iv286, i64 -5
  store double %.29.i.2, double* %scevgep292, align 8
  %.223.3 = load double, double* %.6.i116, align 8
  %.15.i.3 = fmul double %.223.3, 2.000000e+00
  %.29.i.3 = fadd double %.15.i.3, -1.000000e+00
  %scevgep291 = getelementptr double, double* %lsr.iv286, i64 -4
  store double %.29.i.3, double* %scevgep291, align 8
  %.223.4 = load double, double* %.6.i116, align 8
  %.15.i.4 = fmul double %.223.4, 2.000000e+00
  %.29.i.4 = fadd double %.15.i.4, -1.000000e+00
  %scevgep290 = getelementptr double, double* %lsr.iv286, i64 -3
  store double %.29.i.4, double* %scevgep290, align 8
  %.223.5 = load double, double* %.6.i116, align 8
  %.15.i.5 = fmul double %.223.5, 2.000000e+00
  %.29.i.5 = fadd double %.15.i.5, -1.000000e+00
  %scevgep289 = getelementptr double, double* %lsr.iv286, i64 -2
  store double %.29.i.5, double* %scevgep289, align 8
  %.223.6 = load double, double* %.6.i116, align 8
  %.15.i.6 = fmul double %.223.6, 2.000000e+00
  %.29.i.6 = fadd double %.15.i.6, -1.000000e+00
  %scevgep288 = getelementptr double, double* %lsr.iv286, i64 -1
  store double %.29.i.6, double* %scevgep288, align 8
  %.223.7 = load double, double* %.6.i116, align 8
  %.15.i.7 = fmul double %.223.7, 2.000000e+00
  %.29.i.7 = fadd double %.15.i.7, -1.000000e+00
  store double %.29.i.7, double* %lsr.iv286, align 8
  %lsr.iv.next284 = add i64 %lsr.iv283, -8
  %scevgep287 = getelementptr double, double* %lsr.iv286, i64 8
  %exitcond169.7 = icmp eq i64 %lsr.iv.next284, 0
  br i1 %exitcond169.7, label %for.end.1, label %for.body.1.endif.endif, !llvm.loop !19

for.body.2:                                       ; preds = %for.body.2.endif.endif, %for.body.2.lr.ph
  %lsr.iv229 = phi double* [ %scevgep230, %for.body.2.endif.endif ], [ %.6.i28118, %for.body.2.lr.ph ]
  %lsr.iv227 = phi i64 [ %lsr.iv.next228, %for.body.2.endif.endif ], [ %.28, %for.body.2.lr.ph ]
  %136 = bitcast { i32, [624 x i32], i32, double, i32 }* %.318 to i32*
  %.320 = load i32, i32* %136, align 4
  %.321 = icmp ugt i32 %.320, 623
  br i1 %.321, label %for.body.2.if, label %for.body.2.endif, !prof !0

B115.i44:                                         ; preds = %for.body.2.endif.endif
  %.204.i43 = icmp eq i32 %arg.n, 1
  br i1 %.204.i43, label %for.end.2.endif.endif, label %B127.i48

B127.i48:                                         ; preds = %B115.i44, %for.end.1
  br label %for.end.2.endif.endif

for.body.2.if:                                    ; preds = %for.body.2
  %137 = bitcast { i32, [624 x i32], i32, double, i32 }* %.318 to i32*
  call void @numba_rnd_shuffle({ i32, [624 x i32], i32, double, i32 }* nonnull %.318)
  store i32 0, i32* %137, align 4
  br label %for.body.2.endif

for.body.2.endif:                                 ; preds = %for.body.2.if, %for.body.2
  %.326 = phi i32 [ 0, %for.body.2.if ], [ %.320, %for.body.2 ]
  %138 = bitcast { i32, [624 x i32], i32, double, i32 }* %.318 to i32*
  %139 = sext i32 %.326 to i64
  %.328 = getelementptr inbounds { i32, [624 x i32], i32, double, i32 }, { i32, [624 x i32], i32, double, i32 }* %.318, i64 0, i32 1, i64 %139
  %.329 = load i32, i32* %.328, align 4
  %.330 = add i32 %.326, 1
  store i32 %.330, i32* %138, align 4
  %.332 = lshr i32 %.329, 11
  %.333 = xor i32 %.332, %.329
  %.334 = shl i32 %.333, 7
  %.335 = and i32 %.334, -1658038656
  %.336 = xor i32 %.335, %.333
  %.337 = shl i32 %.336, 15
  %.338 = and i32 %.337, -272236544
  %.339 = xor i32 %.338, %.336
  %.340 = lshr i32 %.339, 18
  %.341 = xor i32 %.340, %.339
  %.342 = lshr i32 %.341, 5
  %.345 = icmp ugt i32 %.330, 623
  br i1 %.345, label %for.body.2.endif.if, label %for.body.2.endif.endif, !prof !0

for.body.2.endif.if:                              ; preds = %for.body.2.endif
  %140 = bitcast { i32, [624 x i32], i32, double, i32 }* %.318 to i32*
  call void @numba_rnd_shuffle({ i32, [624 x i32], i32, double, i32 }* nonnull %.318)
  store i32 0, i32* %140, align 4
  br label %for.body.2.endif.endif

for.body.2.endif.endif:                           ; preds = %for.body.2.endif.if, %for.body.2.endif
  %.350 = phi i32 [ 0, %for.body.2.endif.if ], [ %.330, %for.body.2.endif ]
  %141 = bitcast { i32, [624 x i32], i32, double, i32 }* %.318 to i32*
  %142 = sext i32 %.350 to i64
  %.352 = getelementptr inbounds { i32, [624 x i32], i32, double, i32 }, { i32, [624 x i32], i32, double, i32 }* %.318, i64 0, i32 1, i64 %142
  %.353 = load i32, i32* %.352, align 4
  %.354 = add i32 %.350, 1
  store i32 %.354, i32* %141, align 4
  %.356 = lshr i32 %.353, 11
  %.357 = xor i32 %.356, %.353
  %.358 = shl i32 %.357, 7
  %.359 = and i32 %.358, -1658038656
  %.360 = xor i32 %.359, %.357
  %.361 = shl i32 %.360, 15
  %.362 = and i32 %.361, -272236544
  %.363 = xor i32 %.362, %.360
  %.364 = lshr i32 %.363, 18
  %.365 = xor i32 %.364, %.363
  %.366 = lshr i32 %.365, 6
  %.367 = uitofp i32 %.342 to double
  %.368 = uitofp i32 %.366 to double
  %.369 = fmul double %.367, 0x4190000000000000
  %.370 = fadd double %.369, %.368
  %.371 = fmul double %.370, 0x3CA0000000000000
  store double %.371, double* %lsr.iv229, align 8
  %lsr.iv.next228 = add i64 %lsr.iv227, -1
  %scevgep230 = getelementptr double, double* %lsr.iv229, i64 1
  %exitcond167 = icmp eq i64 %lsr.iv.next228, 0
  br i1 %exitcond167, label %B115.i44, label %for.body.2

for.end.2.endif.endif:                            ; preds = %B127.i48, %B115.i44
  %.428177 = phi i64 [ %.28, %B127.i48 ], [ 1, %B115.i44 ]
  %.432 = shl nsw i64 %.428177, 3
  %.433 = call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.432, i32 32)
  %.5.i112 = getelementptr i8, i8* %.433, i64 24
  %143 = bitcast i8* %.5.i112 to double**
  %.6.i113119 = load double*, double** %143, align 8
  %.468143 = icmp sgt i64 %.428177, 0
  br i1 %.468143, label %for.body.3.endif.endif.lr.ph, label %B115.i100

for.body.3.endif.endif.lr.ph:                     ; preds = %for.end.2.endif.endif
  %.470 = icmp ugt i32 %arg.n, 1
  br i1 %.470, label %for.body.3.endif.endif.us.preheader, label %for.body.3.endif.endif.preheader

for.body.3.endif.endif.preheader:                 ; preds = %for.body.3.endif.endif.lr.ph
  %144 = icmp ult i64 %.428177, 16
  br i1 %144, label %for.body.3.endif.endif.preheader489, label %min.iters.checked239

for.body.3.endif.endif.preheader489:              ; preds = %middle.block236, %vector.memcheck253, %min.iters.checked239, %for.body.3.endif.endif.preheader
  %loop.index.3145.ph = phi i64 [ 0, %vector.memcheck253 ], [ 0, %min.iters.checked239 ], [ 0, %for.body.3.endif.endif.preheader ], [ %n.vec241, %middle.block236 ]
  %145 = sub nsw i64 %.428177, %loop.index.3145.ph
  %146 = add nsw i64 %.428177, -1
  %147 = sub nsw i64 %146, %loop.index.3145.ph
  %xtraiter514 = and i64 %145, 7
  %lcmp.mod515 = icmp eq i64 %xtraiter514, 0
  br i1 %lcmp.mod515, label %for.body.3.endif.endif.prol.loopexit, label %for.body.3.endif.endif.prol.preheader

for.body.3.endif.endif.prol.preheader:            ; preds = %for.body.3.endif.endif.preheader489
  %148 = sub i64 0, %xtraiter514
  br label %for.body.3.endif.endif.prol

for.body.3.endif.endif.prol:                      ; preds = %for.body.3.endif.endif.prol, %for.body.3.endif.endif.prol.preheader
  %lsr.iv212 = phi i64 [ %lsr.iv.next213, %for.body.3.endif.endif.prol ], [ %148, %for.body.3.endif.endif.prol.preheader ]
  %loop.index.3145.prol = phi i64 [ %.499.prol, %for.body.3.endif.endif.prol ], [ %loop.index.3145.ph, %for.body.3.endif.endif.prol.preheader ]
  %.478.prol = load double, double* %.6.i28118, align 8
  %.15.i114.prol = fmul double %.478.prol, 2.000000e+00
  %.29.i115.prol = fadd double %.15.i114.prol, -1.000000e+00
  %scevgep211 = getelementptr double, double* %.6.i113119, i64 %loop.index.3145.prol
  store double %.29.i115.prol, double* %scevgep211, align 8
  %.499.prol = add nuw nsw i64 %loop.index.3145.prol, 1
  %lsr.iv.next213 = add nsw i64 %lsr.iv212, 1
  %prol.iter516.cmp = icmp eq i64 %lsr.iv.next213, 0
  br i1 %prol.iter516.cmp, label %for.body.3.endif.endif.prol.loopexit, label %for.body.3.endif.endif.prol, !llvm.loop !20

for.body.3.endif.endif.prol.loopexit:             ; preds = %for.body.3.endif.endif.prol, %for.body.3.endif.endif.preheader489
  %loop.index.3145.unr = phi i64 [ %loop.index.3145.ph, %for.body.3.endif.endif.preheader489 ], [ %.499.prol, %for.body.3.endif.endif.prol ]
  %149 = icmp ult i64 %147, 7
  br i1 %149, label %B115.i100, label %for.body.3.endif.endif.preheader489.new

for.body.3.endif.endif.preheader489.new:          ; preds = %for.body.3.endif.endif.prol.loopexit
  %150 = sub i64 %.428177, %loop.index.3145.unr
  %151 = add i64 %loop.index.3145.unr, 7
  %scevgep201 = getelementptr double, double* %.6.i113119, i64 %151
  br label %for.body.3.endif.endif

min.iters.checked239:                             ; preds = %for.body.3.endif.endif.preheader
  %n.vec241 = and i64 %.428177, -16
  %cmp.zero242 = icmp eq i64 %n.vec241, 0
  br i1 %cmp.zero242, label %for.body.3.endif.endif.preheader489, label %vector.memcheck253

vector.memcheck253:                               ; preds = %min.iters.checked239
  %scevgep245 = getelementptr double, double* %.6.i113119, i64 %.428177
  %bound0249 = icmp ult double* %.6.i113119, %.6.i28118
  %bound1250 = icmp ult double* %.6.i28118, %scevgep245
  %memcheck.conflict252 = and i1 %bound0249, %bound1250
  br i1 %memcheck.conflict252, label %for.body.3.endif.endif.preheader489, label %vector.body235.preheader

vector.body235.preheader:                         ; preds = %vector.memcheck253
  %152 = add nsw i64 %n.vec241, -16
  %153 = lshr exact i64 %152, 4
  %154 = and i64 %153, 1
  %lcmp.mod518 = icmp eq i64 %154, 0
  br i1 %lcmp.mod518, label %vector.body235.prol, label %vector.body235.prol.loopexit

vector.body235.prol:                              ; preds = %vector.body235.preheader
  %155 = load double, double* %.6.i28118, align 8, !alias.scope !21
  %156 = insertelement <4 x double> undef, double %155, i32 0
  %157 = shufflevector <4 x double> %156, <4 x double> undef, <4 x i32> zeroinitializer
  %158 = insertelement <4 x double> undef, double %155, i32 0
  %159 = shufflevector <4 x double> %158, <4 x double> undef, <4 x i32> zeroinitializer
  %160 = insertelement <4 x double> undef, double %155, i32 0
  %161 = shufflevector <4 x double> %160, <4 x double> undef, <4 x i32> zeroinitializer
  %162 = insertelement <4 x double> undef, double %155, i32 0
  %163 = shufflevector <4 x double> %162, <4 x double> undef, <4 x i32> zeroinitializer
  %164 = fmul <4 x double> %157, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %165 = fmul <4 x double> %159, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %166 = fmul <4 x double> %161, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %167 = fmul <4 x double> %163, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %168 = fadd <4 x double> %164, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %169 = fadd <4 x double> %165, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %170 = fadd <4 x double> %166, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %171 = fadd <4 x double> %167, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %172 = bitcast double* %.6.i113119 to <4 x double>*
  store <4 x double> %168, <4 x double>* %172, align 8, !alias.scope !24, !noalias !21
  %173 = getelementptr double, double* %.6.i113119, i64 4
  %174 = bitcast double* %173 to <4 x double>*
  store <4 x double> %169, <4 x double>* %174, align 8, !alias.scope !24, !noalias !21
  %175 = getelementptr double, double* %.6.i113119, i64 8
  %176 = bitcast double* %175 to <4 x double>*
  store <4 x double> %170, <4 x double>* %176, align 8, !alias.scope !24, !noalias !21
  %177 = getelementptr double, double* %.6.i113119, i64 12
  %178 = bitcast double* %177 to <4 x double>*
  store <4 x double> %171, <4 x double>* %178, align 8, !alias.scope !24, !noalias !21
  br label %vector.body235.prol.loopexit

vector.body235.prol.loopexit:                     ; preds = %vector.body235.prol, %vector.body235.preheader
  %index255.unr = phi i64 [ 0, %vector.body235.preheader ], [ 16, %vector.body235.prol ]
  %179 = icmp eq i64 %153, 0
  br i1 %179, label %middle.block236, label %vector.body235.preheader.new

vector.body235.preheader.new:                     ; preds = %vector.body235.prol.loopexit
  %180 = load double, double* %.6.i28118, align 8, !alias.scope !21
  %181 = insertelement <4 x double> undef, double %180, i32 0
  %182 = shufflevector <4 x double> %181, <4 x double> undef, <4 x i32> zeroinitializer
  %183 = insertelement <4 x double> undef, double %180, i32 0
  %184 = shufflevector <4 x double> %183, <4 x double> undef, <4 x i32> zeroinitializer
  %185 = insertelement <4 x double> undef, double %180, i32 0
  %186 = shufflevector <4 x double> %185, <4 x double> undef, <4 x i32> zeroinitializer
  %187 = insertelement <4 x double> undef, double %180, i32 0
  %188 = shufflevector <4 x double> %187, <4 x double> undef, <4 x i32> zeroinitializer
  %189 = fmul <4 x double> %182, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %190 = fmul <4 x double> %184, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %191 = fmul <4 x double> %186, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %192 = fmul <4 x double> %188, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %193 = fadd <4 x double> %189, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %194 = fadd <4 x double> %190, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %195 = fadd <4 x double> %191, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %196 = fadd <4 x double> %192, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %197 = load double, double* %.6.i28118, align 8, !alias.scope !21
  %198 = insertelement <4 x double> undef, double %197, i32 0
  %199 = shufflevector <4 x double> %198, <4 x double> undef, <4 x i32> zeroinitializer
  %200 = insertelement <4 x double> undef, double %197, i32 0
  %201 = shufflevector <4 x double> %200, <4 x double> undef, <4 x i32> zeroinitializer
  %202 = insertelement <4 x double> undef, double %197, i32 0
  %203 = shufflevector <4 x double> %202, <4 x double> undef, <4 x i32> zeroinitializer
  %204 = insertelement <4 x double> undef, double %197, i32 0
  %205 = shufflevector <4 x double> %204, <4 x double> undef, <4 x i32> zeroinitializer
  %206 = fmul <4 x double> %199, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %207 = fmul <4 x double> %201, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %208 = fmul <4 x double> %203, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %209 = fmul <4 x double> %205, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %210 = fadd <4 x double> %206, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %211 = fadd <4 x double> %207, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %212 = fadd <4 x double> %208, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %213 = fadd <4 x double> %209, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %214 = sub i64 %n.vec241, %index255.unr
  %215 = add i64 %index255.unr, 28
  %scevgep216 = getelementptr double, double* %.6.i113119, i64 %215
  br label %vector.body235

vector.body235:                                   ; preds = %vector.body235, %vector.body235.preheader.new
  %lsr.iv217 = phi double* [ %scevgep218, %vector.body235 ], [ %scevgep216, %vector.body235.preheader.new ]
  %lsr.iv214 = phi i64 [ %lsr.iv.next215, %vector.body235 ], [ %214, %vector.body235.preheader.new ]
  %lsr.iv217219 = bitcast double* %lsr.iv217 to <4 x double>*
  %scevgep226 = getelementptr <4 x double>, <4 x double>* %lsr.iv217219, i64 -7
  store <4 x double> %193, <4 x double>* %scevgep226, align 8, !alias.scope !24, !noalias !21
  %scevgep225 = getelementptr <4 x double>, <4 x double>* %lsr.iv217219, i64 -6
  store <4 x double> %194, <4 x double>* %scevgep225, align 8, !alias.scope !24, !noalias !21
  %scevgep224 = getelementptr <4 x double>, <4 x double>* %lsr.iv217219, i64 -5
  store <4 x double> %195, <4 x double>* %scevgep224, align 8, !alias.scope !24, !noalias !21
  %scevgep223 = getelementptr <4 x double>, <4 x double>* %lsr.iv217219, i64 -4
  store <4 x double> %196, <4 x double>* %scevgep223, align 8, !alias.scope !24, !noalias !21
  %scevgep222 = getelementptr <4 x double>, <4 x double>* %lsr.iv217219, i64 -3
  store <4 x double> %210, <4 x double>* %scevgep222, align 8, !alias.scope !24, !noalias !21
  %scevgep221 = getelementptr <4 x double>, <4 x double>* %lsr.iv217219, i64 -2
  store <4 x double> %211, <4 x double>* %scevgep221, align 8, !alias.scope !24, !noalias !21
  %scevgep220 = getelementptr <4 x double>, <4 x double>* %lsr.iv217219, i64 -1
  store <4 x double> %212, <4 x double>* %scevgep220, align 8, !alias.scope !24, !noalias !21
  store <4 x double> %213, <4 x double>* %lsr.iv217219, align 8, !alias.scope !24, !noalias !21
  %lsr.iv.next215 = add i64 %lsr.iv214, -32
  %scevgep218 = getelementptr double, double* %lsr.iv217, i64 32
  %216 = icmp eq i64 %lsr.iv.next215, 0
  br i1 %216, label %middle.block236, label %vector.body235, !llvm.loop !26

middle.block236:                                  ; preds = %vector.body235, %vector.body235.prol.loopexit
  %cmp.n258 = icmp eq i64 %.428177, %n.vec241
  br i1 %cmp.n258, label %B115.i100, label %for.body.3.endif.endif.preheader489

for.body.3.endif.endif.us.preheader:              ; preds = %for.body.3.endif.endif.lr.ph
  %217 = icmp ult i64 %.428177, 16
  br i1 %217, label %for.body.3.endif.endif.us.preheader488, label %min.iters.checked269

min.iters.checked269:                             ; preds = %for.body.3.endif.endif.us.preheader
  %n.vec271 = and i64 %.428177, -16
  %cmp.zero272 = icmp eq i64 %n.vec271, 0
  br i1 %cmp.zero272, label %for.body.3.endif.endif.us.preheader488, label %vector.memcheck282

vector.memcheck282:                               ; preds = %min.iters.checked269
  %scevgep274 = getelementptr double, double* %.6.i113119, i64 %.428177
  %scevgep276 = getelementptr double, double* %.6.i28118, i64 %.428177
  %bound0278 = icmp ult double* %.6.i113119, %scevgep276
  %bound1279 = icmp ult double* %.6.i28118, %scevgep274
  %memcheck.conflict281 = and i1 %bound0278, %bound1279
  br i1 %memcheck.conflict281, label %for.body.3.endif.endif.us.preheader488, label %vector.body265.preheader

vector.body265.preheader:                         ; preds = %vector.memcheck282
  %218 = add nsw i64 %n.vec271, -16
  %219 = lshr exact i64 %218, 4
  %220 = and i64 %219, 1
  %lcmp.mod513 = icmp eq i64 %220, 0
  br i1 %lcmp.mod513, label %vector.body265.prol, label %vector.body265.prol.loopexit

vector.body265.prol:                              ; preds = %vector.body265.preheader
  %221 = bitcast double* %.6.i28118 to <4 x double>*
  %wide.load294.prol = load <4 x double>, <4 x double>* %221, align 8, !alias.scope !27
  %222 = getelementptr double, double* %.6.i28118, i64 4
  %223 = bitcast double* %222 to <4 x double>*
  %wide.load295.prol = load <4 x double>, <4 x double>* %223, align 8, !alias.scope !27
  %224 = getelementptr double, double* %.6.i28118, i64 8
  %225 = bitcast double* %224 to <4 x double>*
  %wide.load296.prol = load <4 x double>, <4 x double>* %225, align 8, !alias.scope !27
  %226 = getelementptr double, double* %.6.i28118, i64 12
  %227 = bitcast double* %226 to <4 x double>*
  %wide.load297.prol = load <4 x double>, <4 x double>* %227, align 8, !alias.scope !27
  %228 = fmul <4 x double> %wide.load294.prol, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %229 = fmul <4 x double> %wide.load295.prol, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %230 = fmul <4 x double> %wide.load296.prol, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %231 = fmul <4 x double> %wide.load297.prol, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %232 = fadd <4 x double> %228, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %233 = fadd <4 x double> %229, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %234 = fadd <4 x double> %230, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %235 = fadd <4 x double> %231, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %236 = bitcast double* %.6.i113119 to <4 x double>*
  store <4 x double> %232, <4 x double>* %236, align 8, !alias.scope !30, !noalias !27
  %237 = getelementptr double, double* %.6.i113119, i64 4
  %238 = bitcast double* %237 to <4 x double>*
  store <4 x double> %233, <4 x double>* %238, align 8, !alias.scope !30, !noalias !27
  %239 = getelementptr double, double* %.6.i113119, i64 8
  %240 = bitcast double* %239 to <4 x double>*
  store <4 x double> %234, <4 x double>* %240, align 8, !alias.scope !30, !noalias !27
  %241 = getelementptr double, double* %.6.i113119, i64 12
  %242 = bitcast double* %241 to <4 x double>*
  store <4 x double> %235, <4 x double>* %242, align 8, !alias.scope !30, !noalias !27
  br label %vector.body265.prol.loopexit

vector.body265.prol.loopexit:                     ; preds = %vector.body265.prol, %vector.body265.preheader
  %index284.unr = phi i64 [ 0, %vector.body265.preheader ], [ 16, %vector.body265.prol ]
  %243 = icmp eq i64 %219, 0
  br i1 %243, label %middle.block266, label %vector.body265.preheader.new

vector.body265.preheader.new:                     ; preds = %vector.body265.prol.loopexit
  %244 = sub i64 %n.vec271, %index284.unr
  %245 = add i64 %index284.unr, 28
  %scevgep177 = getelementptr double, double* %.6.i113119, i64 %245
  %scevgep188 = getelementptr double, double* %.6.i28118, i64 %245
  br label %vector.body265

vector.body265:                                   ; preds = %vector.body265, %vector.body265.preheader.new
  %lsr.iv189 = phi double* [ %scevgep190, %vector.body265 ], [ %scevgep188, %vector.body265.preheader.new ]
  %lsr.iv178 = phi double* [ %scevgep179, %vector.body265 ], [ %scevgep177, %vector.body265.preheader.new ]
  %lsr.iv175 = phi i64 [ %lsr.iv.next176, %vector.body265 ], [ %244, %vector.body265.preheader.new ]
  %lsr.iv189191 = bitcast double* %lsr.iv189 to <4 x double>*
  %lsr.iv178180 = bitcast double* %lsr.iv178 to <4 x double>*
  %scevgep198 = getelementptr <4 x double>, <4 x double>* %lsr.iv189191, i64 -7
  %wide.load294 = load <4 x double>, <4 x double>* %scevgep198, align 8, !alias.scope !27
  %scevgep197 = getelementptr <4 x double>, <4 x double>* %lsr.iv189191, i64 -6
  %wide.load295 = load <4 x double>, <4 x double>* %scevgep197, align 8, !alias.scope !27
  %scevgep196 = getelementptr <4 x double>, <4 x double>* %lsr.iv189191, i64 -5
  %wide.load296 = load <4 x double>, <4 x double>* %scevgep196, align 8, !alias.scope !27
  %scevgep195 = getelementptr <4 x double>, <4 x double>* %lsr.iv189191, i64 -4
  %wide.load297 = load <4 x double>, <4 x double>* %scevgep195, align 8, !alias.scope !27
  %246 = fmul <4 x double> %wide.load294, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %247 = fmul <4 x double> %wide.load295, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %248 = fmul <4 x double> %wide.load296, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %249 = fmul <4 x double> %wide.load297, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %250 = fadd <4 x double> %246, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %251 = fadd <4 x double> %247, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %252 = fadd <4 x double> %248, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %253 = fadd <4 x double> %249, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %scevgep187 = getelementptr <4 x double>, <4 x double>* %lsr.iv178180, i64 -7
  store <4 x double> %250, <4 x double>* %scevgep187, align 8, !alias.scope !30, !noalias !27
  %scevgep186 = getelementptr <4 x double>, <4 x double>* %lsr.iv178180, i64 -6
  store <4 x double> %251, <4 x double>* %scevgep186, align 8, !alias.scope !30, !noalias !27
  %scevgep185 = getelementptr <4 x double>, <4 x double>* %lsr.iv178180, i64 -5
  store <4 x double> %252, <4 x double>* %scevgep185, align 8, !alias.scope !30, !noalias !27
  %scevgep184 = getelementptr <4 x double>, <4 x double>* %lsr.iv178180, i64 -4
  store <4 x double> %253, <4 x double>* %scevgep184, align 8, !alias.scope !30, !noalias !27
  %scevgep194 = getelementptr <4 x double>, <4 x double>* %lsr.iv189191, i64 -3
  %wide.load294.1 = load <4 x double>, <4 x double>* %scevgep194, align 8, !alias.scope !27
  %scevgep193 = getelementptr <4 x double>, <4 x double>* %lsr.iv189191, i64 -2
  %wide.load295.1 = load <4 x double>, <4 x double>* %scevgep193, align 8, !alias.scope !27
  %scevgep192 = getelementptr <4 x double>, <4 x double>* %lsr.iv189191, i64 -1
  %wide.load296.1 = load <4 x double>, <4 x double>* %scevgep192, align 8, !alias.scope !27
  %wide.load297.1 = load <4 x double>, <4 x double>* %lsr.iv189191, align 8, !alias.scope !27
  %254 = fmul <4 x double> %wide.load294.1, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %255 = fmul <4 x double> %wide.load295.1, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %256 = fmul <4 x double> %wide.load296.1, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %257 = fmul <4 x double> %wide.load297.1, <double 2.000000e+00, double 2.000000e+00, double 2.000000e+00, double 2.000000e+00>
  %258 = fadd <4 x double> %254, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %259 = fadd <4 x double> %255, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %260 = fadd <4 x double> %256, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %261 = fadd <4 x double> %257, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %scevgep183 = getelementptr <4 x double>, <4 x double>* %lsr.iv178180, i64 -3
  store <4 x double> %258, <4 x double>* %scevgep183, align 8, !alias.scope !30, !noalias !27
  %scevgep182 = getelementptr <4 x double>, <4 x double>* %lsr.iv178180, i64 -2
  store <4 x double> %259, <4 x double>* %scevgep182, align 8, !alias.scope !30, !noalias !27
  %scevgep181 = getelementptr <4 x double>, <4 x double>* %lsr.iv178180, i64 -1
  store <4 x double> %260, <4 x double>* %scevgep181, align 8, !alias.scope !30, !noalias !27
  store <4 x double> %261, <4 x double>* %lsr.iv178180, align 8, !alias.scope !30, !noalias !27
  %lsr.iv.next176 = add i64 %lsr.iv175, -32
  %scevgep179 = getelementptr double, double* %lsr.iv178, i64 32
  %scevgep190 = getelementptr double, double* %lsr.iv189, i64 32
  %262 = icmp eq i64 %lsr.iv.next176, 0
  br i1 %262, label %middle.block266, label %vector.body265, !llvm.loop !32

middle.block266:                                  ; preds = %vector.body265, %vector.body265.prol.loopexit
  %cmp.n287 = icmp eq i64 %.428177, %n.vec271
  br i1 %cmp.n287, label %B115.i100, label %for.body.3.endif.endif.us.preheader488

for.body.3.endif.endif.us.preheader488:           ; preds = %middle.block266, %vector.memcheck282, %min.iters.checked269, %for.body.3.endif.endif.us.preheader
  %loop.index.3145.us.ph = phi i64 [ 0, %vector.memcheck282 ], [ 0, %min.iters.checked269 ], [ 0, %for.body.3.endif.endif.us.preheader ], [ %n.vec271, %middle.block266 ]
  %263 = sub nsw i64 %.428177, %loop.index.3145.us.ph
  %264 = add nsw i64 %.428177, -1
  %265 = sub nsw i64 %264, %loop.index.3145.us.ph
  %xtraiter509 = and i64 %263, 7
  %lcmp.mod510 = icmp eq i64 %xtraiter509, 0
  br i1 %lcmp.mod510, label %for.body.3.endif.endif.us.prol.loopexit, label %for.body.3.endif.endif.us.prol.preheader

for.body.3.endif.endif.us.prol.preheader:         ; preds = %for.body.3.endif.endif.us.preheader488
  %266 = sub i64 0, %xtraiter509
  br label %for.body.3.endif.endif.us.prol

for.body.3.endif.endif.us.prol:                   ; preds = %for.body.3.endif.endif.us.prol, %for.body.3.endif.endif.us.prol.preheader
  %lsr.iv173 = phi i64 [ %lsr.iv.next174, %for.body.3.endif.endif.us.prol ], [ %266, %for.body.3.endif.endif.us.prol.preheader ]
  %loop.index.3145.us.prol = phi i64 [ %.499.us.prol, %for.body.3.endif.endif.us.prol ], [ %loop.index.3145.us.ph, %for.body.3.endif.endif.us.prol.preheader ]
  %scevgep172 = getelementptr double, double* %.6.i28118, i64 %loop.index.3145.us.prol
  %.478.us.prol = load double, double* %scevgep172, align 8
  %.15.i114.us.prol = fmul double %.478.us.prol, 2.000000e+00
  %.29.i115.us.prol = fadd double %.15.i114.us.prol, -1.000000e+00
  %scevgep171 = getelementptr double, double* %.6.i113119, i64 %loop.index.3145.us.prol
  store double %.29.i115.us.prol, double* %scevgep171, align 8
  %.499.us.prol = add nuw nsw i64 %loop.index.3145.us.prol, 1
  %lsr.iv.next174 = add nsw i64 %lsr.iv173, 1
  %prol.iter511.cmp = icmp eq i64 %lsr.iv.next174, 0
  br i1 %prol.iter511.cmp, label %for.body.3.endif.endif.us.prol.loopexit, label %for.body.3.endif.endif.us.prol, !llvm.loop !33

for.body.3.endif.endif.us.prol.loopexit:          ; preds = %for.body.3.endif.endif.us.prol, %for.body.3.endif.endif.us.preheader488
  %loop.index.3145.us.unr = phi i64 [ %loop.index.3145.us.ph, %for.body.3.endif.endif.us.preheader488 ], [ %.499.us.prol, %for.body.3.endif.endif.us.prol ]
  %267 = icmp ult i64 %265, 7
  br i1 %267, label %B115.i100, label %for.body.3.endif.endif.us.preheader488.new

for.body.3.endif.endif.us.preheader488.new:       ; preds = %for.body.3.endif.endif.us.prol.loopexit
  %268 = sub i64 %.428177, %loop.index.3145.us.unr
  %269 = add i64 %loop.index.3145.us.unr, 7
  %scevgep151 = getelementptr double, double* %.6.i113119, i64 %269
  %scevgep161 = getelementptr double, double* %.6.i28118, i64 %269
  br label %for.body.3.endif.endif.us

for.body.3.endif.endif.us:                        ; preds = %for.body.3.endif.endif.us, %for.body.3.endif.endif.us.preheader488.new
  %lsr.iv162 = phi double* [ %scevgep163, %for.body.3.endif.endif.us ], [ %scevgep161, %for.body.3.endif.endif.us.preheader488.new ]
  %lsr.iv152 = phi double* [ %scevgep153, %for.body.3.endif.endif.us ], [ %scevgep151, %for.body.3.endif.endif.us.preheader488.new ]
  %lsr.iv149 = phi i64 [ %lsr.iv.next150, %for.body.3.endif.endif.us ], [ %268, %for.body.3.endif.endif.us.preheader488.new ]
  %scevgep170 = getelementptr double, double* %lsr.iv162, i64 -7
  %.478.us = load double, double* %scevgep170, align 8
  %.15.i114.us = fmul double %.478.us, 2.000000e+00
  %.29.i115.us = fadd double %.15.i114.us, -1.000000e+00
  %scevgep160 = getelementptr double, double* %lsr.iv152, i64 -7
  store double %.29.i115.us, double* %scevgep160, align 8
  %scevgep169 = getelementptr double, double* %lsr.iv162, i64 -6
  %.478.us.1 = load double, double* %scevgep169, align 8
  %.15.i114.us.1 = fmul double %.478.us.1, 2.000000e+00
  %.29.i115.us.1 = fadd double %.15.i114.us.1, -1.000000e+00
  %scevgep159 = getelementptr double, double* %lsr.iv152, i64 -6
  store double %.29.i115.us.1, double* %scevgep159, align 8
  %scevgep168 = getelementptr double, double* %lsr.iv162, i64 -5
  %.478.us.2 = load double, double* %scevgep168, align 8
  %.15.i114.us.2 = fmul double %.478.us.2, 2.000000e+00
  %.29.i115.us.2 = fadd double %.15.i114.us.2, -1.000000e+00
  %scevgep158 = getelementptr double, double* %lsr.iv152, i64 -5
  store double %.29.i115.us.2, double* %scevgep158, align 8
  %scevgep167 = getelementptr double, double* %lsr.iv162, i64 -4
  %.478.us.3 = load double, double* %scevgep167, align 8
  %.15.i114.us.3 = fmul double %.478.us.3, 2.000000e+00
  %.29.i115.us.3 = fadd double %.15.i114.us.3, -1.000000e+00
  %scevgep157 = getelementptr double, double* %lsr.iv152, i64 -4
  store double %.29.i115.us.3, double* %scevgep157, align 8
  %scevgep166 = getelementptr double, double* %lsr.iv162, i64 -3
  %.478.us.4 = load double, double* %scevgep166, align 8
  %.15.i114.us.4 = fmul double %.478.us.4, 2.000000e+00
  %.29.i115.us.4 = fadd double %.15.i114.us.4, -1.000000e+00
  %scevgep156 = getelementptr double, double* %lsr.iv152, i64 -3
  store double %.29.i115.us.4, double* %scevgep156, align 8
  %scevgep165 = getelementptr double, double* %lsr.iv162, i64 -2
  %.478.us.5 = load double, double* %scevgep165, align 8
  %.15.i114.us.5 = fmul double %.478.us.5, 2.000000e+00
  %.29.i115.us.5 = fadd double %.15.i114.us.5, -1.000000e+00
  %scevgep155 = getelementptr double, double* %lsr.iv152, i64 -2
  store double %.29.i115.us.5, double* %scevgep155, align 8
  %scevgep164 = getelementptr double, double* %lsr.iv162, i64 -1
  %.478.us.6 = load double, double* %scevgep164, align 8
  %.15.i114.us.6 = fmul double %.478.us.6, 2.000000e+00
  %.29.i115.us.6 = fadd double %.15.i114.us.6, -1.000000e+00
  %scevgep154 = getelementptr double, double* %lsr.iv152, i64 -1
  store double %.29.i115.us.6, double* %scevgep154, align 8
  %.478.us.7 = load double, double* %lsr.iv162, align 8
  %.15.i114.us.7 = fmul double %.478.us.7, 2.000000e+00
  %.29.i115.us.7 = fadd double %.15.i114.us.7, -1.000000e+00
  store double %.29.i115.us.7, double* %lsr.iv152, align 8
  %lsr.iv.next150 = add i64 %lsr.iv149, -8
  %scevgep153 = getelementptr double, double* %lsr.iv152, i64 8
  %scevgep163 = getelementptr double, double* %lsr.iv162, i64 8
  %exitcond165.7 = icmp eq i64 %lsr.iv.next150, 0
  br i1 %exitcond165.7, label %B115.i100, label %for.body.3.endif.endif.us, !llvm.loop !34

B115.i100:                                        ; preds = %for.body.3.endif.endif, %for.body.3.endif.endif.us, %for.body.3.endif.endif.us.prol.loopexit, %middle.block266, %middle.block236, %for.body.3.endif.endif.prol.loopexit, %for.end.2.endif.endif
  call void @NRT_decref(i8* %.292)
  %.204.i99 = icmp eq i64 %.173173, 1
  br i1 %.204.i99, label %for.end.3.endif.endif.endif.endif, label %B79.i70

for.body.3.endif.endif:                           ; preds = %for.body.3.endif.endif, %for.body.3.endif.endif.preheader489.new
  %lsr.iv202 = phi double* [ %scevgep203, %for.body.3.endif.endif ], [ %scevgep201, %for.body.3.endif.endif.preheader489.new ]
  %lsr.iv199 = phi i64 [ %lsr.iv.next200, %for.body.3.endif.endif ], [ %150, %for.body.3.endif.endif.preheader489.new ]
  %.478 = load double, double* %.6.i28118, align 8
  %.15.i114 = fmul double %.478, 2.000000e+00
  %.29.i115 = fadd double %.15.i114, -1.000000e+00
  %scevgep210 = getelementptr double, double* %lsr.iv202, i64 -7
  store double %.29.i115, double* %scevgep210, align 8
  %.478.1 = load double, double* %.6.i28118, align 8
  %.15.i114.1 = fmul double %.478.1, 2.000000e+00
  %.29.i115.1 = fadd double %.15.i114.1, -1.000000e+00
  %scevgep209 = getelementptr double, double* %lsr.iv202, i64 -6
  store double %.29.i115.1, double* %scevgep209, align 8
  %.478.2 = load double, double* %.6.i28118, align 8
  %.15.i114.2 = fmul double %.478.2, 2.000000e+00
  %.29.i115.2 = fadd double %.15.i114.2, -1.000000e+00
  %scevgep208 = getelementptr double, double* %lsr.iv202, i64 -5
  store double %.29.i115.2, double* %scevgep208, align 8
  %.478.3 = load double, double* %.6.i28118, align 8
  %.15.i114.3 = fmul double %.478.3, 2.000000e+00
  %.29.i115.3 = fadd double %.15.i114.3, -1.000000e+00
  %scevgep207 = getelementptr double, double* %lsr.iv202, i64 -4
  store double %.29.i115.3, double* %scevgep207, align 8
  %.478.4 = load double, double* %.6.i28118, align 8
  %.15.i114.4 = fmul double %.478.4, 2.000000e+00
  %.29.i115.4 = fadd double %.15.i114.4, -1.000000e+00
  %scevgep206 = getelementptr double, double* %lsr.iv202, i64 -3
  store double %.29.i115.4, double* %scevgep206, align 8
  %.478.5 = load double, double* %.6.i28118, align 8
  %.15.i114.5 = fmul double %.478.5, 2.000000e+00
  %.29.i115.5 = fadd double %.15.i114.5, -1.000000e+00
  %scevgep205 = getelementptr double, double* %lsr.iv202, i64 -2
  store double %.29.i115.5, double* %scevgep205, align 8
  %.478.6 = load double, double* %.6.i28118, align 8
  %.15.i114.6 = fmul double %.478.6, 2.000000e+00
  %.29.i115.6 = fadd double %.15.i114.6, -1.000000e+00
  %scevgep204 = getelementptr double, double* %lsr.iv202, i64 -1
  store double %.29.i115.6, double* %scevgep204, align 8
  %.478.7 = load double, double* %.6.i28118, align 8
  %.15.i114.7 = fmul double %.478.7, 2.000000e+00
  %.29.i115.7 = fadd double %.15.i114.7, -1.000000e+00
  store double %.29.i115.7, double* %lsr.iv202, align 8
  %lsr.iv.next200 = add i64 %lsr.iv199, -8
  %scevgep203 = getelementptr double, double* %lsr.iv202, i64 8
  %exitcond166.7 = icmp eq i64 %lsr.iv.next200, 0
  br i1 %exitcond166.7, label %B115.i100, label %for.body.3.endif.endif, !llvm.loop !35

B79.i70:                                          ; preds = %B115.i100
  %.131.i67 = icmp eq i64 %.428177, %.173173
  %.146.i68 = icmp eq i64 %.428177, 1
  %or.cond.i69 = or i1 %.146.i68, %.131.i67
  br i1 %or.cond.i69, label %for.end.3.endif.endif.endif.endif, label %for.end.3.endif.endif.endif.if

for.end.3.endif.endif.endif.if:                   ; preds = %B79.i70
  store { i8*, i32 }* @.const.picklebuf.4509323848, { i8*, i32 }** %excinfo, align 8
  ret i32 1

for.end.3.endif.endif.endif.endif:                ; preds = %B79.i70, %B115.i100
  %.618181 = phi i64 [ %.173173, %B79.i70 ], [ %.428177, %B115.i100 ]
  %.623 = call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.618181, i32 32)
  %.5.i56 = getelementptr i8, i8* %.623, i64 24
  %270 = bitcast i8* %.5.i56 to i8**
  %.6.i57 = load i8*, i8** %270, align 8
  %.659124 = icmp sgt i64 %.618181, 0
  br i1 %.659124, label %for.body.4.endif.endif.endif.lr.ph, label %for.end.4

for.body.4.endif.endif.endif.lr.ph:               ; preds = %for.end.3.endif.endif.endif.endif
  %.661 = icmp ugt i64 %.173173, 1
  br i1 %.661, label %for.body.4.endif.endif.endif.lr.ph.split.us, label %for.body.4.endif.endif.endif.lr.ph.split

for.body.4.endif.endif.endif.lr.ph.split.us:      ; preds = %for.body.4.endif.endif.endif.lr.ph
  %271 = icmp ugt i64 %.428177, 1
  br i1 %271, label %for.body.4.endif.endif.endif.us.us.preheader, label %for.body.4.endif.endif.endif.us.preheader

for.body.4.endif.endif.endif.us.preheader:        ; preds = %for.body.4.endif.endif.endif.lr.ph.split.us
  %272 = icmp ult i64 %.618181, 16
  br i1 %272, label %for.body.4.endif.endif.endif.us.preheader482, label %min.iters.checked373

for.body.4.endif.endif.endif.us.preheader482:     ; preds = %middle.block370, %vector.memcheck391, %min.iters.checked373, %for.body.4.endif.endif.endif.us.preheader
  %loop.index.4127.us.ph = phi i64 [ 0, %vector.memcheck391 ], [ 0, %min.iters.checked373 ], [ 0, %for.body.4.endif.endif.endif.us.preheader ], [ %n.vec375, %middle.block370 ]
  %273 = sub nsw i64 %.618181, %loop.index.4127.us.ph
  %274 = add nsw i64 %.618181, -1
  %275 = sub nsw i64 %274, %loop.index.4127.us.ph
  %xtraiter500 = and i64 %273, 3
  %lcmp.mod501 = icmp eq i64 %xtraiter500, 0
  br i1 %lcmp.mod501, label %for.body.4.endif.endif.endif.us.prol.loopexit, label %for.body.4.endif.endif.endif.us.prol.preheader

for.body.4.endif.endif.endif.us.prol.preheader:   ; preds = %for.body.4.endif.endif.endif.us.preheader482
  %276 = sub i64 0, %xtraiter500
  br label %for.body.4.endif.endif.endif.us.prol

for.body.4.endif.endif.endif.us.prol:             ; preds = %for.body.4.endif.endif.endif.us.prol, %for.body.4.endif.endif.endif.us.prol.preheader
  %lsr.iv77 = phi i64 [ %lsr.iv.next78, %for.body.4.endif.endif.endif.us.prol ], [ %276, %for.body.4.endif.endif.endif.us.prol.preheader ]
  %loop.index.4127.us.prol = phi i64 [ %.701.us.prol, %for.body.4.endif.endif.endif.us.prol ], [ %loop.index.4127.us.ph, %for.body.4.endif.endif.endif.us.prol.preheader ]
  %scevgep76 = getelementptr double, double* %.6.i26117, i64 %loop.index.4127.us.prol
  %.669.us.prol = load double, double* %scevgep76, align 8
  %.678.us.prol = load double, double* %.6.i113119, align 8
  %.18.i.us.prol = fmul double %.669.us.prol, %.669.us.prol
  %.33.i.us.prol = fmul double %.678.us.prol, %.678.us.prol
  %.45.i.us.prol = fadd double %.18.i.us.prol, %.33.i.us.prol
  %.59.i.us.prol = fcmp olt double %.45.i.us.prol, 1.000000e+00
  %.74.i.us.prol = zext i1 %.59.i.us.prol to i8
  %scevgep75 = getelementptr i8, i8* %.6.i57, i64 %loop.index.4127.us.prol
  store i8 %.74.i.us.prol, i8* %scevgep75, align 1
  %.701.us.prol = add nuw nsw i64 %loop.index.4127.us.prol, 1
  %lsr.iv.next78 = add nsw i64 %lsr.iv77, 1
  %prol.iter502.cmp = icmp eq i64 %lsr.iv.next78, 0
  br i1 %prol.iter502.cmp, label %for.body.4.endif.endif.endif.us.prol.loopexit, label %for.body.4.endif.endif.endif.us.prol, !llvm.loop !36

for.body.4.endif.endif.endif.us.prol.loopexit:    ; preds = %for.body.4.endif.endif.endif.us.prol, %for.body.4.endif.endif.endif.us.preheader482
  %loop.index.4127.us.unr = phi i64 [ %loop.index.4127.us.ph, %for.body.4.endif.endif.endif.us.preheader482 ], [ %.701.us.prol, %for.body.4.endif.endif.endif.us.prol ]
  %277 = icmp ult i64 %275, 3
  br i1 %277, label %for.end.4, label %for.body.4.endif.endif.endif.us.preheader482.new

for.body.4.endif.endif.endif.us.preheader482.new: ; preds = %for.body.4.endif.endif.endif.us.prol.loopexit
  %278 = sub i64 %.618181, %loop.index.4127.us.unr
  %279 = add i64 %loop.index.4127.us.unr, 3
  %scevgep63 = getelementptr i8, i8* %.6.i57, i64 %279
  %scevgep69 = getelementptr double, double* %.6.i26117, i64 %279
  br label %for.body.4.endif.endif.endif.us

min.iters.checked373:                             ; preds = %for.body.4.endif.endif.endif.us.preheader
  %n.vec375 = and i64 %.618181, -16
  %cmp.zero376 = icmp eq i64 %n.vec375, 0
  br i1 %cmp.zero376, label %for.body.4.endif.endif.endif.us.preheader482, label %vector.memcheck391

vector.memcheck391:                               ; preds = %min.iters.checked373
  %280 = bitcast double* %.6.i113119 to i8*
  %281 = bitcast double* %.6.i26117 to i8*
  %scevgep378 = getelementptr i8, i8* %.6.i57, i64 %.618181
  %scevgep379 = getelementptr double, double* %.6.i26117, i64 %.618181
  %scevgep379380 = bitcast double* %scevgep379 to i8*
  %bound0381 = icmp ult i8* %.6.i57, %scevgep379380
  %bound1382 = icmp ult i8* %281, %scevgep378
  %found.conflict383 = and i1 %bound0381, %bound1382
  %bc384 = bitcast double* %.6.i113119 to i8*
  %bound0386 = icmp ult i8* %.6.i57, %280
  %bound1387 = icmp ult i8* %bc384, %scevgep378
  %found.conflict388 = and i1 %bound0386, %bound1387
  %conflict.rdx389 = or i1 %found.conflict383, %found.conflict388
  br i1 %conflict.rdx389, label %for.body.4.endif.endif.endif.us.preheader482, label %vector.body369.preheader

vector.body369.preheader:                         ; preds = %vector.memcheck391
  %282 = load double, double* %.6.i113119, align 8, !alias.scope !37
  %283 = insertelement <4 x double> undef, double %282, i32 0
  %284 = insertelement <4 x double> undef, double %282, i32 0
  %285 = insertelement <4 x double> undef, double %282, i32 0
  %286 = insertelement <4 x double> undef, double %282, i32 0
  %287 = fmul <4 x double> %283, %283
  %288 = shufflevector <4 x double> %287, <4 x double> undef, <4 x i32> zeroinitializer
  %289 = fmul <4 x double> %284, %284
  %290 = shufflevector <4 x double> %289, <4 x double> undef, <4 x i32> zeroinitializer
  %291 = fmul <4 x double> %285, %285
  %292 = shufflevector <4 x double> %291, <4 x double> undef, <4 x i32> zeroinitializer
  %293 = fmul <4 x double> %286, %286
  %294 = shufflevector <4 x double> %293, <4 x double> undef, <4 x i32> zeroinitializer
  %scevgep81 = getelementptr i8, i8* %.6.i57, i64 12
  %scevgep88 = getelementptr double, double* %.6.i26117, i64 12
  br label %vector.body369

vector.body369:                                   ; preds = %vector.body369, %vector.body369.preheader
  %lsr.iv89 = phi double* [ %scevgep90, %vector.body369 ], [ %scevgep88, %vector.body369.preheader ]
  %lsr.iv82 = phi i8* [ %scevgep83, %vector.body369 ], [ %scevgep81, %vector.body369.preheader ]
  %lsr.iv79 = phi i64 [ %lsr.iv.next80, %vector.body369 ], [ %n.vec375, %vector.body369.preheader ]
  %lsr.iv8991 = bitcast double* %lsr.iv89 to <4 x double>*
  %lsr.iv8284 = bitcast i8* %lsr.iv82 to <4 x i8>*
  %scevgep94 = getelementptr <4 x double>, <4 x double>* %lsr.iv8991, i64 -3
  %wide.load403 = load <4 x double>, <4 x double>* %scevgep94, align 8, !alias.scope !40
  %scevgep93 = getelementptr <4 x double>, <4 x double>* %lsr.iv8991, i64 -2
  %wide.load404 = load <4 x double>, <4 x double>* %scevgep93, align 8, !alias.scope !40
  %scevgep92 = getelementptr <4 x double>, <4 x double>* %lsr.iv8991, i64 -1
  %wide.load405 = load <4 x double>, <4 x double>* %scevgep92, align 8, !alias.scope !40
  %wide.load406 = load <4 x double>, <4 x double>* %lsr.iv8991, align 8, !alias.scope !40
  %295 = fmul <4 x double> %wide.load403, %wide.load403
  %296 = fmul <4 x double> %wide.load404, %wide.load404
  %297 = fmul <4 x double> %wide.load405, %wide.load405
  %298 = fmul <4 x double> %wide.load406, %wide.load406
  %299 = fadd <4 x double> %295, %288
  %300 = fadd <4 x double> %296, %290
  %301 = fadd <4 x double> %297, %292
  %302 = fadd <4 x double> %298, %294
  %303 = fcmp olt <4 x double> %299, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %304 = fcmp olt <4 x double> %300, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %305 = fcmp olt <4 x double> %301, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %306 = fcmp olt <4 x double> %302, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %307 = zext <4 x i1> %303 to <4 x i8>
  %308 = zext <4 x i1> %304 to <4 x i8>
  %309 = zext <4 x i1> %305 to <4 x i8>
  %310 = zext <4 x i1> %306 to <4 x i8>
  %scevgep87 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv8284, i64 -3
  store <4 x i8> %307, <4 x i8>* %scevgep87, align 1, !alias.scope !42, !noalias !44
  %scevgep86 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv8284, i64 -2
  store <4 x i8> %308, <4 x i8>* %scevgep86, align 1, !alias.scope !42, !noalias !44
  %scevgep85 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv8284, i64 -1
  store <4 x i8> %309, <4 x i8>* %scevgep85, align 1, !alias.scope !42, !noalias !44
  store <4 x i8> %310, <4 x i8>* %lsr.iv8284, align 1, !alias.scope !42, !noalias !44
  %lsr.iv.next80 = add i64 %lsr.iv79, -16
  %scevgep83 = getelementptr i8, i8* %lsr.iv82, i64 16
  %scevgep90 = getelementptr double, double* %lsr.iv89, i64 16
  %311 = icmp eq i64 %lsr.iv.next80, 0
  br i1 %311, label %middle.block370, label %vector.body369, !llvm.loop !45

middle.block370:                                  ; preds = %vector.body369
  %cmp.n396 = icmp eq i64 %.618181, %n.vec375
  br i1 %cmp.n396, label %for.end.4, label %for.body.4.endif.endif.endif.us.preheader482

for.body.4.endif.endif.endif.us.us.preheader:     ; preds = %for.body.4.endif.endif.endif.lr.ph.split.us
  %312 = icmp ult i64 %.618181, 16
  br i1 %312, label %for.body.4.endif.endif.endif.us.us.preheader481, label %min.iters.checked411

min.iters.checked411:                             ; preds = %for.body.4.endif.endif.endif.us.us.preheader
  %n.vec413 = and i64 %.618181, -16
  %cmp.zero414 = icmp eq i64 %n.vec413, 0
  br i1 %cmp.zero414, label %for.body.4.endif.endif.endif.us.us.preheader481, label %vector.memcheck429

vector.memcheck429:                               ; preds = %min.iters.checked411
  %313 = bitcast double* %.6.i113119 to i8*
  %314 = bitcast double* %.6.i26117 to i8*
  %scevgep416 = getelementptr i8, i8* %.6.i57, i64 %.618181
  %scevgep417 = getelementptr double, double* %.6.i26117, i64 %.618181
  %scevgep417418 = bitcast double* %scevgep417 to i8*
  %scevgep419 = getelementptr double, double* %.6.i113119, i64 %.618181
  %scevgep419420 = bitcast double* %scevgep419 to i8*
  %bound0421 = icmp ult i8* %.6.i57, %scevgep417418
  %bound1422 = icmp ult i8* %314, %scevgep416
  %found.conflict423 = and i1 %bound0421, %bound1422
  %bound0424 = icmp ult i8* %.6.i57, %scevgep419420
  %bound1425 = icmp ult i8* %313, %scevgep416
  %found.conflict426 = and i1 %bound0424, %bound1425
  %conflict.rdx427 = or i1 %found.conflict423, %found.conflict426
  br i1 %conflict.rdx427, label %for.body.4.endif.endif.endif.us.us.preheader481, label %vector.body407.preheader

vector.body407.preheader:                         ; preds = %vector.memcheck429
  %scevgep40 = getelementptr i8, i8* %.6.i57, i64 12
  %scevgep47 = getelementptr double, double* %.6.i113119, i64 12
  %scevgep54 = getelementptr double, double* %.6.i26117, i64 12
  br label %vector.body407

vector.body407:                                   ; preds = %vector.body407, %vector.body407.preheader
  %lsr.iv55 = phi double* [ %scevgep56, %vector.body407 ], [ %scevgep54, %vector.body407.preheader ]
  %lsr.iv48 = phi double* [ %scevgep49, %vector.body407 ], [ %scevgep47, %vector.body407.preheader ]
  %lsr.iv41 = phi i8* [ %scevgep42, %vector.body407 ], [ %scevgep40, %vector.body407.preheader ]
  %lsr.iv38 = phi i64 [ %lsr.iv.next39, %vector.body407 ], [ %n.vec413, %vector.body407.preheader ]
  %lsr.iv5557 = bitcast double* %lsr.iv55 to <4 x double>*
  %lsr.iv4850 = bitcast double* %lsr.iv48 to <4 x double>*
  %lsr.iv4143 = bitcast i8* %lsr.iv41 to <4 x i8>*
  %scevgep60 = getelementptr <4 x double>, <4 x double>* %lsr.iv5557, i64 -3
  %wide.load441 = load <4 x double>, <4 x double>* %scevgep60, align 8, !alias.scope !46
  %scevgep59 = getelementptr <4 x double>, <4 x double>* %lsr.iv5557, i64 -2
  %wide.load442 = load <4 x double>, <4 x double>* %scevgep59, align 8, !alias.scope !46
  %scevgep58 = getelementptr <4 x double>, <4 x double>* %lsr.iv5557, i64 -1
  %wide.load443 = load <4 x double>, <4 x double>* %scevgep58, align 8, !alias.scope !46
  %wide.load444 = load <4 x double>, <4 x double>* %lsr.iv5557, align 8, !alias.scope !46
  %scevgep53 = getelementptr <4 x double>, <4 x double>* %lsr.iv4850, i64 -3
  %wide.load445 = load <4 x double>, <4 x double>* %scevgep53, align 8, !alias.scope !49
  %scevgep52 = getelementptr <4 x double>, <4 x double>* %lsr.iv4850, i64 -2
  %wide.load446 = load <4 x double>, <4 x double>* %scevgep52, align 8, !alias.scope !49
  %scevgep51 = getelementptr <4 x double>, <4 x double>* %lsr.iv4850, i64 -1
  %wide.load447 = load <4 x double>, <4 x double>* %scevgep51, align 8, !alias.scope !49
  %wide.load448 = load <4 x double>, <4 x double>* %lsr.iv4850, align 8, !alias.scope !49
  %315 = fmul <4 x double> %wide.load441, %wide.load441
  %316 = fmul <4 x double> %wide.load442, %wide.load442
  %317 = fmul <4 x double> %wide.load443, %wide.load443
  %318 = fmul <4 x double> %wide.load444, %wide.load444
  %319 = fmul <4 x double> %wide.load445, %wide.load445
  %320 = fmul <4 x double> %wide.load446, %wide.load446
  %321 = fmul <4 x double> %wide.load447, %wide.load447
  %322 = fmul <4 x double> %wide.load448, %wide.load448
  %323 = fadd <4 x double> %315, %319
  %324 = fadd <4 x double> %316, %320
  %325 = fadd <4 x double> %317, %321
  %326 = fadd <4 x double> %318, %322
  %327 = fcmp olt <4 x double> %323, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %328 = fcmp olt <4 x double> %324, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %329 = fcmp olt <4 x double> %325, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %330 = fcmp olt <4 x double> %326, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %331 = zext <4 x i1> %327 to <4 x i8>
  %332 = zext <4 x i1> %328 to <4 x i8>
  %333 = zext <4 x i1> %329 to <4 x i8>
  %334 = zext <4 x i1> %330 to <4 x i8>
  %scevgep46 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv4143, i64 -3
  store <4 x i8> %331, <4 x i8>* %scevgep46, align 1, !alias.scope !51, !noalias !53
  %scevgep45 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv4143, i64 -2
  store <4 x i8> %332, <4 x i8>* %scevgep45, align 1, !alias.scope !51, !noalias !53
  %scevgep44 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv4143, i64 -1
  store <4 x i8> %333, <4 x i8>* %scevgep44, align 1, !alias.scope !51, !noalias !53
  store <4 x i8> %334, <4 x i8>* %lsr.iv4143, align 1, !alias.scope !51, !noalias !53
  %lsr.iv.next39 = add i64 %lsr.iv38, -16
  %scevgep42 = getelementptr i8, i8* %lsr.iv41, i64 16
  %scevgep49 = getelementptr double, double* %lsr.iv48, i64 16
  %scevgep56 = getelementptr double, double* %lsr.iv55, i64 16
  %335 = icmp eq i64 %lsr.iv.next39, 0
  br i1 %335, label %middle.block408, label %vector.body407, !llvm.loop !54

middle.block408:                                  ; preds = %vector.body407
  %cmp.n434 = icmp eq i64 %.618181, %n.vec413
  br i1 %cmp.n434, label %for.end.4, label %for.body.4.endif.endif.endif.us.us.preheader481

for.body.4.endif.endif.endif.us.us.preheader481:  ; preds = %middle.block408, %vector.memcheck429, %min.iters.checked411, %for.body.4.endif.endif.endif.us.us.preheader
  %loop.index.4127.us.us.ph = phi i64 [ 0, %vector.memcheck429 ], [ 0, %min.iters.checked411 ], [ 0, %for.body.4.endif.endif.endif.us.us.preheader ], [ %n.vec413, %middle.block408 ]
  %336 = sub nsw i64 %.618181, %loop.index.4127.us.us.ph
  %337 = add nsw i64 %.618181, -1
  %338 = sub nsw i64 %337, %loop.index.4127.us.us.ph
  %xtraiter498 = and i64 %336, 3
  %lcmp.mod499 = icmp eq i64 %xtraiter498, 0
  br i1 %lcmp.mod499, label %for.body.4.endif.endif.endif.us.us.prol.loopexit, label %for.body.4.endif.endif.endif.us.us.prol.preheader

for.body.4.endif.endif.endif.us.us.prol.preheader: ; preds = %for.body.4.endif.endif.endif.us.us.preheader481
  %339 = sub i64 0, %xtraiter498
  br label %for.body.4.endif.endif.endif.us.us.prol

for.body.4.endif.endif.endif.us.us.prol:          ; preds = %for.body.4.endif.endif.endif.us.us.prol, %for.body.4.endif.endif.endif.us.us.prol.preheader
  %lsr.iv36 = phi i64 [ %lsr.iv.next37, %for.body.4.endif.endif.endif.us.us.prol ], [ %339, %for.body.4.endif.endif.endif.us.us.prol.preheader ]
  %loop.index.4127.us.us.prol = phi i64 [ %.701.us.us.prol, %for.body.4.endif.endif.endif.us.us.prol ], [ %loop.index.4127.us.us.ph, %for.body.4.endif.endif.endif.us.us.prol.preheader ]
  %scevgep35 = getelementptr double, double* %.6.i26117, i64 %loop.index.4127.us.us.prol
  %.669.us.us.prol = load double, double* %scevgep35, align 8
  %scevgep34 = getelementptr double, double* %.6.i113119, i64 %loop.index.4127.us.us.prol
  %.678.us.us.prol = load double, double* %scevgep34, align 8
  %.18.i.us.us.prol = fmul double %.669.us.us.prol, %.669.us.us.prol
  %.33.i.us.us.prol = fmul double %.678.us.us.prol, %.678.us.us.prol
  %.45.i.us.us.prol = fadd double %.18.i.us.us.prol, %.33.i.us.us.prol
  %.59.i.us.us.prol = fcmp olt double %.45.i.us.us.prol, 1.000000e+00
  %.74.i.us.us.prol = zext i1 %.59.i.us.us.prol to i8
  %scevgep33 = getelementptr i8, i8* %.6.i57, i64 %loop.index.4127.us.us.prol
  store i8 %.74.i.us.us.prol, i8* %scevgep33, align 1
  %.701.us.us.prol = add nuw nsw i64 %loop.index.4127.us.us.prol, 1
  %lsr.iv.next37 = add nsw i64 %lsr.iv36, 1
  %prol.iter.cmp = icmp eq i64 %lsr.iv.next37, 0
  br i1 %prol.iter.cmp, label %for.body.4.endif.endif.endif.us.us.prol.loopexit, label %for.body.4.endif.endif.endif.us.us.prol, !llvm.loop !55

for.body.4.endif.endif.endif.us.us.prol.loopexit: ; preds = %for.body.4.endif.endif.endif.us.us.prol, %for.body.4.endif.endif.endif.us.us.preheader481
  %loop.index.4127.us.us.unr = phi i64 [ %loop.index.4127.us.us.ph, %for.body.4.endif.endif.endif.us.us.preheader481 ], [ %.701.us.us.prol, %for.body.4.endif.endif.endif.us.us.prol ]
  %340 = icmp ult i64 %338, 3
  br i1 %340, label %for.end.4, label %for.body.4.endif.endif.endif.us.us.preheader481.new

for.body.4.endif.endif.endif.us.us.preheader481.new: ; preds = %for.body.4.endif.endif.endif.us.us.prol.loopexit
  %341 = sub i64 %.618181, %loop.index.4127.us.us.unr
  %342 = add i64 %loop.index.4127.us.us.unr, 3
  %scevgep15 = getelementptr i8, i8* %.6.i57, i64 %342
  %scevgep21 = getelementptr double, double* %.6.i113119, i64 %342
  %scevgep27 = getelementptr double, double* %.6.i26117, i64 %342
  br label %for.body.4.endif.endif.endif.us.us

for.body.4.endif.endif.endif.us.us:               ; preds = %for.body.4.endif.endif.endif.us.us, %for.body.4.endif.endif.endif.us.us.preheader481.new
  %lsr.iv28 = phi double* [ %scevgep29, %for.body.4.endif.endif.endif.us.us ], [ %scevgep27, %for.body.4.endif.endif.endif.us.us.preheader481.new ]
  %lsr.iv22 = phi double* [ %scevgep23, %for.body.4.endif.endif.endif.us.us ], [ %scevgep21, %for.body.4.endif.endif.endif.us.us.preheader481.new ]
  %lsr.iv16 = phi i8* [ %scevgep17, %for.body.4.endif.endif.endif.us.us ], [ %scevgep15, %for.body.4.endif.endif.endif.us.us.preheader481.new ]
  %lsr.iv13 = phi i64 [ %lsr.iv.next14, %for.body.4.endif.endif.endif.us.us ], [ %341, %for.body.4.endif.endif.endif.us.us.preheader481.new ]
  %scevgep32 = getelementptr double, double* %lsr.iv28, i64 -3
  %.669.us.us = load double, double* %scevgep32, align 8
  %scevgep26 = getelementptr double, double* %lsr.iv22, i64 -3
  %.678.us.us = load double, double* %scevgep26, align 8
  %.18.i.us.us = fmul double %.669.us.us, %.669.us.us
  %.33.i.us.us = fmul double %.678.us.us, %.678.us.us
  %.45.i.us.us = fadd double %.18.i.us.us, %.33.i.us.us
  %.59.i.us.us = fcmp olt double %.45.i.us.us, 1.000000e+00
  %.74.i.us.us = zext i1 %.59.i.us.us to i8
  %scevgep20 = getelementptr i8, i8* %lsr.iv16, i64 -3
  store i8 %.74.i.us.us, i8* %scevgep20, align 1
  %scevgep31 = getelementptr double, double* %lsr.iv28, i64 -2
  %.669.us.us.1 = load double, double* %scevgep31, align 8
  %scevgep25 = getelementptr double, double* %lsr.iv22, i64 -2
  %.678.us.us.1 = load double, double* %scevgep25, align 8
  %.18.i.us.us.1 = fmul double %.669.us.us.1, %.669.us.us.1
  %.33.i.us.us.1 = fmul double %.678.us.us.1, %.678.us.us.1
  %.45.i.us.us.1 = fadd double %.18.i.us.us.1, %.33.i.us.us.1
  %.59.i.us.us.1 = fcmp olt double %.45.i.us.us.1, 1.000000e+00
  %.74.i.us.us.1 = zext i1 %.59.i.us.us.1 to i8
  %scevgep19 = getelementptr i8, i8* %lsr.iv16, i64 -2
  store i8 %.74.i.us.us.1, i8* %scevgep19, align 1
  %scevgep30 = getelementptr double, double* %lsr.iv28, i64 -1
  %.669.us.us.2 = load double, double* %scevgep30, align 8
  %scevgep24 = getelementptr double, double* %lsr.iv22, i64 -1
  %.678.us.us.2 = load double, double* %scevgep24, align 8
  %.18.i.us.us.2 = fmul double %.669.us.us.2, %.669.us.us.2
  %.33.i.us.us.2 = fmul double %.678.us.us.2, %.678.us.us.2
  %.45.i.us.us.2 = fadd double %.18.i.us.us.2, %.33.i.us.us.2
  %.59.i.us.us.2 = fcmp olt double %.45.i.us.us.2, 1.000000e+00
  %.74.i.us.us.2 = zext i1 %.59.i.us.us.2 to i8
  %scevgep18 = getelementptr i8, i8* %lsr.iv16, i64 -1
  store i8 %.74.i.us.us.2, i8* %scevgep18, align 1
  %.669.us.us.3 = load double, double* %lsr.iv28, align 8
  %.678.us.us.3 = load double, double* %lsr.iv22, align 8
  %.18.i.us.us.3 = fmul double %.669.us.us.3, %.669.us.us.3
  %.33.i.us.us.3 = fmul double %.678.us.us.3, %.678.us.us.3
  %.45.i.us.us.3 = fadd double %.18.i.us.us.3, %.33.i.us.us.3
  %.59.i.us.us.3 = fcmp olt double %.45.i.us.us.3, 1.000000e+00
  %.74.i.us.us.3 = zext i1 %.59.i.us.us.3 to i8
  store i8 %.74.i.us.us.3, i8* %lsr.iv16, align 1
  %lsr.iv.next14 = add i64 %lsr.iv13, -4
  %scevgep17 = getelementptr i8, i8* %lsr.iv16, i64 4
  %scevgep23 = getelementptr double, double* %lsr.iv22, i64 4
  %scevgep29 = getelementptr double, double* %lsr.iv28, i64 4
  %exitcond.3 = icmp eq i64 %lsr.iv.next14, 0
  br i1 %exitcond.3, label %for.end.4, label %for.body.4.endif.endif.endif.us.us, !llvm.loop !56

for.body.4.endif.endif.endif.us:                  ; preds = %for.body.4.endif.endif.endif.us, %for.body.4.endif.endif.endif.us.preheader482.new
  %lsr.iv70 = phi double* [ %scevgep71, %for.body.4.endif.endif.endif.us ], [ %scevgep69, %for.body.4.endif.endif.endif.us.preheader482.new ]
  %lsr.iv64 = phi i8* [ %scevgep65, %for.body.4.endif.endif.endif.us ], [ %scevgep63, %for.body.4.endif.endif.endif.us.preheader482.new ]
  %lsr.iv61 = phi i64 [ %lsr.iv.next62, %for.body.4.endif.endif.endif.us ], [ %278, %for.body.4.endif.endif.endif.us.preheader482.new ]
  %scevgep74 = getelementptr double, double* %lsr.iv70, i64 -3
  %.669.us = load double, double* %scevgep74, align 8
  %.678.us = load double, double* %.6.i113119, align 8
  %.18.i.us = fmul double %.669.us, %.669.us
  %.33.i.us = fmul double %.678.us, %.678.us
  %.45.i.us = fadd double %.18.i.us, %.33.i.us
  %.59.i.us = fcmp olt double %.45.i.us, 1.000000e+00
  %.74.i.us = zext i1 %.59.i.us to i8
  %scevgep68 = getelementptr i8, i8* %lsr.iv64, i64 -3
  store i8 %.74.i.us, i8* %scevgep68, align 1
  %scevgep73 = getelementptr double, double* %lsr.iv70, i64 -2
  %.669.us.1 = load double, double* %scevgep73, align 8
  %.678.us.1 = load double, double* %.6.i113119, align 8
  %.18.i.us.1 = fmul double %.669.us.1, %.669.us.1
  %.33.i.us.1 = fmul double %.678.us.1, %.678.us.1
  %.45.i.us.1 = fadd double %.18.i.us.1, %.33.i.us.1
  %.59.i.us.1 = fcmp olt double %.45.i.us.1, 1.000000e+00
  %.74.i.us.1 = zext i1 %.59.i.us.1 to i8
  %scevgep67 = getelementptr i8, i8* %lsr.iv64, i64 -2
  store i8 %.74.i.us.1, i8* %scevgep67, align 1
  %scevgep72 = getelementptr double, double* %lsr.iv70, i64 -1
  %.669.us.2 = load double, double* %scevgep72, align 8
  %.678.us.2 = load double, double* %.6.i113119, align 8
  %.18.i.us.2 = fmul double %.669.us.2, %.669.us.2
  %.33.i.us.2 = fmul double %.678.us.2, %.678.us.2
  %.45.i.us.2 = fadd double %.18.i.us.2, %.33.i.us.2
  %.59.i.us.2 = fcmp olt double %.45.i.us.2, 1.000000e+00
  %.74.i.us.2 = zext i1 %.59.i.us.2 to i8
  %scevgep66 = getelementptr i8, i8* %lsr.iv64, i64 -1
  store i8 %.74.i.us.2, i8* %scevgep66, align 1
  %.669.us.3 = load double, double* %lsr.iv70, align 8
  %.678.us.3 = load double, double* %.6.i113119, align 8
  %.18.i.us.3 = fmul double %.669.us.3, %.669.us.3
  %.33.i.us.3 = fmul double %.678.us.3, %.678.us.3
  %.45.i.us.3 = fadd double %.18.i.us.3, %.33.i.us.3
  %.59.i.us.3 = fcmp olt double %.45.i.us.3, 1.000000e+00
  %.74.i.us.3 = zext i1 %.59.i.us.3 to i8
  store i8 %.74.i.us.3, i8* %lsr.iv64, align 1
  %lsr.iv.next62 = add i64 %lsr.iv61, -4
  %scevgep65 = getelementptr i8, i8* %lsr.iv64, i64 4
  %scevgep71 = getelementptr double, double* %lsr.iv70, i64 4
  %exitcond162.3 = icmp eq i64 %lsr.iv.next62, 0
  br i1 %exitcond162.3, label %for.end.4, label %for.body.4.endif.endif.endif.us, !llvm.loop !57

for.body.4.endif.endif.endif.lr.ph.split:         ; preds = %for.body.4.endif.endif.endif.lr.ph
  %343 = icmp ugt i64 %.428177, 1
  br i1 %343, label %for.body.4.endif.endif.endif.us128.preheader, label %for.body.4.endif.endif.endif.preheader

for.body.4.endif.endif.endif.preheader:           ; preds = %for.body.4.endif.endif.endif.lr.ph.split
  %344 = icmp ult i64 %.618181, 16
  br i1 %344, label %for.body.4.endif.endif.endif.preheader486, label %min.iters.checked302

for.body.4.endif.endif.endif.preheader486:        ; preds = %middle.block299, %vector.memcheck319, %min.iters.checked302, %for.body.4.endif.endif.endif.preheader
  %loop.index.4127.ph = phi i64 [ 0, %vector.memcheck319 ], [ 0, %min.iters.checked302 ], [ 0, %for.body.4.endif.endif.endif.preheader ], [ %n.vec304, %middle.block299 ]
  %345 = sub nsw i64 %.618181, %loop.index.4127.ph
  %346 = add nsw i64 %.618181, -1
  %347 = sub nsw i64 %346, %loop.index.4127.ph
  %xtraiter506 = and i64 %345, 3
  %lcmp.mod507 = icmp eq i64 %xtraiter506, 0
  br i1 %lcmp.mod507, label %for.body.4.endif.endif.endif.prol.loopexit, label %for.body.4.endif.endif.endif.prol.preheader

for.body.4.endif.endif.endif.prol.preheader:      ; preds = %for.body.4.endif.endif.endif.preheader486
  %348 = sub i64 0, %xtraiter506
  br label %for.body.4.endif.endif.endif.prol

for.body.4.endif.endif.endif.prol:                ; preds = %for.body.4.endif.endif.endif.prol, %for.body.4.endif.endif.endif.prol.preheader
  %lsr.iv138 = phi i64 [ %lsr.iv.next139, %for.body.4.endif.endif.endif.prol ], [ %348, %for.body.4.endif.endif.endif.prol.preheader ]
  %loop.index.4127.prol = phi i64 [ %.701.prol, %for.body.4.endif.endif.endif.prol ], [ %loop.index.4127.ph, %for.body.4.endif.endif.endif.prol.preheader ]
  %.669.prol = load double, double* %.6.i26117, align 8
  %.678.prol = load double, double* %.6.i113119, align 8
  %.18.i.prol = fmul double %.669.prol, %.669.prol
  %.33.i.prol = fmul double %.678.prol, %.678.prol
  %.45.i.prol = fadd double %.18.i.prol, %.33.i.prol
  %.59.i.prol = fcmp olt double %.45.i.prol, 1.000000e+00
  %.74.i.prol = zext i1 %.59.i.prol to i8
  %scevgep137 = getelementptr i8, i8* %.6.i57, i64 %loop.index.4127.prol
  store i8 %.74.i.prol, i8* %scevgep137, align 1
  %.701.prol = add nuw nsw i64 %loop.index.4127.prol, 1
  %lsr.iv.next139 = add nsw i64 %lsr.iv138, 1
  %prol.iter508.cmp = icmp eq i64 %lsr.iv.next139, 0
  br i1 %prol.iter508.cmp, label %for.body.4.endif.endif.endif.prol.loopexit, label %for.body.4.endif.endif.endif.prol, !llvm.loop !58

for.body.4.endif.endif.endif.prol.loopexit:       ; preds = %for.body.4.endif.endif.endif.prol, %for.body.4.endif.endif.endif.preheader486
  %loop.index.4127.unr = phi i64 [ %loop.index.4127.ph, %for.body.4.endif.endif.endif.preheader486 ], [ %.701.prol, %for.body.4.endif.endif.endif.prol ]
  %349 = icmp ult i64 %347, 3
  br i1 %349, label %for.end.4, label %for.body.4.endif.endif.endif.preheader486.new

for.body.4.endif.endif.endif.preheader486.new:    ; preds = %for.body.4.endif.endif.endif.prol.loopexit
  %350 = sub i64 %.618181, %loop.index.4127.unr
  %351 = add i64 %loop.index.4127.unr, 3
  %scevgep131 = getelementptr i8, i8* %.6.i57, i64 %351
  br label %for.body.4.endif.endif.endif

min.iters.checked302:                             ; preds = %for.body.4.endif.endif.endif.preheader
  %n.vec304 = and i64 %.618181, -16
  %cmp.zero305 = icmp eq i64 %n.vec304, 0
  br i1 %cmp.zero305, label %for.body.4.endif.endif.endif.preheader486, label %vector.memcheck319

vector.memcheck319:                               ; preds = %min.iters.checked302
  %352 = bitcast double* %.6.i113119 to i8*
  %scevgep307 = getelementptr i8, i8* %.6.i57, i64 %.618181
  %bc308 = bitcast double* %.6.i26117 to i8*
  %bc309 = bitcast double* %.6.i26117 to i8*
  %bound0310 = icmp ult i8* %.6.i57, %bc309
  %bound1311 = icmp ult i8* %bc308, %scevgep307
  %found.conflict312 = and i1 %bound0310, %bound1311
  %bc313 = bitcast double* %.6.i113119 to i8*
  %bound0315 = icmp ult i8* %.6.i57, %352
  %bound1316 = icmp ult i8* %bc313, %scevgep307
  %found.conflict317 = and i1 %bound0315, %bound1316
  %conflict.rdx = or i1 %found.conflict312, %found.conflict317
  br i1 %conflict.rdx, label %for.body.4.endif.endif.endif.preheader486, label %vector.body298.preheader

vector.body298.preheader:                         ; preds = %vector.memcheck319
  %353 = load double, double* %.6.i26117, align 8, !alias.scope !59
  %354 = insertelement <4 x double> undef, double %353, i32 0
  %355 = insertelement <4 x double> undef, double %353, i32 0
  %356 = insertelement <4 x double> undef, double %353, i32 0
  %357 = insertelement <4 x double> undef, double %353, i32 0
  %358 = load double, double* %.6.i113119, align 8, !alias.scope !62
  %359 = insertelement <4 x double> undef, double %358, i32 0
  %360 = insertelement <4 x double> undef, double %358, i32 0
  %361 = insertelement <4 x double> undef, double %358, i32 0
  %362 = insertelement <4 x double> undef, double %358, i32 0
  %363 = fmul <4 x double> %354, %354
  %364 = fmul <4 x double> %355, %355
  %365 = fmul <4 x double> %356, %356
  %366 = fmul <4 x double> %357, %357
  %367 = fmul <4 x double> %359, %359
  %368 = fmul <4 x double> %360, %360
  %369 = fmul <4 x double> %361, %361
  %370 = fmul <4 x double> %362, %362
  %371 = fadd <4 x double> %363, %367
  %372 = shufflevector <4 x double> %371, <4 x double> undef, <4 x i32> zeroinitializer
  %373 = fadd <4 x double> %364, %368
  %374 = shufflevector <4 x double> %373, <4 x double> undef, <4 x i32> zeroinitializer
  %375 = fadd <4 x double> %365, %369
  %376 = shufflevector <4 x double> %375, <4 x double> undef, <4 x i32> zeroinitializer
  %377 = fadd <4 x double> %366, %370
  %378 = shufflevector <4 x double> %377, <4 x double> undef, <4 x i32> zeroinitializer
  %379 = fcmp olt <4 x double> %372, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %380 = fcmp olt <4 x double> %374, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %381 = fcmp olt <4 x double> %376, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %382 = fcmp olt <4 x double> %378, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %383 = zext <4 x i1> %379 to <4 x i8>
  %384 = zext <4 x i1> %380 to <4 x i8>
  %385 = zext <4 x i1> %381 to <4 x i8>
  %386 = zext <4 x i1> %382 to <4 x i8>
  %scevgep142 = getelementptr i8, i8* %.6.i57, i64 12
  br label %vector.body298

vector.body298:                                   ; preds = %vector.body298, %vector.body298.preheader
  %lsr.iv143 = phi i8* [ %scevgep144, %vector.body298 ], [ %scevgep142, %vector.body298.preheader ]
  %lsr.iv140 = phi i64 [ %lsr.iv.next141, %vector.body298 ], [ %n.vec304, %vector.body298.preheader ]
  %lsr.iv143145 = bitcast i8* %lsr.iv143 to <4 x i8>*
  %scevgep148 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv143145, i64 -3
  store <4 x i8> %383, <4 x i8>* %scevgep148, align 1, !alias.scope !64, !noalias !66
  %scevgep147 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv143145, i64 -2
  store <4 x i8> %384, <4 x i8>* %scevgep147, align 1, !alias.scope !64, !noalias !66
  %scevgep146 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv143145, i64 -1
  store <4 x i8> %385, <4 x i8>* %scevgep146, align 1, !alias.scope !64, !noalias !66
  store <4 x i8> %386, <4 x i8>* %lsr.iv143145, align 1, !alias.scope !64, !noalias !66
  %lsr.iv.next141 = add i64 %lsr.iv140, -16
  %scevgep144 = getelementptr i8, i8* %lsr.iv143, i64 16
  %387 = icmp eq i64 %lsr.iv.next141, 0
  br i1 %387, label %middle.block299, label %vector.body298, !llvm.loop !67

middle.block299:                                  ; preds = %vector.body298
  %cmp.n324 = icmp eq i64 %.618181, %n.vec304
  br i1 %cmp.n324, label %for.end.4, label %for.body.4.endif.endif.endif.preheader486

for.body.4.endif.endif.endif.us128.preheader:     ; preds = %for.body.4.endif.endif.endif.lr.ph.split
  %388 = icmp ult i64 %.618181, 16
  br i1 %388, label %for.body.4.endif.endif.endif.us128.preheader484, label %min.iters.checked335

min.iters.checked335:                             ; preds = %for.body.4.endif.endif.endif.us128.preheader
  %n.vec337 = and i64 %.618181, -16
  %cmp.zero338 = icmp eq i64 %n.vec337, 0
  br i1 %cmp.zero338, label %for.body.4.endif.endif.endif.us128.preheader484, label %vector.memcheck353

vector.memcheck353:                               ; preds = %min.iters.checked335
  %389 = bitcast double* %.6.i113119 to i8*
  %390 = bitcast double* %.6.i26117 to i8*
  %scevgep340 = getelementptr i8, i8* %.6.i57, i64 %.618181
  %scevgep341 = getelementptr double, double* %.6.i113119, i64 %.618181
  %scevgep341342 = bitcast double* %scevgep341 to i8*
  %bc343 = bitcast double* %.6.i26117 to i8*
  %bound0345 = icmp ult i8* %.6.i57, %390
  %bound1346 = icmp ult i8* %bc343, %scevgep340
  %found.conflict347 = and i1 %bound0345, %bound1346
  %bound0348 = icmp ult i8* %.6.i57, %scevgep341342
  %bound1349 = icmp ult i8* %389, %scevgep340
  %found.conflict350 = and i1 %bound0348, %bound1349
  %conflict.rdx351 = or i1 %found.conflict347, %found.conflict350
  br i1 %conflict.rdx351, label %for.body.4.endif.endif.endif.us128.preheader484, label %vector.body331.preheader

vector.body331.preheader:                         ; preds = %vector.memcheck353
  %391 = load double, double* %.6.i26117, align 8, !alias.scope !68
  %392 = insertelement <4 x double> undef, double %391, i32 0
  %393 = insertelement <4 x double> undef, double %391, i32 0
  %394 = insertelement <4 x double> undef, double %391, i32 0
  %395 = insertelement <4 x double> undef, double %391, i32 0
  %396 = fmul <4 x double> %392, %392
  %397 = shufflevector <4 x double> %396, <4 x double> undef, <4 x i32> zeroinitializer
  %398 = fmul <4 x double> %393, %393
  %399 = shufflevector <4 x double> %398, <4 x double> undef, <4 x i32> zeroinitializer
  %400 = fmul <4 x double> %394, %394
  %401 = shufflevector <4 x double> %400, <4 x double> undef, <4 x i32> zeroinitializer
  %402 = fmul <4 x double> %395, %395
  %403 = shufflevector <4 x double> %402, <4 x double> undef, <4 x i32> zeroinitializer
  %scevgep115 = getelementptr i8, i8* %.6.i57, i64 12
  %scevgep122 = getelementptr double, double* %.6.i113119, i64 12
  br label %vector.body331

vector.body331:                                   ; preds = %vector.body331, %vector.body331.preheader
  %lsr.iv123 = phi double* [ %scevgep124, %vector.body331 ], [ %scevgep122, %vector.body331.preheader ]
  %lsr.iv116 = phi i8* [ %scevgep117, %vector.body331 ], [ %scevgep115, %vector.body331.preheader ]
  %lsr.iv113 = phi i64 [ %lsr.iv.next114, %vector.body331 ], [ %n.vec337, %vector.body331.preheader ]
  %lsr.iv123125 = bitcast double* %lsr.iv123 to <4 x double>*
  %lsr.iv116118 = bitcast i8* %lsr.iv116 to <4 x i8>*
  %scevgep128 = getelementptr <4 x double>, <4 x double>* %lsr.iv123125, i64 -3
  %wide.load365 = load <4 x double>, <4 x double>* %scevgep128, align 8, !alias.scope !71
  %scevgep127 = getelementptr <4 x double>, <4 x double>* %lsr.iv123125, i64 -2
  %wide.load366 = load <4 x double>, <4 x double>* %scevgep127, align 8, !alias.scope !71
  %scevgep126 = getelementptr <4 x double>, <4 x double>* %lsr.iv123125, i64 -1
  %wide.load367 = load <4 x double>, <4 x double>* %scevgep126, align 8, !alias.scope !71
  %wide.load368 = load <4 x double>, <4 x double>* %lsr.iv123125, align 8, !alias.scope !71
  %404 = fmul <4 x double> %wide.load365, %wide.load365
  %405 = fmul <4 x double> %wide.load366, %wide.load366
  %406 = fmul <4 x double> %wide.load367, %wide.load367
  %407 = fmul <4 x double> %wide.load368, %wide.load368
  %408 = fadd <4 x double> %397, %404
  %409 = fadd <4 x double> %399, %405
  %410 = fadd <4 x double> %401, %406
  %411 = fadd <4 x double> %403, %407
  %412 = fcmp olt <4 x double> %408, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %413 = fcmp olt <4 x double> %409, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %414 = fcmp olt <4 x double> %410, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %415 = fcmp olt <4 x double> %411, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %416 = zext <4 x i1> %412 to <4 x i8>
  %417 = zext <4 x i1> %413 to <4 x i8>
  %418 = zext <4 x i1> %414 to <4 x i8>
  %419 = zext <4 x i1> %415 to <4 x i8>
  %scevgep121 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv116118, i64 -3
  store <4 x i8> %416, <4 x i8>* %scevgep121, align 1, !alias.scope !73, !noalias !75
  %scevgep120 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv116118, i64 -2
  store <4 x i8> %417, <4 x i8>* %scevgep120, align 1, !alias.scope !73, !noalias !75
  %scevgep119 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv116118, i64 -1
  store <4 x i8> %418, <4 x i8>* %scevgep119, align 1, !alias.scope !73, !noalias !75
  store <4 x i8> %419, <4 x i8>* %lsr.iv116118, align 1, !alias.scope !73, !noalias !75
  %lsr.iv.next114 = add i64 %lsr.iv113, -16
  %scevgep117 = getelementptr i8, i8* %lsr.iv116, i64 16
  %scevgep124 = getelementptr double, double* %lsr.iv123, i64 16
  %420 = icmp eq i64 %lsr.iv.next114, 0
  br i1 %420, label %middle.block332, label %vector.body331, !llvm.loop !76

middle.block332:                                  ; preds = %vector.body331
  %cmp.n358 = icmp eq i64 %.618181, %n.vec337
  br i1 %cmp.n358, label %for.end.4, label %for.body.4.endif.endif.endif.us128.preheader484

for.body.4.endif.endif.endif.us128.preheader484:  ; preds = %middle.block332, %vector.memcheck353, %min.iters.checked335, %for.body.4.endif.endif.endif.us128.preheader
  %loop.index.4127.us129.ph = phi i64 [ 0, %vector.memcheck353 ], [ 0, %min.iters.checked335 ], [ 0, %for.body.4.endif.endif.endif.us128.preheader ], [ %n.vec337, %middle.block332 ]
  %421 = sub nsw i64 %.618181, %loop.index.4127.us129.ph
  %422 = add nsw i64 %.618181, -1
  %423 = sub nsw i64 %422, %loop.index.4127.us129.ph
  %xtraiter503 = and i64 %421, 3
  %lcmp.mod504 = icmp eq i64 %xtraiter503, 0
  br i1 %lcmp.mod504, label %for.body.4.endif.endif.endif.us128.prol.loopexit, label %for.body.4.endif.endif.endif.us128.prol.preheader

for.body.4.endif.endif.endif.us128.prol.preheader: ; preds = %for.body.4.endif.endif.endif.us128.preheader484
  %424 = sub i64 0, %xtraiter503
  br label %for.body.4.endif.endif.endif.us128.prol

for.body.4.endif.endif.endif.us128.prol:          ; preds = %for.body.4.endif.endif.endif.us128.prol, %for.body.4.endif.endif.endif.us128.prol.preheader
  %lsr.iv111 = phi i64 [ %lsr.iv.next112, %for.body.4.endif.endif.endif.us128.prol ], [ %424, %for.body.4.endif.endif.endif.us128.prol.preheader ]
  %loop.index.4127.us129.prol = phi i64 [ %.701.us141.prol, %for.body.4.endif.endif.endif.us128.prol ], [ %loop.index.4127.us129.ph, %for.body.4.endif.endif.endif.us128.prol.preheader ]
  %.669.us131.prol = load double, double* %.6.i26117, align 8
  %scevgep110 = getelementptr double, double* %.6.i113119, i64 %loop.index.4127.us129.prol
  %.678.us134.prol = load double, double* %scevgep110, align 8
  %.18.i.us135.prol = fmul double %.669.us131.prol, %.669.us131.prol
  %.33.i.us136.prol = fmul double %.678.us134.prol, %.678.us134.prol
  %.45.i.us137.prol = fadd double %.18.i.us135.prol, %.33.i.us136.prol
  %.59.i.us138.prol = fcmp olt double %.45.i.us137.prol, 1.000000e+00
  %.74.i.us139.prol = zext i1 %.59.i.us138.prol to i8
  %scevgep109 = getelementptr i8, i8* %.6.i57, i64 %loop.index.4127.us129.prol
  store i8 %.74.i.us139.prol, i8* %scevgep109, align 1
  %.701.us141.prol = add nuw nsw i64 %loop.index.4127.us129.prol, 1
  %lsr.iv.next112 = add nsw i64 %lsr.iv111, 1
  %prol.iter505.cmp = icmp eq i64 %lsr.iv.next112, 0
  br i1 %prol.iter505.cmp, label %for.body.4.endif.endif.endif.us128.prol.loopexit, label %for.body.4.endif.endif.endif.us128.prol, !llvm.loop !77

for.body.4.endif.endif.endif.us128.prol.loopexit: ; preds = %for.body.4.endif.endif.endif.us128.prol, %for.body.4.endif.endif.endif.us128.preheader484
  %loop.index.4127.us129.unr = phi i64 [ %loop.index.4127.us129.ph, %for.body.4.endif.endif.endif.us128.preheader484 ], [ %.701.us141.prol, %for.body.4.endif.endif.endif.us128.prol ]
  %425 = icmp ult i64 %423, 3
  br i1 %425, label %for.end.4, label %for.body.4.endif.endif.endif.us128.preheader484.new

for.body.4.endif.endif.endif.us128.preheader484.new: ; preds = %for.body.4.endif.endif.endif.us128.prol.loopexit
  %426 = sub i64 %.618181, %loop.index.4127.us129.unr
  %427 = add i64 %loop.index.4127.us129.unr, 3
  %scevgep97 = getelementptr i8, i8* %.6.i57, i64 %427
  %scevgep103 = getelementptr double, double* %.6.i113119, i64 %427
  br label %for.body.4.endif.endif.endif.us128

for.body.4.endif.endif.endif.us128:               ; preds = %for.body.4.endif.endif.endif.us128, %for.body.4.endif.endif.endif.us128.preheader484.new
  %lsr.iv104 = phi double* [ %scevgep105, %for.body.4.endif.endif.endif.us128 ], [ %scevgep103, %for.body.4.endif.endif.endif.us128.preheader484.new ]
  %lsr.iv98 = phi i8* [ %scevgep99, %for.body.4.endif.endif.endif.us128 ], [ %scevgep97, %for.body.4.endif.endif.endif.us128.preheader484.new ]
  %lsr.iv95 = phi i64 [ %lsr.iv.next96, %for.body.4.endif.endif.endif.us128 ], [ %426, %for.body.4.endif.endif.endif.us128.preheader484.new ]
  %.669.us131 = load double, double* %.6.i26117, align 8
  %scevgep108 = getelementptr double, double* %lsr.iv104, i64 -3
  %.678.us134 = load double, double* %scevgep108, align 8
  %.18.i.us135 = fmul double %.669.us131, %.669.us131
  %.33.i.us136 = fmul double %.678.us134, %.678.us134
  %.45.i.us137 = fadd double %.18.i.us135, %.33.i.us136
  %.59.i.us138 = fcmp olt double %.45.i.us137, 1.000000e+00
  %.74.i.us139 = zext i1 %.59.i.us138 to i8
  %scevgep102 = getelementptr i8, i8* %lsr.iv98, i64 -3
  store i8 %.74.i.us139, i8* %scevgep102, align 1
  %.669.us131.1 = load double, double* %.6.i26117, align 8
  %scevgep107 = getelementptr double, double* %lsr.iv104, i64 -2
  %.678.us134.1 = load double, double* %scevgep107, align 8
  %.18.i.us135.1 = fmul double %.669.us131.1, %.669.us131.1
  %.33.i.us136.1 = fmul double %.678.us134.1, %.678.us134.1
  %.45.i.us137.1 = fadd double %.18.i.us135.1, %.33.i.us136.1
  %.59.i.us138.1 = fcmp olt double %.45.i.us137.1, 1.000000e+00
  %.74.i.us139.1 = zext i1 %.59.i.us138.1 to i8
  %scevgep101 = getelementptr i8, i8* %lsr.iv98, i64 -2
  store i8 %.74.i.us139.1, i8* %scevgep101, align 1
  %.669.us131.2 = load double, double* %.6.i26117, align 8
  %scevgep106 = getelementptr double, double* %lsr.iv104, i64 -1
  %.678.us134.2 = load double, double* %scevgep106, align 8
  %.18.i.us135.2 = fmul double %.669.us131.2, %.669.us131.2
  %.33.i.us136.2 = fmul double %.678.us134.2, %.678.us134.2
  %.45.i.us137.2 = fadd double %.18.i.us135.2, %.33.i.us136.2
  %.59.i.us138.2 = fcmp olt double %.45.i.us137.2, 1.000000e+00
  %.74.i.us139.2 = zext i1 %.59.i.us138.2 to i8
  %scevgep100 = getelementptr i8, i8* %lsr.iv98, i64 -1
  store i8 %.74.i.us139.2, i8* %scevgep100, align 1
  %.669.us131.3 = load double, double* %.6.i26117, align 8
  %.678.us134.3 = load double, double* %lsr.iv104, align 8
  %.18.i.us135.3 = fmul double %.669.us131.3, %.669.us131.3
  %.33.i.us136.3 = fmul double %.678.us134.3, %.678.us134.3
  %.45.i.us137.3 = fadd double %.18.i.us135.3, %.33.i.us136.3
  %.59.i.us138.3 = fcmp olt double %.45.i.us137.3, 1.000000e+00
  %.74.i.us139.3 = zext i1 %.59.i.us138.3 to i8
  store i8 %.74.i.us139.3, i8* %lsr.iv98, align 1
  %lsr.iv.next96 = add i64 %lsr.iv95, -4
  %scevgep99 = getelementptr i8, i8* %lsr.iv98, i64 4
  %scevgep105 = getelementptr double, double* %lsr.iv104, i64 4
  %exitcond163.3 = icmp eq i64 %lsr.iv.next96, 0
  br i1 %exitcond163.3, label %for.end.4, label %for.body.4.endif.endif.endif.us128, !llvm.loop !78

for.end.4:                                        ; preds = %for.body.4.endif.endif.endif, %for.body.4.endif.endif.endif.us128, %for.body.4.endif.endif.endif.us128.prol.loopexit, %middle.block332, %middle.block299, %for.body.4.endif.endif.endif.prol.loopexit, %for.body.4.endif.endif.endif.us, %for.body.4.endif.endif.endif.us.us, %for.body.4.endif.endif.endif.us.us.prol.loopexit, %middle.block408, %middle.block370, %for.body.4.endif.endif.endif.us.prol.loopexit, %for.end.3.endif.endif.endif.endif
  %const = bitcast i64 9223372036854775792 to i64
  call void @NRT_incref(i8* %.623), !noalias !79
  call void @NRT_decref(i8* %.178)
  call void @NRT_decref(i8* %.433)
  %.77.i = icmp eq i64 %.618181, 0
  br i1 %.77.i, label %for.end.4.endif, label %B22.endif.i.preheader, !prof !0

B22.endif.i.preheader:                            ; preds = %for.end.4
  %428 = icmp sgt i64 %.618181, 1
  %smax = select i1 %428, i64 %.618181, i64 1
  %min.iters.check452 = icmp ult i64 %smax, 16
  br i1 %min.iters.check452, label %B22.endif.i.preheader480, label %min.iters.checked453

min.iters.checked453:                             ; preds = %B22.endif.i.preheader
  %n.vec455 = and i64 %smax, %const
  %cmp.zero456 = icmp eq i64 %n.vec455, 0
  br i1 %cmp.zero456, label %B22.endif.i.preheader480, label %vector.body449.preheader

vector.body449.preheader:                         ; preds = %min.iters.checked453
  %429 = add nsw i64 %n.vec455, -16
  %430 = lshr exact i64 %429, 4
  %431 = and i64 %430, 1
  %lcmp.mod = icmp eq i64 %431, 0
  br i1 %lcmp.mod, label %vector.body449.prol, label %vector.body449.prol.loopexit

vector.body449.prol:                              ; preds = %vector.body449.preheader
  %432 = bitcast i8* %.6.i57 to <4 x i8>*
  %wide.load471.prol = load <4 x i8>, <4 x i8>* %432, align 1, !noalias !79
  %433 = getelementptr i8, i8* %.6.i57, i64 4
  %434 = bitcast i8* %433 to <4 x i8>*
  %wide.load472.prol = load <4 x i8>, <4 x i8>* %434, align 1, !noalias !79
  %435 = getelementptr i8, i8* %.6.i57, i64 8
  %436 = bitcast i8* %435 to <4 x i8>*
  %wide.load473.prol = load <4 x i8>, <4 x i8>* %436, align 1, !noalias !79
  %437 = getelementptr i8, i8* %.6.i57, i64 12
  %438 = bitcast i8* %437 to <4 x i8>*
  %wide.load474.prol = load <4 x i8>, <4 x i8>* %438, align 1, !noalias !79
  %439 = and <4 x i8> %wide.load471.prol, <i8 1, i8 1, i8 1, i8 1>
  %440 = and <4 x i8> %wide.load472.prol, <i8 1, i8 1, i8 1, i8 1>
  %441 = and <4 x i8> %wide.load473.prol, <i8 1, i8 1, i8 1, i8 1>
  %442 = and <4 x i8> %wide.load474.prol, <i8 1, i8 1, i8 1, i8 1>
  %443 = zext <4 x i8> %439 to <4 x i64>
  %444 = zext <4 x i8> %440 to <4 x i64>
  %445 = zext <4 x i8> %441 to <4 x i64>
  %446 = zext <4 x i8> %442 to <4 x i64>
  br label %vector.body449.prol.loopexit

vector.body449.prol.loopexit:                     ; preds = %vector.body449.prol, %vector.body449.preheader
  %.lcssa497.unr = phi <4 x i64> [ undef, %vector.body449.preheader ], [ %443, %vector.body449.prol ]
  %.lcssa496.unr = phi <4 x i64> [ undef, %vector.body449.preheader ], [ %444, %vector.body449.prol ]
  %.lcssa495.unr = phi <4 x i64> [ undef, %vector.body449.preheader ], [ %445, %vector.body449.prol ]
  %.lcssa.unr = phi <4 x i64> [ undef, %vector.body449.preheader ], [ %446, %vector.body449.prol ]
  %index458.unr = phi i64 [ 0, %vector.body449.preheader ], [ 16, %vector.body449.prol ]
  %vec.phi.unr = phi <4 x i64> [ zeroinitializer, %vector.body449.preheader ], [ %443, %vector.body449.prol ]
  %vec.phi462.unr = phi <4 x i64> [ zeroinitializer, %vector.body449.preheader ], [ %444, %vector.body449.prol ]
  %vec.phi463.unr = phi <4 x i64> [ zeroinitializer, %vector.body449.preheader ], [ %445, %vector.body449.prol ]
  %vec.phi464.unr = phi <4 x i64> [ zeroinitializer, %vector.body449.preheader ], [ %446, %vector.body449.prol ]
  %447 = icmp eq i64 %430, 0
  br i1 %447, label %middle.block450, label %vector.body449.preheader.new

vector.body449.preheader.new:                     ; preds = %vector.body449.prol.loopexit
  %448 = sub i64 %n.vec455, %index458.unr
  %449 = add i64 %index458.unr, 28
  %scevgep2 = getelementptr i8, i8* %.6.i57, i64 %449
  br label %vector.body449

vector.body449:                                   ; preds = %vector.body449, %vector.body449.preheader.new
  %lsr.iv3 = phi i8* [ %scevgep4, %vector.body449 ], [ %scevgep2, %vector.body449.preheader.new ]
  %lsr.iv = phi i64 [ %lsr.iv.next, %vector.body449 ], [ %448, %vector.body449.preheader.new ]
  %vec.phi = phi <4 x i64> [ %vec.phi.unr, %vector.body449.preheader.new ], [ %470, %vector.body449 ]
  %vec.phi462 = phi <4 x i64> [ %vec.phi462.unr, %vector.body449.preheader.new ], [ %471, %vector.body449 ]
  %vec.phi463 = phi <4 x i64> [ %vec.phi463.unr, %vector.body449.preheader.new ], [ %472, %vector.body449 ]
  %vec.phi464 = phi <4 x i64> [ %vec.phi464.unr, %vector.body449.preheader.new ], [ %473, %vector.body449 ]
  %lsr.iv35 = bitcast i8* %lsr.iv3 to <4 x i8>*
  %scevgep12 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv35, i64 -7
  %wide.load471 = load <4 x i8>, <4 x i8>* %scevgep12, align 1, !noalias !79
  %scevgep11 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv35, i64 -6
  %wide.load472 = load <4 x i8>, <4 x i8>* %scevgep11, align 1, !noalias !79
  %scevgep10 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv35, i64 -5
  %wide.load473 = load <4 x i8>, <4 x i8>* %scevgep10, align 1, !noalias !79
  %scevgep9 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv35, i64 -4
  %wide.load474 = load <4 x i8>, <4 x i8>* %scevgep9, align 1, !noalias !79
  %450 = and <4 x i8> %wide.load471, <i8 1, i8 1, i8 1, i8 1>
  %451 = and <4 x i8> %wide.load472, <i8 1, i8 1, i8 1, i8 1>
  %452 = and <4 x i8> %wide.load473, <i8 1, i8 1, i8 1, i8 1>
  %453 = and <4 x i8> %wide.load474, <i8 1, i8 1, i8 1, i8 1>
  %454 = zext <4 x i8> %450 to <4 x i64>
  %455 = zext <4 x i8> %451 to <4 x i64>
  %456 = zext <4 x i8> %452 to <4 x i64>
  %457 = zext <4 x i8> %453 to <4 x i64>
  %458 = add nsw <4 x i64> %454, %vec.phi
  %459 = add nsw <4 x i64> %455, %vec.phi462
  %460 = add nsw <4 x i64> %456, %vec.phi463
  %461 = add nsw <4 x i64> %457, %vec.phi464
  %scevgep8 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv35, i64 -3
  %wide.load471.1 = load <4 x i8>, <4 x i8>* %scevgep8, align 1, !noalias !79
  %scevgep7 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv35, i64 -2
  %wide.load472.1 = load <4 x i8>, <4 x i8>* %scevgep7, align 1, !noalias !79
  %scevgep6 = getelementptr <4 x i8>, <4 x i8>* %lsr.iv35, i64 -1
  %wide.load473.1 = load <4 x i8>, <4 x i8>* %scevgep6, align 1, !noalias !79
  %wide.load474.1 = load <4 x i8>, <4 x i8>* %lsr.iv35, align 1, !noalias !79
  %462 = and <4 x i8> %wide.load471.1, <i8 1, i8 1, i8 1, i8 1>
  %463 = and <4 x i8> %wide.load472.1, <i8 1, i8 1, i8 1, i8 1>
  %464 = and <4 x i8> %wide.load473.1, <i8 1, i8 1, i8 1, i8 1>
  %465 = and <4 x i8> %wide.load474.1, <i8 1, i8 1, i8 1, i8 1>
  %466 = zext <4 x i8> %462 to <4 x i64>
  %467 = zext <4 x i8> %463 to <4 x i64>
  %468 = zext <4 x i8> %464 to <4 x i64>
  %469 = zext <4 x i8> %465 to <4 x i64>
  %470 = add nsw <4 x i64> %466, %458
  %471 = add nsw <4 x i64> %467, %459
  %472 = add nsw <4 x i64> %468, %460
  %473 = add nsw <4 x i64> %469, %461
  %lsr.iv.next = add i64 %lsr.iv, -32
  %scevgep4 = getelementptr i8, i8* %lsr.iv3, i64 32
  %474 = icmp eq i64 %lsr.iv.next, 0
  br i1 %474, label %middle.block450, label %vector.body449, !llvm.loop !82

middle.block450:                                  ; preds = %vector.body449, %vector.body449.prol.loopexit
  %.lcssa497 = phi <4 x i64> [ %.lcssa497.unr, %vector.body449.prol.loopexit ], [ %470, %vector.body449 ]
  %.lcssa496 = phi <4 x i64> [ %.lcssa496.unr, %vector.body449.prol.loopexit ], [ %471, %vector.body449 ]
  %.lcssa495 = phi <4 x i64> [ %.lcssa495.unr, %vector.body449.prol.loopexit ], [ %472, %vector.body449 ]
  %.lcssa = phi <4 x i64> [ %.lcssa.unr, %vector.body449.prol.loopexit ], [ %473, %vector.body449 ]
  %bin.rdx = add <4 x i64> %.lcssa496, %.lcssa497
  %bin.rdx475 = add <4 x i64> %.lcssa495, %bin.rdx
  %bin.rdx476 = add <4 x i64> %.lcssa, %bin.rdx475
  %rdx.shuf = shufflevector <4 x i64> %bin.rdx476, <4 x i64> undef, <4 x i32> <i32 2, i32 3, i32 undef, i32 undef>
  %bin.rdx477 = add <4 x i64> %bin.rdx476, %rdx.shuf
  %rdx.shuf478 = shufflevector <4 x i64> %bin.rdx477, <4 x i64> undef, <4 x i32> <i32 1, i32 undef, i32 undef, i32 undef>
  %bin.rdx479 = add <4 x i64> %bin.rdx477, %rdx.shuf478
  %475 = extractelement <4 x i64> %bin.rdx479, i32 0
  %cmp.n461 = icmp eq i64 %smax, %n.vec455
  br i1 %cmp.n461, label %for.end.4.endif.loopexit, label %B22.endif.i.preheader480

B22.endif.i.preheader480:                         ; preds = %middle.block450, %min.iters.checked453, %B22.endif.i.preheader
  %c.08.i.ph = phi i64 [ 0, %min.iters.checked453 ], [ 0, %B22.endif.i.preheader ], [ %475, %middle.block450 ]
  %.79.07.i.ph = phi i64 [ 0, %min.iters.checked453 ], [ 0, %B22.endif.i.preheader ], [ %n.vec455, %middle.block450 ]
  br label %B22.endif.i

B22.endif.i:                                      ; preds = %B22.endif.i, %B22.endif.i.preheader480
  %c.08.i = phi i64 [ %.299.i, %B22.endif.i ], [ %c.08.i.ph, %B22.endif.i.preheader480 ]
  %.79.07.i = phi i64 [ %.185.i, %B22.endif.i ], [ %.79.07.i.ph, %B22.endif.i.preheader480 ]
  %scevgep1 = getelementptr i8, i8* %.6.i57, i64 %.79.07.i
  %.185.i = add nuw nsw i64 %.79.07.i, 1
  %.287.i = load i8, i8* %scevgep1, align 1, !noalias !79
  %476 = and i8 %.287.i, 1
  %.298.i = zext i8 %476 to i64
  %.299.i = add nsw i64 %.298.i, %c.08.i
  %.186.i = icmp slt i64 %.185.i, %.618181
  br i1 %.186.i, label %B22.endif.i, label %for.end.4.endif.loopexit, !prof !83, !llvm.loop !84

for.body.4.endif.endif.endif:                     ; preds = %for.body.4.endif.endif.endif, %for.body.4.endif.endif.endif.preheader486.new
  %lsr.iv132 = phi i8* [ %scevgep133, %for.body.4.endif.endif.endif ], [ %scevgep131, %for.body.4.endif.endif.endif.preheader486.new ]
  %lsr.iv129 = phi i64 [ %lsr.iv.next130, %for.body.4.endif.endif.endif ], [ %350, %for.body.4.endif.endif.endif.preheader486.new ]
  %.669 = load double, double* %.6.i26117, align 8
  %.678 = load double, double* %.6.i113119, align 8
  %.18.i = fmul double %.669, %.669
  %.33.i = fmul double %.678, %.678
  %.45.i = fadd double %.18.i, %.33.i
  %.59.i = fcmp olt double %.45.i, 1.000000e+00
  %.74.i = zext i1 %.59.i to i8
  %scevgep136 = getelementptr i8, i8* %lsr.iv132, i64 -3
  store i8 %.74.i, i8* %scevgep136, align 1
  %.669.1 = load double, double* %.6.i26117, align 8
  %.678.1 = load double, double* %.6.i113119, align 8
  %.18.i.1 = fmul double %.669.1, %.669.1
  %.33.i.1 = fmul double %.678.1, %.678.1
  %.45.i.1 = fadd double %.18.i.1, %.33.i.1
  %.59.i.1 = fcmp olt double %.45.i.1, 1.000000e+00
  %.74.i.1 = zext i1 %.59.i.1 to i8
  %scevgep135 = getelementptr i8, i8* %lsr.iv132, i64 -2
  store i8 %.74.i.1, i8* %scevgep135, align 1
  %.669.2 = load double, double* %.6.i26117, align 8
  %.678.2 = load double, double* %.6.i113119, align 8
  %.18.i.2 = fmul double %.669.2, %.669.2
  %.33.i.2 = fmul double %.678.2, %.678.2
  %.45.i.2 = fadd double %.18.i.2, %.33.i.2
  %.59.i.2 = fcmp olt double %.45.i.2, 1.000000e+00
  %.74.i.2 = zext i1 %.59.i.2 to i8
  %scevgep134 = getelementptr i8, i8* %lsr.iv132, i64 -1
  store i8 %.74.i.2, i8* %scevgep134, align 1
  %.669.3 = load double, double* %.6.i26117, align 8
  %.678.3 = load double, double* %.6.i113119, align 8
  %.18.i.3 = fmul double %.669.3, %.669.3
  %.33.i.3 = fmul double %.678.3, %.678.3
  %.45.i.3 = fadd double %.18.i.3, %.33.i.3
  %.59.i.3 = fcmp olt double %.45.i.3, 1.000000e+00
  %.74.i.3 = zext i1 %.59.i.3 to i8
  store i8 %.74.i.3, i8* %lsr.iv132, align 1
  %lsr.iv.next130 = add i64 %lsr.iv129, -4
  %scevgep133 = getelementptr i8, i8* %lsr.iv132, i64 4
  %exitcond164.3 = icmp eq i64 %lsr.iv.next130, 0
  br i1 %exitcond164.3, label %for.end.4, label %for.body.4.endif.endif.endif, !llvm.loop !86

for.end.4.endif.loopexit:                         ; preds = %B22.endif.i, %middle.block450
  %.299.i.lcssa = phi i64 [ %475, %middle.block450 ], [ %.299.i, %B22.endif.i ]
  %phitmp182 = shl i64 %.299.i.lcssa, 2
  br label %for.end.4.endif

for.end.4.endif:                                  ; preds = %for.end.4.endif.loopexit, %for.end.4
  %c.0.lcssa.i = phi i64 [ 0, %for.end.4 ], [ %phitmp182, %for.end.4.endif.loopexit ]
  %477 = icmp eq i32 %arg.n, 0
  call void @NRT_decref(i8* %.623), !noalias !79
  call void @NRT_decref(i8* %.623)
  br i1 %477, label %for.end.4.endif.if, label %for.end.4.endif.else, !prof !0

for.end.4.endif.if:                               ; preds = %for.end.4.endif
  store { i8*, i32 }* @.const.picklebuf.26009893776, { i8*, i32 }** %excinfo, align 8
  ret i32 1

for.end.4.endif.else:                             ; preds = %for.end.4.endif
  %const_mat = add i64 %const, 16
  %notlhs = icmp ne i64 %c.0.lcssa.i, %const_mat
  %notrhs10 = icmp ne i32 %arg.n, -1
  %.766 = or i1 %notrhs10, %notlhs
  br i1 %.766, label %for.end.4.endif.else.if, label %for.end.4.endif.else.endif, !prof !83

for.end.4.endif.else.if:                          ; preds = %for.end.4.endif.else
  %478 = or i64 %c.0.lcssa.i, %.28
  %479 = and i64 %478, -4294967296
  %480 = icmp eq i64 %479, 0
  br i1 %480, label %481, label %488

; <label>:481:                                    ; preds = %for.end.4.endif.else.if
  %482 = trunc i64 %.28 to i32
  %483 = trunc i64 %c.0.lcssa.i to i32
  %484 = udiv i32 %483, %482
  %485 = urem i32 %483, %482
  %486 = zext i32 %484 to i64
  %487 = zext i32 %485 to i64
  br label %491

; <label>:488:                                    ; preds = %for.end.4.endif.else.if
  %489 = sdiv i64 %c.0.lcssa.i, %.28
  %490 = srem i64 %c.0.lcssa.i, %.28
  br label %491

; <label>:491:                                    ; preds = %488, %481
  %492 = phi i64 [ %489, %488 ], [ %486, %481 ]
  %493 = phi i64 [ %490, %488 ], [ %487, %481 ]
  %.770 = xor i64 %493, %.28
  %.771 = icmp slt i64 %.770, 0
  %.772 = icmp ne i64 %493, 0
  %.773 = and i1 %.772, %.771
  %.778 = sext i1 %.773 to i64
  %.778..768 = add i64 %.778, %492
  %phitmp = sitofp i64 %.778..768 to float
  br label %for.end.4.endif.else.endif

for.end.4.endif.else.endif:                       ; preds = %491, %for.end.4.endif.else
  %.759.0 = phi float [ 0.000000e+00, %for.end.4.endif.else ], [ %phitmp, %491 ]
  store float %.759.0, float* %retptr, align 4
  ret i32 0
}

declare noalias i8* @NRT_MemInfo_alloc_safe_aligned(i64, i32) local_unnamed_addr

; Function Attrs: nounwind readnone
declare { i32, [624 x i32], i32, double, i32 }* @numba_get_np_random_state() local_unnamed_addr #0

declare void @numba_rnd_shuffle({ i32, [624 x i32], i32, double, i32 }* nocapture) local_unnamed_addr

define float @"cfunc._ZN14numba_examples2pi12calc_pi$2467Ei"(i32 %.1) local_unnamed_addr {
entry:
  %.3 = alloca float, align 4
  store float 0.000000e+00, float* %.3, align 4
  %excinfo = alloca { i8*, i32 }*, align 8
  %.5 = call i32 @"_ZN14numba_examples2pi12calc_pi$2467Ei"(float* nonnull %.3, { i8*, i32 }** nonnull %excinfo, i8* undef, i32 %.1)
  %.6 = load { i8*, i32 }*, { i8*, i32 }** %excinfo, align 8
  %.7 = icmp eq i32 %.5, 0
  %notrhs = icmp ne i32 %.5, -2
  %.12 = xor i1 %.7, %notrhs
  %.15 = load float, float* %.3, align 4
  %.17 = alloca i32, align 4
  br i1 %.12, label %entry.if, label %entry.endif, !prof !0

entry.if:                                         ; preds = %entry
  %.13 = icmp sgt i32 %.5, 0
  call void @numba_gil_ensure(i32* nonnull %.17)
  br i1 %.13, label %entry.if.if, label %entry.if.endif

entry.endif:                                      ; preds = %.19, %entry
  ret float %.15

.19:                                              ; preds = %entry.if.endif.endif.endif, %entry.if.endif.if, %entry.if.if.if, %entry.if.endif, %entry.if.if
  %.39 = call i8* @PyString_FromString(i8* getelementptr inbounds ([29 x i8], [29 x i8]* @".const.<Numba C callback 'calc_pi'>", i64 0, i64 0))
  call void @PyErr_WriteUnraisable(i8* %.39)
  call void @Py_DecRef(i8* %.39)
  call void @numba_gil_release(i32* nonnull %.17)
  br label %entry.endif

entry.if.if:                                      ; preds = %entry.if
  call void @PyErr_Clear()
  %.22 = load { i8*, i32 }, { i8*, i32 }* %.6, align 8
  %.23 = extractvalue { i8*, i32 } %.22, 0
  %.25 = extractvalue { i8*, i32 } %.22, 1
  %.26 = call i8* @numba_unpickle(i8* %.23, i32 %.25)
  %.27 = icmp eq i8* %.26, null
  br i1 %.27, label %.19, label %entry.if.if.if, !prof !0

entry.if.endif:                                   ; preds = %entry.if
  switch i32 %.5, label %entry.if.endif.endif.endif [
    i32 -3, label %entry.if.endif.if
    i32 -1, label %.19
  ]

entry.if.if.if:                                   ; preds = %entry.if.if
  call void @numba_do_raise(i8* nonnull %.26)
  br label %.19

entry.if.endif.if:                                ; preds = %entry.if.endif
  call void @PyErr_SetNone(i8* nonnull @PyExc_StopIteration)
  br label %.19

entry.if.endif.endif.endif:                       ; preds = %entry.if.endif
  call void @PyErr_SetString(i8* nonnull @PyExc_SystemError, i8* getelementptr inbounds ([43 x i8], [43 x i8]* @".const.unknown error when calling native function", i64 0, i64 0))
  br label %.19
}

declare void @numba_gil_ensure(i32*) local_unnamed_addr

declare i8* @PyString_FromString(i8*) local_unnamed_addr

declare void @PyErr_WriteUnraisable(i8*) local_unnamed_addr

declare void @Py_DecRef(i8*) local_unnamed_addr

declare void @numba_gil_release(i32*) local_unnamed_addr

declare void @PyErr_Clear() local_unnamed_addr

declare i8* @numba_unpickle(i8*, i32) local_unnamed_addr

declare void @numba_do_raise(i8*) local_unnamed_addr

declare void @PyErr_SetNone(i8*) local_unnamed_addr

declare void @PyErr_SetString(i8*, i8*) local_unnamed_addr

; Function Attrs: noinline
define linkonce_odr void @NRT_decref(i8* %.1) local_unnamed_addr #1 {
.3:
  %.4 = icmp eq i8* %.1, null
  br i1 %.4, label %.3.if, label %.3.endif, !prof !0

.3.if:                                            ; preds = %.3.endif, %.3
  ret void

.3.endif:                                         ; preds = %.3
  %.7 = bitcast i8* %.1 to i64*
  %.4.i = atomicrmw sub i64* %.7, i64 1 monotonic
  %.9 = icmp eq i64 %.4.i, 1
  br i1 %.9, label %.3.endif.if, label %.3.if, !prof !0

.3.endif.if:                                      ; preds = %.3.endif
  tail call void @NRT_MemInfo_call_dtor(i8* nonnull %.1)
  ret void
}

declare void @NRT_MemInfo_call_dtor(i8*) local_unnamed_addr

; Function Attrs: noinline norecurse nounwind
define linkonce_odr void @NRT_incref(i8* %.1) local_unnamed_addr #2 {
.3:
  %.4 = icmp eq i8* %.1, null
  br i1 %.4, label %.3.if, label %.3.endif, !prof !0

.3.if:                                            ; preds = %.3
  ret void

.3.endif:                                         ; preds = %.3
  %.7 = bitcast i8* %.1 to i64*
  %.4.i = atomicrmw add i64* %.7, i64 1 monotonic
  ret void
}

; Function Attrs: nounwind
declare void @llvm.stackprotector(i8*, i8**) #3

attributes #0 = { nounwind readnone }
attributes #1 = { noinline }
attributes #2 = { noinline norecurse nounwind }
attributes #3 = { nounwind }

!0 = !{!"branch_weights", i32 1, i32 99}
!1 = distinct !{!1, !2}
!2 = !{!"llvm.loop.unroll.disable"}
!3 = !{!4}
!4 = distinct !{!4, !5}
!5 = distinct !{!5, !"LVerDomain"}
!6 = !{!7}
!7 = distinct !{!7, !5}
!8 = distinct !{!8, !9, !10}
!9 = !{!"llvm.loop.vectorize.width", i32 1}
!10 = !{!"llvm.loop.interleave.count", i32 1}
!11 = !{!12}
!12 = distinct !{!12, !13}
!13 = distinct !{!13, !"LVerDomain"}
!14 = !{!15}
!15 = distinct !{!15, !13}
!16 = distinct !{!16, !9, !10}
!17 = distinct !{!17, !2}
!18 = distinct !{!18, !9, !10}
!19 = distinct !{!19, !9, !10}
!20 = distinct !{!20, !2}
!21 = !{!22}
!22 = distinct !{!22, !23}
!23 = distinct !{!23, !"LVerDomain"}
!24 = !{!25}
!25 = distinct !{!25, !23}
!26 = distinct !{!26, !9, !10}
!27 = !{!28}
!28 = distinct !{!28, !29}
!29 = distinct !{!29, !"LVerDomain"}
!30 = !{!31}
!31 = distinct !{!31, !29}
!32 = distinct !{!32, !9, !10}
!33 = distinct !{!33, !2}
!34 = distinct !{!34, !9, !10}
!35 = distinct !{!35, !9, !10}
!36 = distinct !{!36, !2}
!37 = !{!38}
!38 = distinct !{!38, !39}
!39 = distinct !{!39, !"LVerDomain"}
!40 = !{!41}
!41 = distinct !{!41, !39}
!42 = !{!43}
!43 = distinct !{!43, !39}
!44 = !{!41, !38}
!45 = distinct !{!45, !9, !10}
!46 = !{!47}
!47 = distinct !{!47, !48}
!48 = distinct !{!48, !"LVerDomain"}
!49 = !{!50}
!50 = distinct !{!50, !48}
!51 = !{!52}
!52 = distinct !{!52, !48}
!53 = !{!47, !50}
!54 = distinct !{!54, !9, !10}
!55 = distinct !{!55, !2}
!56 = distinct !{!56, !9, !10}
!57 = distinct !{!57, !9, !10}
!58 = distinct !{!58, !2}
!59 = !{!60}
!60 = distinct !{!60, !61}
!61 = distinct !{!61, !"LVerDomain"}
!62 = !{!63}
!63 = distinct !{!63, !61}
!64 = !{!65}
!65 = distinct !{!65, !61}
!66 = !{!60, !63}
!67 = distinct !{!67, !9, !10}
!68 = !{!69}
!69 = distinct !{!69, !70}
!70 = distinct !{!70, !"LVerDomain"}
!71 = !{!72}
!72 = distinct !{!72, !70}
!73 = !{!74}
!74 = distinct !{!74, !70}
!75 = !{!69, !72}
!76 = distinct !{!76, !9, !10}
!77 = distinct !{!77, !2}
!78 = distinct !{!78, !9, !10}
!79 = !{!80}
!80 = distinct !{!80, !81, !"_ZN5numba7targets9arraymath19array_sum_impl$2471E5ArrayIbLi1E1C7mutable7alignedE: %retptr"}
!81 = distinct !{!81, !"_ZN5numba7targets9arraymath19array_sum_impl$2471E5ArrayIbLi1E1C7mutable7alignedE"}
!82 = distinct !{!82, !9, !10}
!83 = !{!"branch_weights", i32 99, i32 1}
!84 = distinct !{!84, !85, !9, !10}
!85 = !{!"llvm.loop.unroll.runtime.disable"}
!86 = distinct !{!86, !9, !10}
