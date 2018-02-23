; ModuleID = 'numpy_logistic_regression.bc'
source_filename = "<string>"
target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-apple-darwin15.3.0"

@.const.picklebuf.26031137280 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([183 x i8], [183 x i8]* @.const.pickledata.26031137280, i32 0, i32 0), i32 183 }
@.const.pickledata.26031137280 = internal constant [183 x i8] c"\80\02cexceptions\0AValueError\0Aq\00U\91unable to broadcast argument 1 to output array\0AFile \22/Users/maropu/IdeaProjects/lljvm-translator/examples/numpy_logistic_regression.py\22, line 5, q\01\85q\02\86q\03."
@".const.<Numba C callback 'numpy_logistic_regression'>" = internal constant [47 x i8] c"<Numba C callback 'numpy_logistic_regression'>\00"
@PyExc_StopIteration = external global i8
@PyExc_SystemError = external global i8
@".const.unknown error when calling native function" = internal constant [43 x i8] c"unknown error when calling native function\00"
@.const.picklebuf.26030507072 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([69 x i8], [69 x i8]* @.const.pickledata.26030507072, i32 0, i32 0), i32 69 }
@.const.picklebuf.26031217480 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([97 x i8], [97 x i8]* @.const.pickledata.26031217480, i32 0, i32 0), i32 97 }
@.const.picklebuf.26030746440 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([77 x i8], [77 x i8]* @.const.pickledata.26030746440, i32 0, i32 0), i32 77 }
@".const.BLAS wrapper returned with an error" = internal constant [36 x i8] c"BLAS wrapper returned with an error\00"
@.const.pickledata.26030746440 = internal constant [77 x i8] c"\80\02cexceptions\0AOverflowError\0Aq\00U$array size too large to fit in C intq\01\85q\02\86q\03."
@.const.pickledata.26031217480 = internal constant [97 x i8] c"\80\02cexceptions\0AValueError\0Aq\00U;incompatible array sizes for np.dot(a, b) (matrix * vector)q\01\85q\02\86q\03."
@.const.pickledata.26030507072 = internal constant [69 x i8] c"\80\02cexceptions\0AValueError\0Aq\00U\1Fnegative dimensions not allowedq\01\85q\02\86q\03."
@.const.picklebuf.26031217480.15 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([69 x i8], [69 x i8]* @.const.pickledata.26031217480.19, i32 0, i32 0), i32 69 }
@.const.picklebuf.26031115728 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([97 x i8], [97 x i8]* @.const.pickledata.26031115728, i32 0, i32 0), i32 97 }
@.const.picklebuf.26030746440.16 = internal constant { i8*, i32 } { i8* getelementptr inbounds ([77 x i8], [77 x i8]* @.const.pickledata.26030746440.18, i32 0, i32 0), i32 77 }
@".const.BLAS wrapper returned with an error.17" = internal constant [36 x i8] c"BLAS wrapper returned with an error\00"
@.const.pickledata.26030746440.18 = internal constant [77 x i8] c"\80\02cexceptions\0AOverflowError\0Aq\00U$array size too large to fit in C intq\01\85q\02\86q\03."
@.const.pickledata.26031115728 = internal constant [97 x i8] c"\80\02cexceptions\0AValueError\0Aq\00U;incompatible array sizes for np.dot(a, b) (vector * matrix)q\01\85q\02\86q\03."
@.const.pickledata.26031217480.19 = internal constant [69 x i8] c"\80\02cexceptions\0AValueError\0Aq\00U\1Fnegative dimensions not allowedq\01\85q\02\86q\03."

define i32 @"_ZN25numpy_logistic_regression29numpy_logistic_regression$241E5ArrayIdLi1E1A7mutable7alignedE5ArrayIdLi2E1A7mutable7alignedE5ArrayIdLi1E1A7mutable7alignedEx"({ i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* noalias nocapture %retptr, { i8*, i32 }** noalias nocapture %excinfo, i8* noalias nocapture readnone %env, i8* %arg.Y.0, i8* nocapture readnone %arg.Y.1, i64 %arg.Y.2, i64 %arg.Y.3, double* %arg.Y.4, i64 %arg.Y.5.0, i64 %arg.Y.6.0, i8* %arg.X.0, i8* nocapture readnone %arg.X.1, i64 %arg.X.2, i64 %arg.X.3, double* %arg.X.4, i64 %arg.X.5.0, i64 %arg.X.5.1, i64 %arg.X.6.0, i64 %arg.X.6.1, i8* %arg.w.0, i8* %arg.w.1, i64 %arg.w.2, i64 %arg.w.3, double* %arg.w.4, i64 %arg.w.5.0, i64 %arg.w.6.0, i64 %arg.iterations) local_unnamed_addr {
entry:
  %.390 = alloca { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, align 8
  %excinfo.1 = alloca { i8*, i32 }*, align 8
  %.690 = alloca { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, align 8
  %excinfo.5 = alloca { i8*, i32 }*, align 8
  tail call void @NRT_incref(i8* %arg.Y.0)
  tail call void @NRT_incref(i8* %arg.X.0)
  tail call void @NRT_incref(i8* %arg.w.0)
  %.102 = icmp sgt i64 %arg.iterations, 0
  br i1 %.102, label %B13.if.lr.ph, label %B86

B13.if.lr.ph:                                     ; preds = %entry
  %.267 = mul i64 %arg.X.5.1, %arg.X.5.0
  %.268 = shl i64 %arg.X.5.1, 3
  %.269 = shl i64 %.267, 3
  %.30871 = icmp sgt i64 %arg.X.5.0, 0
  %.31169 = icmp sgt i64 %arg.X.5.1, 0
  %.343 = shl i64 %arg.w.5.0, 3
  %0 = add i64 %arg.X.5.1, -1
  %1 = add i64 %arg.w.5.0, -1
  %brmerge.demorgan = and i1 %.30871, %.31169
  %xtraiter = and i64 %arg.X.5.1, 7
  %xtraiter229 = and i64 %arg.w.5.0, 7
  %brmerge165.demorgan = and i1 %.30871, %.31169
  %xtraiter244 = and i64 %arg.X.5.1, 7
  %xtraiter256 = and i64 %arg.w.5.0, 3
  %xtraiter253 = and i64 %arg.w.5.0, 3
  %xtraiter250 = and i64 %arg.w.5.0, 3
  %xtraiter247 = and i64 %arg.w.5.0, 3
  %2 = shl i64 %arg.X.5.1, 3
  %3 = shl i64 %arg.X.6.1, 3
  %4 = shl i64 %arg.w.6.0, 3
  %5 = shl i64 %arg.Y.6.0, 1
  %6 = shl i64 %arg.w.6.0, 2
  br label %B13.if

B86:                                              ; preds = %for.end.6, %entry
  call void @NRT_decref(i8* %arg.Y.0)
  call void @NRT_decref(i8* %arg.X.0)
  %retptr.repack182 = bitcast { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr to i8**
  store i8* %arg.w.0, i8** %retptr.repack182, align 8
  %retptr.repack1 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 1
  store i8* %arg.w.1, i8** %retptr.repack1, align 8
  %retptr.repack3 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 2
  store i64 %arg.w.2, i64* %retptr.repack3, align 8
  %retptr.repack5 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 3
  store i64 %arg.w.3, i64* %retptr.repack5, align 8
  %retptr.repack7 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 4
  store double* %arg.w.4, double** %retptr.repack7, align 8
  %7 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 5, i64 0
  store i64 %arg.w.5.0, i64* %7, align 8
  %8 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 6, i64 0
  store i64 %arg.w.6.0, i64* %8, align 8
  ret i32 0

B13.if:                                           ; preds = %for.end.6, %B13.if.lr.ph
  %.90.0118 = phi i64 [ %arg.iterations, %B13.if.lr.ph ], [ %.154, %for.end.6 ]
  %.154 = add nsw i64 %.90.0118, -1
  %.270 = call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.269, i32 32)
  %.5.i = getelementptr i8, i8* %.270, i64 24
  %9 = bitcast i8* %.5.i to double**
  %.6.i60 = load double*, double** %9, align 8
  br i1 %brmerge.demorgan, label %for.cond.1.preheader.us.preheader, label %for.end

for.cond.1.preheader.us.preheader:                ; preds = %B13.if
  %10 = ptrtoint double* %arg.X.4 to i64
  br label %for.cond.1.preheader.us

for.cond.1.preheader.us:                          ; preds = %for.cond.1.for.end.1_crit_edge.us, %for.cond.1.preheader.us.preheader
  %lsr.iv6 = phi i64 [ %lsr.iv.next, %for.cond.1.for.end.1_crit_edge.us ], [ %10, %for.cond.1.preheader.us.preheader ]
  %lsr.iv = phi double* [ %33, %for.cond.1.for.end.1_crit_edge.us ], [ %.6.i60, %for.cond.1.preheader.us.preheader ]
  %loop.index72.us = phi i64 [ %.329.us, %for.cond.1.for.end.1_crit_edge.us ], [ 0, %for.cond.1.preheader.us.preheader ]
  %11 = icmp eq i64 %xtraiter, 0
  br i1 %11, label %for.body.1.us.prol.loopexit, label %for.body.1.us.prol.preheader

for.body.1.us.prol.preheader:                     ; preds = %for.cond.1.preheader.us
  br label %for.body.1.us.prol

for.body.1.us.prol:                               ; preds = %for.body.1.us.prol, %for.body.1.us.prol.preheader
  %lsr.iv7 = phi i64 [ %lsr.iv.next8, %for.body.1.us.prol ], [ %lsr.iv6, %for.body.1.us.prol.preheader ]
  %lsr.iv3 = phi double* [ %scevgep4, %for.body.1.us.prol ], [ %lsr.iv, %for.body.1.us.prol.preheader ]
  %loop.index.170.us.prol = phi i64 [ 0, %for.body.1.us.prol.preheader ], [ %.327.us.prol, %for.body.1.us.prol ]
  %lsr.iv35 = bitcast double* %lsr.iv3 to i64*
  %12 = inttoptr i64 %lsr.iv7 to i64*
  %.32525.us.prol = load i64, i64* %12, align 8
  store i64 %.32525.us.prol, i64* %lsr.iv35, align 8
  %.327.us.prol = add nuw nsw i64 %loop.index.170.us.prol, 1
  %scevgep4 = getelementptr double, double* %lsr.iv3, i64 1
  %lsr.iv.next8 = add i64 %lsr.iv7, %arg.X.6.1
  %prol.iter.cmp = icmp eq i64 %xtraiter, %.327.us.prol
  br i1 %prol.iter.cmp, label %for.body.1.us.prol.loopexit, label %for.body.1.us.prol, !llvm.loop !0

for.body.1.us.prol.loopexit:                      ; preds = %for.body.1.us.prol, %for.cond.1.preheader.us
  %loop.index.170.us.unr = phi i64 [ 0, %for.cond.1.preheader.us ], [ %.327.us.prol, %for.body.1.us.prol ]
  %13 = icmp ult i64 %0, 7
  br i1 %13, label %for.cond.1.for.end.1_crit_edge.us, label %for.cond.1.preheader.us.new

for.cond.1.preheader.us.new:                      ; preds = %for.body.1.us.prol.loopexit
  %14 = mul i64 %arg.X.6.1, %loop.index.170.us.unr
  %15 = add i64 %lsr.iv6, %14
  br label %for.body.1.us

for.body.1.us:                                    ; preds = %for.body.1.us, %for.cond.1.preheader.us.new
  %lsr.iv9 = phi i64 [ %lsr.iv.next10, %for.body.1.us ], [ %15, %for.cond.1.preheader.us.new ]
  %loop.index.170.us = phi i64 [ %loop.index.170.us.unr, %for.cond.1.preheader.us.new ], [ %.327.us.7, %for.body.1.us ]
  %16 = bitcast double* %lsr.iv to i64*
  %17 = inttoptr i64 %lsr.iv9 to i64*
  %.32525.us = load i64, i64* %17, align 8
  %scevgep18 = getelementptr i64, i64* %16, i64 %loop.index.170.us
  store i64 %.32525.us, i64* %scevgep18, align 8
  %18 = add i64 %arg.X.6.1, %lsr.iv9
  %19 = inttoptr i64 %18 to i64*
  %.32525.us.1 = load i64, i64* %19, align 8
  %scevgep19 = getelementptr i64, i64* %16, i64 %loop.index.170.us
  %scevgep20 = getelementptr i64, i64* %scevgep19, i64 1
  store i64 %.32525.us.1, i64* %scevgep20, align 8
  %20 = add i64 %arg.X.6.1, %18
  %21 = inttoptr i64 %20 to i64*
  %.32525.us.2 = load i64, i64* %21, align 8
  %scevgep21 = getelementptr i64, i64* %16, i64 %loop.index.170.us
  %scevgep22 = getelementptr i64, i64* %scevgep21, i64 2
  store i64 %.32525.us.2, i64* %scevgep22, align 8
  %22 = add i64 %arg.X.6.1, %20
  %23 = inttoptr i64 %22 to i64*
  %.32525.us.3 = load i64, i64* %23, align 8
  %scevgep23 = getelementptr i64, i64* %16, i64 %loop.index.170.us
  %scevgep24 = getelementptr i64, i64* %scevgep23, i64 3
  store i64 %.32525.us.3, i64* %scevgep24, align 8
  %24 = add i64 %arg.X.6.1, %22
  %25 = inttoptr i64 %24 to i64*
  %.32525.us.4 = load i64, i64* %25, align 8
  %scevgep12 = getelementptr i64, i64* %16, i64 %loop.index.170.us
  %scevgep13 = getelementptr i64, i64* %scevgep12, i64 4
  store i64 %.32525.us.4, i64* %scevgep13, align 8
  %26 = add i64 %arg.X.6.1, %24
  %27 = inttoptr i64 %26 to i64*
  %.32525.us.5 = load i64, i64* %27, align 8
  %scevgep25 = getelementptr i64, i64* %16, i64 %loop.index.170.us
  %scevgep26 = getelementptr i64, i64* %scevgep25, i64 5
  store i64 %.32525.us.5, i64* %scevgep26, align 8
  %28 = add i64 %arg.X.6.1, %26
  %29 = inttoptr i64 %28 to i64*
  %.32525.us.6 = load i64, i64* %29, align 8
  %scevgep16 = getelementptr i64, i64* %16, i64 %loop.index.170.us
  %scevgep17 = getelementptr i64, i64* %scevgep16, i64 6
  store i64 %.32525.us.6, i64* %scevgep17, align 8
  %30 = add i64 %arg.X.6.1, %28
  %31 = inttoptr i64 %30 to i64*
  %.32525.us.7 = load i64, i64* %31, align 8
  %scevgep14 = getelementptr i64, i64* %16, i64 %loop.index.170.us
  %scevgep15 = getelementptr i64, i64* %scevgep14, i64 7
  store i64 %.32525.us.7, i64* %scevgep15, align 8
  %.327.us.7 = add nsw i64 %loop.index.170.us, 8
  %lsr.iv.next10 = add i64 %lsr.iv9, %3
  %exitcond.7 = icmp eq i64 %arg.X.5.1, %.327.us.7
  br i1 %exitcond.7, label %for.cond.1.for.end.1_crit_edge.us, label %for.body.1.us

for.cond.1.for.end.1_crit_edge.us:                ; preds = %for.body.1.us, %for.body.1.us.prol.loopexit
  %32 = bitcast double* %lsr.iv to i1*
  %.329.us = add nuw nsw i64 %loop.index72.us, 1
  %scevgep2 = getelementptr i1, i1* %32, i64 %2
  %33 = bitcast i1* %scevgep2 to double*
  %lsr.iv.next = add i64 %lsr.iv6, %arg.X.6.0
  %exitcond134 = icmp eq i64 %.329.us, %arg.X.5.0
  br i1 %exitcond134, label %for.end, label %for.cond.1.preheader.us

for.end:                                          ; preds = %for.cond.1.for.end.1_crit_edge.us, %B13.if
  %34 = icmp sgt i64 %arg.w.5.0, 0
  %.344 = call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.343, i32 32)
  %.5.i26 = getelementptr i8, i8* %.344, i64 24
  %35 = bitcast i8* %.5.i26 to double**
  %.6.i2761 = load double*, double** %35, align 8
  br i1 %34, label %for.body.2.preheader, label %for.end.2

for.body.2.preheader:                             ; preds = %for.end
  %36 = icmp eq i64 %xtraiter229, 0
  br i1 %36, label %for.body.2.prol.loopexit, label %for.body.2.prol.preheader

for.body.2.prol.preheader:                        ; preds = %for.body.2.preheader
  %37 = ptrtoint double* %arg.w.4 to i64
  br label %for.body.2.prol

for.body.2.prol:                                  ; preds = %for.body.2.prol, %for.body.2.prol.preheader
  %lsr.iv29 = phi i64 [ %lsr.iv.next30, %for.body.2.prol ], [ %37, %for.body.2.prol.preheader ]
  %loop.index.274.prol = phi i64 [ %.387.prol, %for.body.2.prol ], [ 0, %for.body.2.prol.preheader ]
  %38 = bitcast double* %.6.i2761 to i64*
  %39 = inttoptr i64 %lsr.iv29 to i64*
  %.38524.prol = load i64, i64* %39, align 8
  %scevgep28 = getelementptr i64, i64* %38, i64 %loop.index.274.prol
  store i64 %.38524.prol, i64* %scevgep28, align 8
  %.387.prol = add nuw nsw i64 %loop.index.274.prol, 1
  %lsr.iv.next30 = add i64 %lsr.iv29, %arg.w.6.0
  %prol.iter231.cmp = icmp eq i64 %xtraiter229, %.387.prol
  br i1 %prol.iter231.cmp, label %for.body.2.prol.loopexit, label %for.body.2.prol, !llvm.loop !2

for.body.2.prol.loopexit:                         ; preds = %for.body.2.prol, %for.body.2.preheader
  %loop.index.274.unr = phi i64 [ 0, %for.body.2.preheader ], [ %.387.prol, %for.body.2.prol ]
  %40 = icmp ult i64 %1, 7
  br i1 %40, label %for.end.2, label %for.body.2.preheader.new

for.body.2.preheader.new:                         ; preds = %for.body.2.prol.loopexit
  %41 = ptrtoint double* %arg.w.4 to i64
  %42 = sub i64 %arg.w.5.0, %loop.index.274.unr
  %scevgep33 = getelementptr double, double* %.6.i2761, i64 7
  %scevgep34 = getelementptr double, double* %scevgep33, i64 %loop.index.274.unr
  %43 = mul i64 %arg.w.6.0, %loop.index.274.unr
  %44 = add i64 %41, %43
  br label %for.body.2

for.body.2:                                       ; preds = %for.body.2, %for.body.2.preheader.new
  %lsr.iv46 = phi i64 [ %lsr.iv.next47, %for.body.2 ], [ %44, %for.body.2.preheader.new ]
  %lsr.iv36 = phi double* [ %scevgep37, %for.body.2 ], [ %scevgep34, %for.body.2.preheader.new ]
  %lsr.iv31 = phi i64 [ %lsr.iv.next32, %for.body.2 ], [ %42, %for.body.2.preheader.new ]
  %lsr.iv3638 = bitcast double* %lsr.iv36 to i64*
  %45 = inttoptr i64 %lsr.iv46 to i64*
  %.38524 = load i64, i64* %45, align 8
  %scevgep45 = getelementptr i64, i64* %lsr.iv3638, i64 -7
  store i64 %.38524, i64* %scevgep45, align 8
  %46 = add i64 %arg.w.6.0, %lsr.iv46
  %47 = inttoptr i64 %46 to i64*
  %.38524.1 = load i64, i64* %47, align 8
  %scevgep44 = getelementptr i64, i64* %lsr.iv3638, i64 -6
  store i64 %.38524.1, i64* %scevgep44, align 8
  %48 = add i64 %arg.w.6.0, %46
  %49 = inttoptr i64 %48 to i64*
  %.38524.2 = load i64, i64* %49, align 8
  %scevgep43 = getelementptr i64, i64* %lsr.iv3638, i64 -5
  store i64 %.38524.2, i64* %scevgep43, align 8
  %50 = add i64 %arg.w.6.0, %48
  %51 = inttoptr i64 %50 to i64*
  %.38524.3 = load i64, i64* %51, align 8
  %scevgep42 = getelementptr i64, i64* %lsr.iv3638, i64 -4
  store i64 %.38524.3, i64* %scevgep42, align 8
  %52 = add i64 %arg.w.6.0, %50
  %53 = inttoptr i64 %52 to i64*
  %.38524.4 = load i64, i64* %53, align 8
  %scevgep41 = getelementptr i64, i64* %lsr.iv3638, i64 -3
  store i64 %.38524.4, i64* %scevgep41, align 8
  %54 = add i64 %arg.w.6.0, %52
  %55 = inttoptr i64 %54 to i64*
  %.38524.5 = load i64, i64* %55, align 8
  %scevgep40 = getelementptr i64, i64* %lsr.iv3638, i64 -2
  store i64 %.38524.5, i64* %scevgep40, align 8
  %56 = add i64 %arg.w.6.0, %54
  %57 = inttoptr i64 %56 to i64*
  %.38524.6 = load i64, i64* %57, align 8
  %scevgep39 = getelementptr i64, i64* %lsr.iv3638, i64 -1
  store i64 %.38524.6, i64* %scevgep39, align 8
  %58 = add i64 %arg.w.6.0, %56
  %59 = inttoptr i64 %58 to i64*
  %.38524.7 = load i64, i64* %59, align 8
  store i64 %.38524.7, i64* %lsr.iv3638, align 8
  %lsr.iv.next32 = add i64 %lsr.iv31, -8
  %scevgep37 = getelementptr double, double* %lsr.iv36, i64 8
  %lsr.iv.next47 = add i64 %lsr.iv46, %4
  %exitcond135.7 = icmp eq i64 %lsr.iv.next32, 0
  br i1 %exitcond135.7, label %for.end.2, label %for.body.2

for.end.2:                                        ; preds = %for.body.2, %for.body.2.prol.loopexit, %for.end
  %60 = bitcast { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.390 to i8*
  %61 = bitcast { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.390 to i8**
  call void @llvm.memset.p0i8.i64(i8* nonnull %60, i8 0, i64 56, i32 8, i1 false)
  %.398 = call i32 @"_ZN5numba7targets6linalg12dot_impl$242E5ArrayIdLi2E1C7mutable7alignedE5ArrayIdLi1E1C7mutable7alignedE"({ i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* nonnull %.390, { i8*, i32 }** nonnull %excinfo.1, i8* null, i8* %.270, i8* null, i64 %.267, i64 8, double* %.6.i60, i64 %arg.X.5.0, i64 %arg.X.5.1, i64 %.268, i64 8, i8* %.344, i8* null, i64 %arg.w.5.0, i64 8, double* %.6.i2761, i64 %arg.w.5.0, i64 8)
  %.400 = icmp eq i32 %.398, 0
  %notrhs = icmp ne i32 %.398, -2
  %.405 = xor i1 %.400, %notrhs
  %.408.fca.0.load = load i8*, i8** %61, align 8
  %sunkaddr = ptrtoint { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.390 to i64
  %sunkaddr183 = add i64 %sunkaddr, 32
  %sunkaddr184 = inttoptr i64 %sunkaddr183 to double**
  %.408.fca.4.load = load double*, double** %sunkaddr184, align 8
  %sunkaddr185 = ptrtoint { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.390 to i64
  %sunkaddr186 = add i64 %sunkaddr185, 40
  %sunkaddr187 = inttoptr i64 %sunkaddr186 to i64*
  %.408.fca.5.0.load = load i64, i64* %sunkaddr187, align 8
  br i1 %.405, label %for.end.2.if, label %B115.i, !prof !3

for.end.2.if:                                     ; preds = %for.end.2
  %62 = bitcast { i8*, i32 }** %excinfo.1 to i64*
  %.39913 = load i64, i64* %62, align 8
  %63 = bitcast { i8*, i32 }** %excinfo to i64*
  store i64 %.39913, i64* %63, align 8
  ret i32 %.398

B115.i:                                           ; preds = %for.end.2
  call void @NRT_decref(i8* %.270)
  call void @NRT_decref(i8* %.344)
  %.204.i = icmp eq i64 %.408.fca.5.0.load, 1
  br i1 %.204.i, label %for.end.2.endif.endif.endif.endif.endif, label %B79.i41

B79.i41:                                          ; preds = %B115.i
  %64 = icmp eq i64 %arg.Y.5.0, 1
  %.131.i38 = icmp eq i64 %.408.fca.5.0.load, %arg.Y.5.0
  %or.cond.i40 = or i1 %64, %.131.i38
  br i1 %or.cond.i40, label %for.end.2.endif.endif.endif.endif.endif, label %for.end.2.endif.endif.endif.endif.if

for.end.2.endif.endif.endif.endif.if:             ; preds = %B79.i41
  store { i8*, i32 }* @.const.picklebuf.26031137280, { i8*, i32 }** %excinfo, align 8
  ret i32 1

for.end.2.endif.endif.endif.endif.endif:          ; preds = %B79.i41, %B115.i
  %.516150 = phi i64 [ %.408.fca.5.0.load, %B79.i41 ], [ %arg.Y.5.0, %B115.i ]
  %.520 = shl i64 %.516150, 3
  %.521 = call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.520, i32 32)
  %.5.i56 = getelementptr i8, i8* %.521, i64 24
  %65 = bitcast i8* %.5.i56 to double**
  %.6.i5762 = load double*, double** %65, align 8
  %.55875 = icmp sgt i64 %.516150, 0
  br i1 %.55875, label %for.body.3.endif.endif.endif.lr.ph, label %for.end.3

for.body.3.endif.endif.endif.lr.ph:               ; preds = %for.end.2.endif.endif.endif.endif.endif
  %.560 = icmp ugt i64 %.408.fca.5.0.load, 1
  br i1 %.560, label %for.body.3.endif.endif.endif.lr.ph.split.us, label %for.body.3.endif.endif.endif.lr.ph.split

for.body.3.endif.endif.endif.lr.ph.split.us:      ; preds = %for.body.3.endif.endif.endif.lr.ph
  %66 = icmp ugt i64 %arg.Y.5.0, 1
  br i1 %66, label %for.body.3.endif.endif.endif.us.us.preheader, label %for.body.3.endif.endif.endif.us.preheader

for.body.3.endif.endif.endif.us.us.preheader:     ; preds = %for.body.3.endif.endif.endif.lr.ph.split.us
  %xtraiter242 = and i64 %.516150, 1
  %lcmp.mod243 = icmp eq i64 %xtraiter242, 0
  br i1 %lcmp.mod243, label %for.body.3.endif.endif.endif.us.us.prol.loopexit, label %for.body.3.endif.endif.endif.us.us.prol

for.body.3.endif.endif.endif.us.us.prol:          ; preds = %for.body.3.endif.endif.endif.us.us.preheader
  %.568.us.us.prol = load double, double* %.408.fca.4.load, align 8
  %.578.us.us.prol = load double, double* %arg.Y.4, align 8
  %67 = fmul double %.568.us.us.prol, %.578.us.us.prol
  %.29.i.us.us.prol = fsub double -0.000000e+00, %67
  %.43.le.i.us.us.prol = call double @llvm.exp.f64(double %.29.i.us.us.prol) #5
  %phitmp.le.i.us.us.prol = fadd double %.43.le.i.us.us.prol, 1.000000e+00
  %.70.i.us.us.prol = fdiv double 1.000000e+00, %phitmp.le.i.us.us.prol
  %.83.i.us.us.prol = fadd double %.70.i.us.us.prol, -1.000000e+00
  %.93.i.us.us.prol = fmul double %.578.us.us.prol, %.83.i.us.us.prol
  store double %.93.i.us.us.prol, double* %.6.i5762, align 8
  br label %for.body.3.endif.endif.endif.us.us.prol.loopexit

for.body.3.endif.endif.endif.us.us.prol.loopexit: ; preds = %for.body.3.endif.endif.endif.us.us.prol, %for.body.3.endif.endif.endif.us.us.preheader
  %loop.index.378.us.us.unr.ph = phi i64 [ 1, %for.body.3.endif.endif.endif.us.us.prol ], [ 0, %for.body.3.endif.endif.endif.us.us.preheader ]
  %68 = icmp eq i64 %.516150, 1
  br i1 %68, label %for.end.3, label %for.body.3.endif.endif.endif.us.us.preheader.new

for.body.3.endif.endif.endif.us.us.preheader.new: ; preds = %for.body.3.endif.endif.endif.us.us.prol.loopexit
  %69 = ptrtoint double* %arg.Y.4 to i64
  %70 = sub i64 %.516150, %loop.index.378.us.us.unr.ph
  %scevgep100 = getelementptr double, double* %.6.i5762, i64 1
  %scevgep101 = getelementptr double, double* %scevgep100, i64 %loop.index.378.us.us.unr.ph
  %71 = add i64 %loop.index.378.us.us.unr.ph, 1
  %72 = mul i64 %arg.Y.6.0, %71
  %scevgep107 = getelementptr double, double* %.408.fca.4.load, i64 1
  %scevgep108 = getelementptr double, double* %scevgep107, i64 %loop.index.378.us.us.unr.ph
  %73 = mul i64 %arg.Y.6.0, %loop.index.378.us.us.unr.ph
  br label %for.body.3.endif.endif.endif.us.us

for.body.3.endif.endif.endif.us.preheader:        ; preds = %for.body.3.endif.endif.endif.lr.ph.split.us
  %min.iters.check = icmp ult i64 %.516150, 4
  br i1 %min.iters.check, label %for.body.3.endif.endif.endif.us.preheader220, label %min.iters.checked

for.body.3.endif.endif.endif.us.preheader220:     ; preds = %middle.block, %vector.memcheck, %min.iters.checked, %for.body.3.endif.endif.endif.us.preheader
  %loop.index.378.us.ph = phi i64 [ 0, %vector.memcheck ], [ 0, %min.iters.checked ], [ 0, %for.body.3.endif.endif.endif.us.preheader ], [ %n.vec, %middle.block ]
  %74 = sub i64 %.516150, %loop.index.378.us.ph
  %75 = add i64 %.516150, -1
  %xtraiter240 = and i64 %74, 1
  %lcmp.mod241 = icmp eq i64 %xtraiter240, 0
  br i1 %lcmp.mod241, label %for.body.3.endif.endif.endif.us.prol.loopexit, label %for.body.3.endif.endif.endif.us.prol

for.body.3.endif.endif.endif.us.prol:             ; preds = %for.body.3.endif.endif.endif.us.preheader220
  %.567.us.prol = getelementptr double, double* %.408.fca.4.load, i64 %loop.index.378.us.ph
  %.568.us.prol = load double, double* %.567.us.prol, align 8
  %.578.us.prol = load double, double* %arg.Y.4, align 8
  %76 = fmul double %.568.us.prol, %.578.us.prol
  %.29.i.us.prol = fsub double -0.000000e+00, %76
  %.43.le.i.us.prol = call double @llvm.exp.f64(double %.29.i.us.prol) #5
  %phitmp.le.i.us.prol = fadd double %.43.le.i.us.prol, 1.000000e+00
  %.70.i.us.prol = fdiv double 1.000000e+00, %phitmp.le.i.us.prol
  %.83.i.us.prol = fadd double %.70.i.us.prol, -1.000000e+00
  %.93.i.us.prol = fmul double %.578.us.prol, %.83.i.us.prol
  %.597.us.prol = getelementptr double, double* %.6.i5762, i64 %loop.index.378.us.ph
  store double %.93.i.us.prol, double* %.597.us.prol, align 8
  %.599.us.prol = or i64 %loop.index.378.us.ph, 1
  br label %for.body.3.endif.endif.endif.us.prol.loopexit

for.body.3.endif.endif.endif.us.prol.loopexit:    ; preds = %for.body.3.endif.endif.endif.us.prol, %for.body.3.endif.endif.endif.us.preheader220
  %loop.index.378.us.unr.ph = phi i64 [ %.599.us.prol, %for.body.3.endif.endif.endif.us.prol ], [ %loop.index.378.us.ph, %for.body.3.endif.endif.endif.us.preheader220 ]
  %77 = icmp eq i64 %75, %loop.index.378.us.ph
  br i1 %77, label %for.end.3, label %for.body.3.endif.endif.endif.us.preheader220.new

for.body.3.endif.endif.endif.us.preheader220.new: ; preds = %for.body.3.endif.endif.endif.us.prol.loopexit
  %78 = sub i64 %.516150, %loop.index.378.us.unr.ph
  %scevgep88 = getelementptr double, double* %.6.i5762, i64 1
  %scevgep89 = getelementptr double, double* %scevgep88, i64 %loop.index.378.us.unr.ph
  %scevgep93 = getelementptr double, double* %.408.fca.4.load, i64 1
  %scevgep94 = getelementptr double, double* %scevgep93, i64 %loop.index.378.us.unr.ph
  br label %for.body.3.endif.endif.endif.us

min.iters.checked:                                ; preds = %for.body.3.endif.endif.endif.us.preheader
  %n.vec = and i64 %.516150, -4
  %cmp.zero = icmp eq i64 %n.vec, 0
  br i1 %cmp.zero, label %for.body.3.endif.endif.endif.us.preheader220, label %vector.memcheck

vector.memcheck:                                  ; preds = %min.iters.checked
  %scevgep = getelementptr double, double* %.6.i5762, i64 %.516150
  %scevgep179 = getelementptr double, double* %.408.fca.4.load, i64 %.516150
  %bound0 = icmp ult double* %.6.i5762, %scevgep179
  %bound1 = icmp ult double* %.408.fca.4.load, %scevgep
  %found.conflict = and i1 %bound0, %bound1
  %bound0182 = icmp ult double* %.6.i5762, %arg.Y.4
  %bound1183 = icmp ugt double* %scevgep, %arg.Y.4
  %found.conflict184 = and i1 %bound0182, %bound1183
  %conflict.rdx = or i1 %found.conflict, %found.conflict184
  br i1 %conflict.rdx, label %for.body.3.endif.endif.endif.us.preheader220, label %vector.body.preheader

vector.body.preheader:                            ; preds = %vector.memcheck
  %79 = add i64 %n.vec, -4
  %80 = lshr exact i64 %79, 2
  %81 = and i64 %80, 1
  %lcmp.mod239 = icmp eq i64 %81, 0
  br i1 %lcmp.mod239, label %vector.body.prol, label %vector.body.prol.loopexit

vector.body.prol:                                 ; preds = %vector.body.preheader
  %82 = bitcast double* %.408.fca.4.load to <4 x double>*
  %wide.load.prol = load <4 x double>, <4 x double>* %82, align 8, !alias.scope !4
  %83 = load double, double* %arg.Y.4, align 8, !alias.scope !7
  %84 = insertelement <4 x double> undef, double %83, i32 0
  %85 = shufflevector <4 x double> %84, <4 x double> undef, <4 x i32> zeroinitializer
  %86 = fmul <4 x double> %wide.load.prol, %85
  %87 = fsub <4 x double> <double -0.000000e+00, double -0.000000e+00, double -0.000000e+00, double -0.000000e+00>, %86
  %88 = call <4 x double> @llvm.exp.v4f64(<4 x double> %87)
  %89 = fadd <4 x double> %88, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %90 = fdiv <4 x double> <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>, %89
  %91 = fadd <4 x double> %90, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %92 = fmul <4 x double> %85, %91
  %93 = bitcast double* %.6.i5762 to <4 x double>*
  store <4 x double> %92, <4 x double>* %93, align 8, !alias.scope !9, !noalias !11
  br label %vector.body.prol.loopexit

vector.body.prol.loopexit:                        ; preds = %vector.body.prol, %vector.body.preheader
  %index.unr.ph = phi i64 [ 4, %vector.body.prol ], [ 0, %vector.body.preheader ]
  %94 = icmp eq i64 %80, 0
  br i1 %94, label %middle.block, label %vector.body.preheader.new

vector.body.preheader.new:                        ; preds = %vector.body.prol.loopexit
  %95 = load double, double* %arg.Y.4, align 8, !alias.scope !7
  %96 = insertelement <4 x double> undef, double %95, i32 0
  %97 = shufflevector <4 x double> %96, <4 x double> undef, <4 x i32> zeroinitializer
  %98 = load double, double* %arg.Y.4, align 8, !alias.scope !7
  %99 = insertelement <4 x double> undef, double %98, i32 0
  %100 = shufflevector <4 x double> %99, <4 x double> undef, <4 x i32> zeroinitializer
  %101 = sub i64 %n.vec, %index.unr.ph
  %scevgep74 = getelementptr double, double* %.6.i5762, i64 4
  %scevgep75 = getelementptr double, double* %scevgep74, i64 %index.unr.ph
  %scevgep80 = getelementptr double, double* %.408.fca.4.load, i64 4
  %scevgep81 = getelementptr double, double* %scevgep80, i64 %index.unr.ph
  br label %vector.body

vector.body:                                      ; preds = %vector.body, %vector.body.preheader.new
  %lsr.iv82 = phi double* [ %scevgep83, %vector.body ], [ %scevgep81, %vector.body.preheader.new ]
  %lsr.iv76 = phi double* [ %scevgep77, %vector.body ], [ %scevgep75, %vector.body.preheader.new ]
  %lsr.iv72 = phi i64 [ %lsr.iv.next73, %vector.body ], [ %101, %vector.body.preheader.new ]
  %lsr.iv8284 = bitcast double* %lsr.iv82 to <4 x double>*
  %lsr.iv7678 = bitcast double* %lsr.iv76 to <4 x double>*
  %scevgep85 = getelementptr <4 x double>, <4 x double>* %lsr.iv8284, i64 -1
  %wide.load = load <4 x double>, <4 x double>* %scevgep85, align 8, !alias.scope !4
  %102 = fmul <4 x double> %wide.load, %97
  %103 = fsub <4 x double> <double -0.000000e+00, double -0.000000e+00, double -0.000000e+00, double -0.000000e+00>, %102
  %104 = call <4 x double> @llvm.exp.v4f64(<4 x double> %103)
  %105 = fadd <4 x double> %104, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %106 = fdiv <4 x double> <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>, %105
  %107 = fadd <4 x double> %106, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %108 = fmul <4 x double> %97, %107
  %scevgep79 = getelementptr <4 x double>, <4 x double>* %lsr.iv7678, i64 -1
  store <4 x double> %108, <4 x double>* %scevgep79, align 8, !alias.scope !9, !noalias !11
  %wide.load.1 = load <4 x double>, <4 x double>* %lsr.iv8284, align 8, !alias.scope !4
  %109 = fmul <4 x double> %wide.load.1, %100
  %110 = fsub <4 x double> <double -0.000000e+00, double -0.000000e+00, double -0.000000e+00, double -0.000000e+00>, %109
  %111 = call <4 x double> @llvm.exp.v4f64(<4 x double> %110)
  %112 = fadd <4 x double> %111, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %113 = fdiv <4 x double> <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>, %112
  %114 = fadd <4 x double> %113, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %115 = fmul <4 x double> %100, %114
  store <4 x double> %115, <4 x double>* %lsr.iv7678, align 8, !alias.scope !9, !noalias !11
  %lsr.iv.next73 = add i64 %lsr.iv72, -8
  %scevgep77 = getelementptr double, double* %lsr.iv76, i64 8
  %scevgep83 = getelementptr double, double* %lsr.iv82, i64 8
  %116 = icmp eq i64 %lsr.iv.next73, 0
  br i1 %116, label %middle.block, label %vector.body, !llvm.loop !12

middle.block:                                     ; preds = %vector.body, %vector.body.prol.loopexit
  %cmp.n = icmp eq i64 %.516150, %n.vec
  br i1 %cmp.n, label %for.end.3, label %for.body.3.endif.endif.endif.us.preheader220

for.body.3.endif.endif.endif.us.us:               ; preds = %for.body.3.endif.endif.endif.us.us, %for.body.3.endif.endif.endif.us.us.preheader.new
  %lsr.iv109 = phi double* [ %scevgep110, %for.body.3.endif.endif.endif.us.us ], [ %scevgep108, %for.body.3.endif.endif.endif.us.us.preheader.new ]
  %lsr.iv105 = phi i64 [ %lsr.iv.next106, %for.body.3.endif.endif.endif.us.us ], [ %69, %for.body.3.endif.endif.endif.us.us.preheader.new ]
  %lsr.iv102 = phi double* [ %scevgep103, %for.body.3.endif.endif.endif.us.us ], [ %scevgep101, %for.body.3.endif.endif.endif.us.us.preheader.new ]
  %lsr.iv98 = phi i64 [ %lsr.iv.next99, %for.body.3.endif.endif.endif.us.us ], [ %70, %for.body.3.endif.endif.endif.us.us.preheader.new ]
  %scevgep111 = getelementptr double, double* %lsr.iv109, i64 -1
  %.568.us.us = load double, double* %scevgep111, align 8
  %117 = add i64 %73, %lsr.iv105
  %.577.us.us = inttoptr i64 %117 to double*
  %.578.us.us = load double, double* %.577.us.us, align 8
  %118 = fmul double %.568.us.us, %.578.us.us
  %.29.i.us.us = fsub double -0.000000e+00, %118
  %.43.le.i.us.us = call double @llvm.exp.f64(double %.29.i.us.us) #5
  %phitmp.le.i.us.us = fadd double %.43.le.i.us.us, 1.000000e+00
  %.70.i.us.us = fdiv double 1.000000e+00, %phitmp.le.i.us.us
  %.83.i.us.us = fadd double %.70.i.us.us, -1.000000e+00
  %.93.i.us.us = fmul double %.578.us.us, %.83.i.us.us
  %scevgep104 = getelementptr double, double* %lsr.iv102, i64 -1
  store double %.93.i.us.us, double* %scevgep104, align 8
  %.568.us.us.1 = load double, double* %lsr.iv109, align 8
  %119 = add i64 %72, %lsr.iv105
  %.577.us.us.1 = inttoptr i64 %119 to double*
  %.578.us.us.1 = load double, double* %.577.us.us.1, align 8
  %120 = fmul double %.568.us.us.1, %.578.us.us.1
  %.29.i.us.us.1 = fsub double -0.000000e+00, %120
  %.43.le.i.us.us.1 = call double @llvm.exp.f64(double %.29.i.us.us.1) #5
  %phitmp.le.i.us.us.1 = fadd double %.43.le.i.us.us.1, 1.000000e+00
  %.70.i.us.us.1 = fdiv double 1.000000e+00, %phitmp.le.i.us.us.1
  %.83.i.us.us.1 = fadd double %.70.i.us.us.1, -1.000000e+00
  %.93.i.us.us.1 = fmul double %.578.us.us.1, %.83.i.us.us.1
  store double %.93.i.us.us.1, double* %lsr.iv102, align 8
  %lsr.iv.next99 = add i64 %lsr.iv98, -2
  %scevgep103 = getelementptr double, double* %lsr.iv102, i64 2
  %lsr.iv.next106 = add i64 %lsr.iv105, %5
  %scevgep110 = getelementptr double, double* %lsr.iv109, i64 2
  %exitcond139.1 = icmp eq i64 %lsr.iv.next99, 0
  br i1 %exitcond139.1, label %for.end.3, label %for.body.3.endif.endif.endif.us.us

for.body.3.endif.endif.endif.us:                  ; preds = %for.body.3.endif.endif.endif.us, %for.body.3.endif.endif.endif.us.preheader220.new
  %lsr.iv95 = phi double* [ %scevgep96, %for.body.3.endif.endif.endif.us ], [ %scevgep94, %for.body.3.endif.endif.endif.us.preheader220.new ]
  %lsr.iv90 = phi double* [ %scevgep91, %for.body.3.endif.endif.endif.us ], [ %scevgep89, %for.body.3.endif.endif.endif.us.preheader220.new ]
  %lsr.iv86 = phi i64 [ %lsr.iv.next87, %for.body.3.endif.endif.endif.us ], [ %78, %for.body.3.endif.endif.endif.us.preheader220.new ]
  %scevgep97 = getelementptr double, double* %lsr.iv95, i64 -1
  %.568.us = load double, double* %scevgep97, align 8
  %.578.us = load double, double* %arg.Y.4, align 8
  %121 = fmul double %.568.us, %.578.us
  %.29.i.us = fsub double -0.000000e+00, %121
  %.43.le.i.us = call double @llvm.exp.f64(double %.29.i.us) #5
  %phitmp.le.i.us = fadd double %.43.le.i.us, 1.000000e+00
  %.70.i.us = fdiv double 1.000000e+00, %phitmp.le.i.us
  %.83.i.us = fadd double %.70.i.us, -1.000000e+00
  %.93.i.us = fmul double %.578.us, %.83.i.us
  %scevgep92 = getelementptr double, double* %lsr.iv90, i64 -1
  store double %.93.i.us, double* %scevgep92, align 8
  %.568.us.1 = load double, double* %lsr.iv95, align 8
  %.578.us.1 = load double, double* %arg.Y.4, align 8
  %122 = fmul double %.568.us.1, %.578.us.1
  %.29.i.us.1 = fsub double -0.000000e+00, %122
  %.43.le.i.us.1 = call double @llvm.exp.f64(double %.29.i.us.1) #5
  %phitmp.le.i.us.1 = fadd double %.43.le.i.us.1, 1.000000e+00
  %.70.i.us.1 = fdiv double 1.000000e+00, %phitmp.le.i.us.1
  %.83.i.us.1 = fadd double %.70.i.us.1, -1.000000e+00
  %.93.i.us.1 = fmul double %.578.us.1, %.83.i.us.1
  store double %.93.i.us.1, double* %lsr.iv90, align 8
  %lsr.iv.next87 = add i64 %lsr.iv86, -2
  %scevgep91 = getelementptr double, double* %lsr.iv90, i64 2
  %scevgep96 = getelementptr double, double* %lsr.iv95, i64 2
  %exitcond138.1 = icmp eq i64 %lsr.iv.next87, 0
  br i1 %exitcond138.1, label %for.end.3, label %for.body.3.endif.endif.endif.us, !llvm.loop !15

for.body.3.endif.endif.endif.lr.ph.split:         ; preds = %for.body.3.endif.endif.endif.lr.ph
  %123 = icmp ugt i64 %arg.Y.5.0, 1
  br i1 %123, label %for.body.3.endif.endif.endif.us79.preheader, label %for.body.3.endif.endif.endif.preheader

for.body.3.endif.endif.endif.us79.preheader:      ; preds = %for.body.3.endif.endif.endif.lr.ph.split
  %xtraiter236 = and i64 %.516150, 1
  %lcmp.mod237 = icmp eq i64 %xtraiter236, 0
  br i1 %lcmp.mod237, label %for.body.3.endif.endif.endif.us79.prol.loopexit, label %for.body.3.endif.endif.endif.us79.prol

for.body.3.endif.endif.endif.us79.prol:           ; preds = %for.body.3.endif.endif.endif.us79.preheader
  %.568.us82.prol = load double, double* %.408.fca.4.load, align 8
  %.578.us87.prol = load double, double* %arg.Y.4, align 8
  %124 = fmul double %.568.us82.prol, %.578.us87.prol
  %.29.i.us88.prol = fsub double -0.000000e+00, %124
  %.43.le.i.us89.prol = call double @llvm.exp.f64(double %.29.i.us88.prol) #5
  %phitmp.le.i.us90.prol = fadd double %.43.le.i.us89.prol, 1.000000e+00
  %.70.i.us91.prol = fdiv double 1.000000e+00, %phitmp.le.i.us90.prol
  %.83.i.us92.prol = fadd double %.70.i.us91.prol, -1.000000e+00
  %.93.i.us93.prol = fmul double %.578.us87.prol, %.83.i.us92.prol
  store double %.93.i.us93.prol, double* %.6.i5762, align 8
  br label %for.body.3.endif.endif.endif.us79.prol.loopexit

for.body.3.endif.endif.endif.us79.prol.loopexit:  ; preds = %for.body.3.endif.endif.endif.us79.prol, %for.body.3.endif.endif.endif.us79.preheader
  %loop.index.378.us80.unr.ph = phi i64 [ 1, %for.body.3.endif.endif.endif.us79.prol ], [ 0, %for.body.3.endif.endif.endif.us79.preheader ]
  %125 = icmp eq i64 %.516150, 1
  br i1 %125, label %for.end.3, label %for.body.3.endif.endif.endif.us79.preheader.new

for.body.3.endif.endif.endif.us79.preheader.new:  ; preds = %for.body.3.endif.endif.endif.us79.prol.loopexit
  %126 = ptrtoint double* %arg.Y.4 to i64
  %127 = sub i64 %.516150, %loop.index.378.us80.unr.ph
  %scevgep65 = getelementptr double, double* %.6.i5762, i64 1
  %scevgep66 = getelementptr double, double* %scevgep65, i64 %loop.index.378.us80.unr.ph
  %128 = add i64 %loop.index.378.us80.unr.ph, 1
  %129 = mul i64 %arg.Y.6.0, %128
  %130 = mul i64 %arg.Y.6.0, %loop.index.378.us80.unr.ph
  br label %for.body.3.endif.endif.endif.us79

for.body.3.endif.endif.endif.preheader:           ; preds = %for.body.3.endif.endif.endif.lr.ph.split
  %min.iters.check188 = icmp ult i64 %.516150, 4
  br i1 %min.iters.check188, label %for.body.3.endif.endif.endif.preheader223, label %min.iters.checked189

for.body.3.endif.endif.endif.preheader223:        ; preds = %middle.block186, %vector.memcheck208, %min.iters.checked189, %for.body.3.endif.endif.endif.preheader
  %loop.index.378.ph = phi i64 [ 0, %vector.memcheck208 ], [ 0, %min.iters.checked189 ], [ 0, %for.body.3.endif.endif.endif.preheader ], [ %n.vec191, %middle.block186 ]
  %131 = sub i64 %.516150, %loop.index.378.ph
  %132 = add i64 %.516150, -1
  %xtraiter234 = and i64 %131, 1
  %lcmp.mod235 = icmp eq i64 %xtraiter234, 0
  br i1 %lcmp.mod235, label %for.body.3.endif.endif.endif.prol.loopexit, label %for.body.3.endif.endif.endif.prol

for.body.3.endif.endif.endif.prol:                ; preds = %for.body.3.endif.endif.endif.preheader223
  %.568.prol = load double, double* %.408.fca.4.load, align 8
  %.578.prol = load double, double* %arg.Y.4, align 8
  %133 = fmul double %.568.prol, %.578.prol
  %.29.i.prol = fsub double -0.000000e+00, %133
  %.43.le.i.prol = call double @llvm.exp.f64(double %.29.i.prol) #5
  %phitmp.le.i.prol = fadd double %.43.le.i.prol, 1.000000e+00
  %.70.i.prol = fdiv double 1.000000e+00, %phitmp.le.i.prol
  %.83.i.prol = fadd double %.70.i.prol, -1.000000e+00
  %.93.i.prol = fmul double %.578.prol, %.83.i.prol
  %.597.prol = getelementptr double, double* %.6.i5762, i64 %loop.index.378.ph
  store double %.93.i.prol, double* %.597.prol, align 8
  %.599.prol = or i64 %loop.index.378.ph, 1
  br label %for.body.3.endif.endif.endif.prol.loopexit

for.body.3.endif.endif.endif.prol.loopexit:       ; preds = %for.body.3.endif.endif.endif.prol, %for.body.3.endif.endif.endif.preheader223
  %loop.index.378.unr.ph = phi i64 [ %.599.prol, %for.body.3.endif.endif.endif.prol ], [ %loop.index.378.ph, %for.body.3.endif.endif.endif.preheader223 ]
  %134 = icmp eq i64 %132, %loop.index.378.ph
  br i1 %134, label %for.end.3, label %for.body.3.endif.endif.endif.preheader223.new

for.body.3.endif.endif.endif.preheader223.new:    ; preds = %for.body.3.endif.endif.endif.prol.loopexit
  %135 = sub i64 %.516150, %loop.index.378.unr.ph
  %scevgep58 = getelementptr double, double* %.6.i5762, i64 1
  %scevgep59 = getelementptr double, double* %scevgep58, i64 %loop.index.378.unr.ph
  br label %for.body.3.endif.endif.endif

min.iters.checked189:                             ; preds = %for.body.3.endif.endif.endif.preheader
  %n.vec191 = and i64 %.516150, -4
  %cmp.zero192 = icmp eq i64 %n.vec191, 0
  br i1 %cmp.zero192, label %for.body.3.endif.endif.endif.preheader223, label %vector.memcheck208

vector.memcheck208:                               ; preds = %min.iters.checked189
  %scevgep194 = getelementptr double, double* %.6.i5762, i64 %.516150
  %bound0198 = icmp ult double* %.6.i5762, %.408.fca.4.load
  %bound1199 = icmp ult double* %.408.fca.4.load, %scevgep194
  %found.conflict200 = and i1 %bound0198, %bound1199
  %bound0203 = icmp ult double* %.6.i5762, %arg.Y.4
  %bound1204 = icmp ugt double* %scevgep194, %arg.Y.4
  %found.conflict205 = and i1 %bound0203, %bound1204
  %conflict.rdx206 = or i1 %found.conflict200, %found.conflict205
  br i1 %conflict.rdx206, label %for.body.3.endif.endif.endif.preheader223, label %vector.body185.preheader

vector.body185.preheader:                         ; preds = %vector.memcheck208
  %136 = add i64 %n.vec191, -4
  %137 = lshr exact i64 %136, 2
  %138 = and i64 %137, 1
  %lcmp.mod233 = icmp eq i64 %138, 0
  br i1 %lcmp.mod233, label %vector.body185.prol, label %vector.body185.prol.loopexit

vector.body185.prol:                              ; preds = %vector.body185.preheader
  %139 = load double, double* %.408.fca.4.load, align 8, !alias.scope !16
  %140 = insertelement <4 x double> undef, double %139, i32 0
  %141 = load double, double* %arg.Y.4, align 8, !alias.scope !19
  %142 = insertelement <4 x double> undef, double %141, i32 0
  %143 = shufflevector <4 x double> %142, <4 x double> undef, <4 x i32> zeroinitializer
  %144 = fmul <4 x double> %140, %142
  %145 = shufflevector <4 x double> %144, <4 x double> undef, <4 x i32> zeroinitializer
  %146 = fsub <4 x double> <double -0.000000e+00, double -0.000000e+00, double -0.000000e+00, double -0.000000e+00>, %145
  %147 = call <4 x double> @llvm.exp.v4f64(<4 x double> %146)
  %148 = fadd <4 x double> %147, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %149 = fdiv <4 x double> <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>, %148
  %150 = fadd <4 x double> %149, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %151 = fmul <4 x double> %143, %150
  %152 = bitcast double* %.6.i5762 to <4 x double>*
  store <4 x double> %151, <4 x double>* %152, align 8, !alias.scope !21, !noalias !23
  br label %vector.body185.prol.loopexit

vector.body185.prol.loopexit:                     ; preds = %vector.body185.prol, %vector.body185.preheader
  %index210.unr.ph = phi i64 [ 4, %vector.body185.prol ], [ 0, %vector.body185.preheader ]
  %153 = icmp eq i64 %137, 0
  br i1 %153, label %middle.block186, label %vector.body185.preheader.new

vector.body185.preheader.new:                     ; preds = %vector.body185.prol.loopexit
  %154 = load double, double* %.408.fca.4.load, align 8, !alias.scope !16
  %155 = insertelement <4 x double> undef, double %154, i32 0
  %156 = load double, double* %arg.Y.4, align 8, !alias.scope !19
  %157 = insertelement <4 x double> undef, double %156, i32 0
  %158 = shufflevector <4 x double> %157, <4 x double> undef, <4 x i32> zeroinitializer
  %159 = fmul <4 x double> %155, %157
  %160 = shufflevector <4 x double> %159, <4 x double> undef, <4 x i32> zeroinitializer
  %161 = fsub <4 x double> <double -0.000000e+00, double -0.000000e+00, double -0.000000e+00, double -0.000000e+00>, %160
  %162 = call <4 x double> @llvm.exp.v4f64(<4 x double> %161)
  %163 = fadd <4 x double> %162, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %164 = fdiv <4 x double> <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>, %163
  %165 = fadd <4 x double> %164, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %166 = fmul <4 x double> %158, %165
  %167 = load double, double* %.408.fca.4.load, align 8, !alias.scope !16
  %168 = insertelement <4 x double> undef, double %167, i32 0
  %169 = load double, double* %arg.Y.4, align 8, !alias.scope !19
  %170 = insertelement <4 x double> undef, double %169, i32 0
  %171 = shufflevector <4 x double> %170, <4 x double> undef, <4 x i32> zeroinitializer
  %172 = fmul <4 x double> %168, %170
  %173 = shufflevector <4 x double> %172, <4 x double> undef, <4 x i32> zeroinitializer
  %174 = fsub <4 x double> <double -0.000000e+00, double -0.000000e+00, double -0.000000e+00, double -0.000000e+00>, %173
  %175 = call <4 x double> @llvm.exp.v4f64(<4 x double> %174)
  %176 = fadd <4 x double> %175, <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>
  %177 = fdiv <4 x double> <double 1.000000e+00, double 1.000000e+00, double 1.000000e+00, double 1.000000e+00>, %176
  %178 = fadd <4 x double> %177, <double -1.000000e+00, double -1.000000e+00, double -1.000000e+00, double -1.000000e+00>
  %179 = fmul <4 x double> %171, %178
  %180 = sub i64 %n.vec191, %index210.unr.ph
  %scevgep50 = getelementptr double, double* %.6.i5762, i64 4
  %scevgep51 = getelementptr double, double* %scevgep50, i64 %index210.unr.ph
  br label %vector.body185

vector.body185:                                   ; preds = %vector.body185, %vector.body185.preheader.new
  %lsr.iv52 = phi double* [ %scevgep53, %vector.body185 ], [ %scevgep51, %vector.body185.preheader.new ]
  %lsr.iv48 = phi i64 [ %lsr.iv.next49, %vector.body185 ], [ %180, %vector.body185.preheader.new ]
  %lsr.iv5254 = bitcast double* %lsr.iv52 to <4 x double>*
  %scevgep55 = getelementptr <4 x double>, <4 x double>* %lsr.iv5254, i64 -1
  store <4 x double> %166, <4 x double>* %scevgep55, align 8, !alias.scope !21, !noalias !23
  store <4 x double> %179, <4 x double>* %lsr.iv5254, align 8, !alias.scope !21, !noalias !23
  %lsr.iv.next49 = add i64 %lsr.iv48, -8
  %scevgep53 = getelementptr double, double* %lsr.iv52, i64 8
  %181 = icmp eq i64 %lsr.iv.next49, 0
  br i1 %181, label %middle.block186, label %vector.body185, !llvm.loop !24

middle.block186:                                  ; preds = %vector.body185, %vector.body185.prol.loopexit
  %cmp.n213 = icmp eq i64 %.516150, %n.vec191
  br i1 %cmp.n213, label %for.end.3, label %for.body.3.endif.endif.endif.preheader223

for.body.3.endif.endif.endif.us79:                ; preds = %for.body.3.endif.endif.endif.us79, %for.body.3.endif.endif.endif.us79.preheader.new
  %lsr.iv70 = phi i64 [ %lsr.iv.next71, %for.body.3.endif.endif.endif.us79 ], [ %126, %for.body.3.endif.endif.endif.us79.preheader.new ]
  %lsr.iv67 = phi double* [ %scevgep68, %for.body.3.endif.endif.endif.us79 ], [ %scevgep66, %for.body.3.endif.endif.endif.us79.preheader.new ]
  %lsr.iv63 = phi i64 [ %lsr.iv.next64, %for.body.3.endif.endif.endif.us79 ], [ %127, %for.body.3.endif.endif.endif.us79.preheader.new ]
  %.568.us82 = load double, double* %.408.fca.4.load, align 8
  %182 = add i64 %130, %lsr.iv70
  %.577.us86 = inttoptr i64 %182 to double*
  %.578.us87 = load double, double* %.577.us86, align 8
  %183 = fmul double %.568.us82, %.578.us87
  %.29.i.us88 = fsub double -0.000000e+00, %183
  %.43.le.i.us89 = call double @llvm.exp.f64(double %.29.i.us88) #5
  %phitmp.le.i.us90 = fadd double %.43.le.i.us89, 1.000000e+00
  %.70.i.us91 = fdiv double 1.000000e+00, %phitmp.le.i.us90
  %.83.i.us92 = fadd double %.70.i.us91, -1.000000e+00
  %.93.i.us93 = fmul double %.578.us87, %.83.i.us92
  %scevgep69 = getelementptr double, double* %lsr.iv67, i64 -1
  store double %.93.i.us93, double* %scevgep69, align 8
  %.568.us82.1 = load double, double* %.408.fca.4.load, align 8
  %184 = add i64 %129, %lsr.iv70
  %.577.us86.1 = inttoptr i64 %184 to double*
  %.578.us87.1 = load double, double* %.577.us86.1, align 8
  %185 = fmul double %.568.us82.1, %.578.us87.1
  %.29.i.us88.1 = fsub double -0.000000e+00, %185
  %.43.le.i.us89.1 = call double @llvm.exp.f64(double %.29.i.us88.1) #5
  %phitmp.le.i.us90.1 = fadd double %.43.le.i.us89.1, 1.000000e+00
  %.70.i.us91.1 = fdiv double 1.000000e+00, %phitmp.le.i.us90.1
  %.83.i.us92.1 = fadd double %.70.i.us91.1, -1.000000e+00
  %.93.i.us93.1 = fmul double %.578.us87.1, %.83.i.us92.1
  store double %.93.i.us93.1, double* %lsr.iv67, align 8
  %lsr.iv.next64 = add i64 %lsr.iv63, -2
  %scevgep68 = getelementptr double, double* %lsr.iv67, i64 2
  %lsr.iv.next71 = add i64 %lsr.iv70, %5
  %exitcond137.1 = icmp eq i64 %lsr.iv.next64, 0
  br i1 %exitcond137.1, label %for.end.3, label %for.body.3.endif.endif.endif.us79

for.end.3:                                        ; preds = %for.body.3.endif.endif.endif, %for.body.3.endif.endif.endif.us79, %middle.block186, %for.body.3.endif.endif.endif.prol.loopexit, %for.body.3.endif.endif.endif.us79.prol.loopexit, %for.body.3.endif.endif.endif.us, %for.body.3.endif.endif.endif.us.us, %middle.block, %for.body.3.endif.endif.endif.us.prol.loopexit, %for.body.3.endif.endif.endif.us.us.prol.loopexit, %for.end.2.endif.endif.endif.endif.endif
  call void @NRT_decref(i8* %.408.fca.0.load)
  %.628 = call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.269, i32 32)
  %.5.i58 = getelementptr i8, i8* %.628, i64 24
  %186 = bitcast i8* %.5.i58 to double**
  %.6.i5963 = load double*, double** %186, align 8
  br i1 %brmerge165.demorgan, label %for.cond.5.preheader.us.preheader, label %for.end.4

for.cond.5.preheader.us.preheader:                ; preds = %for.end.3
  %187 = ptrtoint double* %arg.X.4 to i64
  br label %for.cond.5.preheader.us

for.cond.5.preheader.us:                          ; preds = %for.cond.5.for.end.5_crit_edge.us, %for.cond.5.preheader.us.preheader
  %lsr.iv118 = phi i64 [ %lsr.iv.next119, %for.cond.5.for.end.5_crit_edge.us ], [ %187, %for.cond.5.preheader.us.preheader ]
  %lsr.iv112 = phi double* [ %210, %for.cond.5.for.end.5_crit_edge.us ], [ %.6.i5963, %for.cond.5.preheader.us.preheader ]
  %loop.index.4100.us = phi i64 [ %.687.us, %for.cond.5.for.end.5_crit_edge.us ], [ 0, %for.cond.5.preheader.us.preheader ]
  %188 = icmp eq i64 %xtraiter244, 0
  br i1 %188, label %for.body.5.us.prol.loopexit, label %for.body.5.us.prol.preheader

for.body.5.us.prol.preheader:                     ; preds = %for.cond.5.preheader.us
  br label %for.body.5.us.prol

for.body.5.us.prol:                               ; preds = %for.body.5.us.prol, %for.body.5.us.prol.preheader
  %lsr.iv120 = phi i64 [ %lsr.iv.next121, %for.body.5.us.prol ], [ %lsr.iv118, %for.body.5.us.prol.preheader ]
  %lsr.iv115 = phi double* [ %scevgep116, %for.body.5.us.prol ], [ %lsr.iv112, %for.body.5.us.prol.preheader ]
  %loop.index.598.us.prol = phi i64 [ 0, %for.body.5.us.prol.preheader ], [ %.685.us.prol, %for.body.5.us.prol ]
  %lsr.iv115117 = bitcast double* %lsr.iv115 to i64*
  %189 = inttoptr i64 %lsr.iv120 to i64*
  %.68320.us.prol = load i64, i64* %189, align 8
  store i64 %.68320.us.prol, i64* %lsr.iv115117, align 8
  %.685.us.prol = add nuw nsw i64 %loop.index.598.us.prol, 1
  %scevgep116 = getelementptr double, double* %lsr.iv115, i64 1
  %lsr.iv.next121 = add i64 %lsr.iv120, %arg.X.6.1
  %prol.iter246.cmp = icmp eq i64 %xtraiter, %.685.us.prol
  br i1 %prol.iter246.cmp, label %for.body.5.us.prol.loopexit, label %for.body.5.us.prol, !llvm.loop !25

for.body.5.us.prol.loopexit:                      ; preds = %for.body.5.us.prol, %for.cond.5.preheader.us
  %loop.index.598.us.unr = phi i64 [ 0, %for.cond.5.preheader.us ], [ %.685.us.prol, %for.body.5.us.prol ]
  %190 = icmp ult i64 %0, 7
  br i1 %190, label %for.cond.5.for.end.5_crit_edge.us, label %for.cond.5.preheader.us.new

for.cond.5.preheader.us.new:                      ; preds = %for.body.5.us.prol.loopexit
  %191 = mul i64 %arg.X.6.1, %loop.index.598.us.unr
  %192 = add i64 %lsr.iv118, %191
  br label %for.body.5.us

for.body.5.us:                                    ; preds = %for.body.5.us, %for.cond.5.preheader.us.new
  %lsr.iv122 = phi i64 [ %lsr.iv.next123, %for.body.5.us ], [ %192, %for.cond.5.preheader.us.new ]
  %loop.index.598.us = phi i64 [ %loop.index.598.us.unr, %for.cond.5.preheader.us.new ], [ %.685.us.7, %for.body.5.us ]
  %193 = bitcast double* %lsr.iv112 to i64*
  %194 = inttoptr i64 %lsr.iv122 to i64*
  %.68320.us = load i64, i64* %194, align 8
  %scevgep131 = getelementptr i64, i64* %193, i64 %loop.index.598.us
  store i64 %.68320.us, i64* %scevgep131, align 8
  %195 = add i64 %arg.X.6.1, %lsr.iv122
  %196 = inttoptr i64 %195 to i64*
  %.68320.us.1 = load i64, i64* %196, align 8
  %scevgep132 = getelementptr i64, i64* %193, i64 %loop.index.598.us
  %scevgep133 = getelementptr i64, i64* %scevgep132, i64 1
  store i64 %.68320.us.1, i64* %scevgep133, align 8
  %197 = add i64 %arg.X.6.1, %195
  %198 = inttoptr i64 %197 to i64*
  %.68320.us.2 = load i64, i64* %198, align 8
  %scevgep134 = getelementptr i64, i64* %193, i64 %loop.index.598.us
  %scevgep135 = getelementptr i64, i64* %scevgep134, i64 2
  store i64 %.68320.us.2, i64* %scevgep135, align 8
  %199 = add i64 %arg.X.6.1, %197
  %200 = inttoptr i64 %199 to i64*
  %.68320.us.3 = load i64, i64* %200, align 8
  %scevgep136 = getelementptr i64, i64* %193, i64 %loop.index.598.us
  %scevgep137 = getelementptr i64, i64* %scevgep136, i64 3
  store i64 %.68320.us.3, i64* %scevgep137, align 8
  %201 = add i64 %arg.X.6.1, %199
  %202 = inttoptr i64 %201 to i64*
  %.68320.us.4 = load i64, i64* %202, align 8
  %scevgep125 = getelementptr i64, i64* %193, i64 %loop.index.598.us
  %scevgep126 = getelementptr i64, i64* %scevgep125, i64 4
  store i64 %.68320.us.4, i64* %scevgep126, align 8
  %203 = add i64 %arg.X.6.1, %201
  %204 = inttoptr i64 %203 to i64*
  %.68320.us.5 = load i64, i64* %204, align 8
  %scevgep138 = getelementptr i64, i64* %193, i64 %loop.index.598.us
  %scevgep139 = getelementptr i64, i64* %scevgep138, i64 5
  store i64 %.68320.us.5, i64* %scevgep139, align 8
  %205 = add i64 %arg.X.6.1, %203
  %206 = inttoptr i64 %205 to i64*
  %.68320.us.6 = load i64, i64* %206, align 8
  %scevgep129 = getelementptr i64, i64* %193, i64 %loop.index.598.us
  %scevgep130 = getelementptr i64, i64* %scevgep129, i64 6
  store i64 %.68320.us.6, i64* %scevgep130, align 8
  %207 = add i64 %arg.X.6.1, %205
  %208 = inttoptr i64 %207 to i64*
  %.68320.us.7 = load i64, i64* %208, align 8
  %scevgep127 = getelementptr i64, i64* %193, i64 %loop.index.598.us
  %scevgep128 = getelementptr i64, i64* %scevgep127, i64 7
  store i64 %.68320.us.7, i64* %scevgep128, align 8
  %.685.us.7 = add nsw i64 %loop.index.598.us, 8
  %lsr.iv.next123 = add i64 %lsr.iv122, %3
  %exitcond141.7 = icmp eq i64 %arg.X.5.1, %.685.us.7
  br i1 %exitcond141.7, label %for.cond.5.for.end.5_crit_edge.us, label %for.body.5.us

for.cond.5.for.end.5_crit_edge.us:                ; preds = %for.body.5.us, %for.body.5.us.prol.loopexit
  %209 = bitcast double* %lsr.iv112 to i1*
  %.687.us = add nuw nsw i64 %loop.index.4100.us, 1
  %scevgep114 = getelementptr i1, i1* %209, i64 %2
  %210 = bitcast i1* %scevgep114 to double*
  %lsr.iv.next119 = add i64 %lsr.iv118, %arg.X.6.0
  %exitcond142 = icmp eq i64 %.687.us, %arg.X.5.0
  br i1 %exitcond142, label %for.end.4, label %for.cond.5.preheader.us

for.body.3.endif.endif.endif:                     ; preds = %for.body.3.endif.endif.endif, %for.body.3.endif.endif.endif.preheader223.new
  %lsr.iv60 = phi double* [ %scevgep61, %for.body.3.endif.endif.endif ], [ %scevgep59, %for.body.3.endif.endif.endif.preheader223.new ]
  %lsr.iv56 = phi i64 [ %lsr.iv.next57, %for.body.3.endif.endif.endif ], [ %135, %for.body.3.endif.endif.endif.preheader223.new ]
  %.568 = load double, double* %.408.fca.4.load, align 8
  %.578 = load double, double* %arg.Y.4, align 8
  %211 = fmul double %.568, %.578
  %.29.i = fsub double -0.000000e+00, %211
  %.43.le.i = call double @llvm.exp.f64(double %.29.i) #5
  %phitmp.le.i = fadd double %.43.le.i, 1.000000e+00
  %.70.i = fdiv double 1.000000e+00, %phitmp.le.i
  %.83.i = fadd double %.70.i, -1.000000e+00
  %.93.i = fmul double %.578, %.83.i
  %scevgep62 = getelementptr double, double* %lsr.iv60, i64 -1
  store double %.93.i, double* %scevgep62, align 8
  %.568.1 = load double, double* %.408.fca.4.load, align 8
  %.578.1 = load double, double* %arg.Y.4, align 8
  %212 = fmul double %.568.1, %.578.1
  %.29.i.1 = fsub double -0.000000e+00, %212
  %.43.le.i.1 = call double @llvm.exp.f64(double %.29.i.1) #5
  %phitmp.le.i.1 = fadd double %.43.le.i.1, 1.000000e+00
  %.70.i.1 = fdiv double 1.000000e+00, %phitmp.le.i.1
  %.83.i.1 = fadd double %.70.i.1, -1.000000e+00
  %.93.i.1 = fmul double %.578.1, %.83.i.1
  store double %.93.i.1, double* %lsr.iv60, align 8
  %lsr.iv.next57 = add i64 %lsr.iv56, -2
  %scevgep61 = getelementptr double, double* %lsr.iv60, i64 2
  %exitcond136.1 = icmp eq i64 %lsr.iv.next57, 0
  br i1 %exitcond136.1, label %for.end.3, label %for.body.3.endif.endif.endif, !llvm.loop !26

for.end.4:                                        ; preds = %for.cond.5.for.end.5_crit_edge.us, %for.end.3
  %213 = bitcast { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.690 to i8*
  %214 = bitcast { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.690 to i8**
  call void @llvm.memset.p0i8.i64(i8* nonnull %213, i8 0, i64 56, i32 8, i1 false)
  %.698 = call i32 @"_ZN5numba7targets6linalg12dot_impl$247E5ArrayIdLi1E1C7mutable7alignedE5ArrayIdLi2E1C7mutable7alignedE"({ i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* nonnull %.690, { i8*, i32 }** nonnull %excinfo.5, i8* null, i8* %.521, i8* null, i64 %.516150, i64 8, double* %.6.i5762, i64 %.516150, i64 8, i8* %.628, i8* null, i64 %.267, i64 8, double* %.6.i5963, i64 %arg.X.5.0, i64 %arg.X.5.1, i64 %.268, i64 8)
  %.700 = icmp eq i32 %.698, 0
  %notrhs19 = icmp ne i32 %.698, -2
  %.705 = xor i1 %.700, %notrhs19
  %.708.fca.0.load = load i8*, i8** %214, align 8
  %sunkaddr188 = ptrtoint { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.690 to i64
  %sunkaddr189 = add i64 %sunkaddr188, 32
  %sunkaddr190 = inttoptr i64 %sunkaddr189 to double**
  %.708.fca.4.load = load double*, double** %sunkaddr190, align 8
  %sunkaddr191 = ptrtoint { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.690 to i64
  %sunkaddr192 = add i64 %sunkaddr191, 40
  %sunkaddr193 = inttoptr i64 %sunkaddr192 to i64*
  %.708.fca.5.0.load = load i64, i64* %sunkaddr193, align 8
  br i1 %.705, label %for.end.4.if, label %for.end.4.endif, !prof !3

for.end.4.if:                                     ; preds = %for.end.4
  %215 = bitcast { i8*, i32 }** %excinfo.5 to i64*
  %.69918 = load i64, i64* %215, align 8
  %216 = bitcast { i8*, i32 }** %excinfo to i64*
  store i64 %.69918, i64* %216, align 8
  ret i32 %.698

for.end.4.endif:                                  ; preds = %for.end.4
  %217 = icmp sgt i64 %arg.w.5.0, 0
  call void @NRT_incref(i8* %arg.w.0)
  call void @NRT_decref(i8* %.628)
  call void @NRT_decref(i8* %.521)
  br i1 %217, label %for.body.6.lr.ph, label %for.end.6

for.body.6.lr.ph:                                 ; preds = %for.end.4.endif
  %218 = icmp ugt i64 %arg.w.5.0, 1
  br i1 %218, label %for.body.6.lr.ph.split.us, label %for.body.6.lr.ph.split

for.body.6.lr.ph.split.us:                        ; preds = %for.body.6.lr.ph
  %219 = icmp ugt i64 %.708.fca.5.0.load, 1
  br i1 %219, label %for.body.6.us.us.preheader, label %for.body.6.us.preheader

for.body.6.us.preheader:                          ; preds = %for.body.6.lr.ph.split.us
  %220 = icmp eq i64 %xtraiter253, 0
  br i1 %220, label %for.body.6.us.prol.loopexit, label %for.body.6.us.prol.preheader

for.body.6.us.prol.preheader:                     ; preds = %for.body.6.us.preheader
  %221 = ptrtoint double* %arg.w.4 to i64
  br label %for.body.6.us.prol

for.body.6.us.prol:                               ; preds = %for.body.6.us.prol, %for.body.6.us.prol.preheader
  %lsr.iv160 = phi i64 [ %lsr.iv.next161, %for.body.6.us.prol ], [ %221, %for.body.6.us.prol.preheader ]
  %loop.index.6104.us.prol = phi i64 [ %.807.us.prol, %for.body.6.us.prol ], [ 0, %for.body.6.us.prol.preheader ]
  %.790.us.prol = inttoptr i64 %lsr.iv160 to double*
  %.791.us.prol = load double, double* %.790.us.prol, align 8
  %.800.us.prol = load double, double* %.708.fca.4.load, align 8
  %.801.us.prol = fsub double %.791.us.prol, %.800.us.prol
  store double %.801.us.prol, double* %.790.us.prol, align 8
  %.807.us.prol = add nuw nsw i64 %loop.index.6104.us.prol, 1
  %lsr.iv.next161 = add i64 %lsr.iv160, %arg.w.6.0
  %prol.iter255.cmp = icmp eq i64 %xtraiter247, %.807.us.prol
  br i1 %prol.iter255.cmp, label %for.body.6.us.prol.loopexit, label %for.body.6.us.prol, !llvm.loop !27

for.body.6.us.prol.loopexit:                      ; preds = %for.body.6.us.prol, %for.body.6.us.preheader
  %loop.index.6104.us.unr = phi i64 [ 0, %for.body.6.us.preheader ], [ %.807.us.prol, %for.body.6.us.prol ]
  %222 = icmp ult i64 %1, 3
  br i1 %222, label %for.end.6, label %for.body.6.us.preheader.new

for.body.6.us.preheader.new:                      ; preds = %for.body.6.us.prol.loopexit
  %223 = ptrtoint double* %arg.w.4 to i64
  %224 = sub i64 %arg.w.5.0, %loop.index.6104.us.unr
  %225 = add i64 %loop.index.6104.us.unr, 3
  %226 = mul i64 %arg.w.6.0, %225
  %227 = add i64 %loop.index.6104.us.unr, 2
  %228 = mul i64 %arg.w.6.0, %227
  %229 = add i64 %loop.index.6104.us.unr, 1
  %230 = mul i64 %arg.w.6.0, %229
  %231 = mul i64 %arg.w.6.0, %loop.index.6104.us.unr
  br label %for.body.6.us

for.body.6.us.us.preheader:                       ; preds = %for.body.6.lr.ph.split.us
  %232 = icmp eq i64 %xtraiter256, 0
  br i1 %232, label %for.body.6.us.us.prol.loopexit, label %for.body.6.us.us.prol.preheader

for.body.6.us.us.prol.preheader:                  ; preds = %for.body.6.us.us.preheader
  %233 = ptrtoint double* %arg.w.4 to i64
  br label %for.body.6.us.us.prol

for.body.6.us.us.prol:                            ; preds = %for.body.6.us.us.prol, %for.body.6.us.us.prol.preheader
  %lsr.iv167 = phi i64 [ %lsr.iv.next168, %for.body.6.us.us.prol ], [ %233, %for.body.6.us.us.prol.preheader ]
  %loop.index.6104.us.us.prol = phi i64 [ %.807.us.us.prol, %for.body.6.us.us.prol ], [ 0, %for.body.6.us.us.prol.preheader ]
  %.790.us.us.prol = inttoptr i64 %lsr.iv167 to double*
  %.791.us.us.prol = load double, double* %.790.us.us.prol, align 8
  %scevgep166 = getelementptr double, double* %.708.fca.4.load, i64 %loop.index.6104.us.us.prol
  %.800.us.us.prol = load double, double* %scevgep166, align 8
  %.801.us.us.prol = fsub double %.791.us.us.prol, %.800.us.us.prol
  store double %.801.us.us.prol, double* %.790.us.us.prol, align 8
  %.807.us.us.prol = add nuw nsw i64 %loop.index.6104.us.us.prol, 1
  %lsr.iv.next168 = add i64 %lsr.iv167, %arg.w.6.0
  %prol.iter258.cmp = icmp eq i64 %xtraiter247, %.807.us.us.prol
  br i1 %prol.iter258.cmp, label %for.body.6.us.us.prol.loopexit, label %for.body.6.us.us.prol, !llvm.loop !28

for.body.6.us.us.prol.loopexit:                   ; preds = %for.body.6.us.us.prol, %for.body.6.us.us.preheader
  %loop.index.6104.us.us.unr = phi i64 [ 0, %for.body.6.us.us.preheader ], [ %.807.us.us.prol, %for.body.6.us.us.prol ]
  %234 = icmp ult i64 %1, 3
  br i1 %234, label %for.end.6, label %for.body.6.us.us.preheader.new

for.body.6.us.us.preheader.new:                   ; preds = %for.body.6.us.us.prol.loopexit
  %235 = ptrtoint double* %arg.w.4 to i64
  %236 = sub i64 %arg.w.5.0, %loop.index.6104.us.us.unr
  %scevgep171 = getelementptr double, double* %.708.fca.4.load, i64 3
  %scevgep172 = getelementptr double, double* %scevgep171, i64 %loop.index.6104.us.us.unr
  %237 = add i64 %loop.index.6104.us.us.unr, 3
  %238 = mul i64 %arg.w.6.0, %237
  %239 = add i64 %loop.index.6104.us.us.unr, 2
  %240 = mul i64 %arg.w.6.0, %239
  %241 = add i64 %loop.index.6104.us.us.unr, 1
  %242 = mul i64 %arg.w.6.0, %241
  %243 = mul i64 %arg.w.6.0, %loop.index.6104.us.us.unr
  br label %for.body.6.us.us

for.body.6.us.us:                                 ; preds = %for.body.6.us.us, %for.body.6.us.us.preheader.new
  %lsr.iv178 = phi i64 [ %lsr.iv.next179, %for.body.6.us.us ], [ %235, %for.body.6.us.us.preheader.new ]
  %lsr.iv173 = phi double* [ %scevgep174, %for.body.6.us.us ], [ %scevgep172, %for.body.6.us.us.preheader.new ]
  %lsr.iv169 = phi i64 [ %lsr.iv.next170, %for.body.6.us.us ], [ %236, %for.body.6.us.us.preheader.new ]
  %244 = add i64 %243, %lsr.iv178
  %.790.us.us = inttoptr i64 %244 to double*
  %.791.us.us = load double, double* %.790.us.us, align 8
  %scevgep177 = getelementptr double, double* %lsr.iv173, i64 -3
  %.800.us.us = load double, double* %scevgep177, align 8
  %.801.us.us = fsub double %.791.us.us, %.800.us.us
  store double %.801.us.us, double* %.790.us.us, align 8
  %245 = add i64 %242, %lsr.iv178
  %.790.us.us.1 = inttoptr i64 %245 to double*
  %.791.us.us.1 = load double, double* %.790.us.us.1, align 8
  %scevgep176 = getelementptr double, double* %lsr.iv173, i64 -2
  %.800.us.us.1 = load double, double* %scevgep176, align 8
  %.801.us.us.1 = fsub double %.791.us.us.1, %.800.us.us.1
  store double %.801.us.us.1, double* %.790.us.us.1, align 8
  %246 = add i64 %240, %lsr.iv178
  %.790.us.us.2 = inttoptr i64 %246 to double*
  %.791.us.us.2 = load double, double* %.790.us.us.2, align 8
  %scevgep175 = getelementptr double, double* %lsr.iv173, i64 -1
  %.800.us.us.2 = load double, double* %scevgep175, align 8
  %.801.us.us.2 = fsub double %.791.us.us.2, %.800.us.us.2
  store double %.801.us.us.2, double* %.790.us.us.2, align 8
  %247 = add i64 %238, %lsr.iv178
  %.790.us.us.3 = inttoptr i64 %247 to double*
  %.791.us.us.3 = load double, double* %.790.us.us.3, align 8
  %.800.us.us.3 = load double, double* %lsr.iv173, align 8
  %.801.us.us.3 = fsub double %.791.us.us.3, %.800.us.us.3
  store double %.801.us.us.3, double* %.790.us.us.3, align 8
  %lsr.iv.next170 = add i64 %lsr.iv169, -4
  %scevgep174 = getelementptr double, double* %lsr.iv173, i64 4
  %lsr.iv.next179 = add i64 %lsr.iv178, %6
  %exitcond146.3 = icmp eq i64 %lsr.iv.next170, 0
  br i1 %exitcond146.3, label %for.end.6, label %for.body.6.us.us

for.body.6.us:                                    ; preds = %for.body.6.us, %for.body.6.us.preheader.new
  %lsr.iv164 = phi i64 [ %lsr.iv.next165, %for.body.6.us ], [ %223, %for.body.6.us.preheader.new ]
  %lsr.iv162 = phi i64 [ %lsr.iv.next163, %for.body.6.us ], [ %224, %for.body.6.us.preheader.new ]
  %248 = add i64 %231, %lsr.iv164
  %.790.us = inttoptr i64 %248 to double*
  %.791.us = load double, double* %.790.us, align 8
  %.800.us = load double, double* %.708.fca.4.load, align 8
  %.801.us = fsub double %.791.us, %.800.us
  store double %.801.us, double* %.790.us, align 8
  %249 = add i64 %230, %lsr.iv164
  %.790.us.1 = inttoptr i64 %249 to double*
  %.791.us.1 = load double, double* %.790.us.1, align 8
  %.800.us.1 = load double, double* %.708.fca.4.load, align 8
  %.801.us.1 = fsub double %.791.us.1, %.800.us.1
  store double %.801.us.1, double* %.790.us.1, align 8
  %250 = add i64 %228, %lsr.iv164
  %.790.us.2 = inttoptr i64 %250 to double*
  %.791.us.2 = load double, double* %.790.us.2, align 8
  %.800.us.2 = load double, double* %.708.fca.4.load, align 8
  %.801.us.2 = fsub double %.791.us.2, %.800.us.2
  store double %.801.us.2, double* %.790.us.2, align 8
  %251 = add i64 %226, %lsr.iv164
  %.790.us.3 = inttoptr i64 %251 to double*
  %.791.us.3 = load double, double* %.790.us.3, align 8
  %.800.us.3 = load double, double* %.708.fca.4.load, align 8
  %.801.us.3 = fsub double %.791.us.3, %.800.us.3
  store double %.801.us.3, double* %.790.us.3, align 8
  %lsr.iv.next163 = add i64 %lsr.iv162, -4
  %lsr.iv.next165 = add i64 %lsr.iv164, %6
  %exitcond145.3 = icmp eq i64 %lsr.iv.next163, 0
  br i1 %exitcond145.3, label %for.end.6, label %for.body.6.us

for.body.6.lr.ph.split:                           ; preds = %for.body.6.lr.ph
  %252 = icmp ugt i64 %.708.fca.5.0.load, 1
  br i1 %252, label %for.body.6.us105.preheader, label %for.body.6.preheader

for.body.6.preheader:                             ; preds = %for.body.6.lr.ph.split
  %253 = icmp eq i64 %xtraiter247, 0
  br i1 %253, label %for.body.6.prol.loopexit, label %for.body.6.prol.preheader

for.body.6.prol.preheader:                        ; preds = %for.body.6.preheader
  %254 = ptrtoint double* %arg.w.4 to i64
  br label %for.body.6.prol

for.body.6.prol:                                  ; preds = %for.body.6.prol, %for.body.6.prol.preheader
  %lsr.iv140 = phi i64 [ %lsr.iv.next141, %for.body.6.prol ], [ %254, %for.body.6.prol.preheader ]
  %loop.index.6104.prol = phi i64 [ %.807.prol, %for.body.6.prol ], [ 0, %for.body.6.prol.preheader ]
  %.791.prol = load double, double* %arg.w.4, align 8
  %.800.prol = load double, double* %.708.fca.4.load, align 8
  %.801.prol = fsub double %.791.prol, %.800.prol
  %.805.prol = inttoptr i64 %lsr.iv140 to double*
  store double %.801.prol, double* %.805.prol, align 8
  %.807.prol = add nuw nsw i64 %loop.index.6104.prol, 1
  %lsr.iv.next141 = add i64 %lsr.iv140, %arg.w.6.0
  %prol.iter249.cmp = icmp eq i64 %xtraiter247, %.807.prol
  br i1 %prol.iter249.cmp, label %for.body.6.prol.loopexit, label %for.body.6.prol, !llvm.loop !29

for.body.6.prol.loopexit:                         ; preds = %for.body.6.prol, %for.body.6.preheader
  %loop.index.6104.unr = phi i64 [ 0, %for.body.6.preheader ], [ %.807.prol, %for.body.6.prol ]
  %255 = icmp ult i64 %1, 3
  br i1 %255, label %for.end.6, label %for.body.6.preheader.new

for.body.6.preheader.new:                         ; preds = %for.body.6.prol.loopexit
  %256 = ptrtoint double* %arg.w.4 to i64
  %257 = sub i64 %arg.w.5.0, %loop.index.6104.unr
  %258 = add i64 %loop.index.6104.unr, 3
  %259 = mul i64 %arg.w.6.0, %258
  %260 = add i64 %loop.index.6104.unr, 2
  %261 = mul i64 %arg.w.6.0, %260
  %262 = add i64 %loop.index.6104.unr, 1
  %263 = mul i64 %arg.w.6.0, %262
  %264 = mul i64 %arg.w.6.0, %loop.index.6104.unr
  br label %for.body.6

for.body.6.us105.preheader:                       ; preds = %for.body.6.lr.ph.split
  %265 = icmp eq i64 %xtraiter250, 0
  br i1 %265, label %for.body.6.us105.prol.loopexit, label %for.body.6.us105.prol.preheader

for.body.6.us105.prol.preheader:                  ; preds = %for.body.6.us105.preheader
  %266 = ptrtoint double* %arg.w.4 to i64
  br label %for.body.6.us105.prol

for.body.6.us105.prol:                            ; preds = %for.body.6.us105.prol, %for.body.6.us105.prol.preheader
  %lsr.iv146 = phi i64 [ %lsr.iv.next147, %for.body.6.us105.prol ], [ %266, %for.body.6.us105.prol.preheader ]
  %loop.index.6104.us106.prol = phi i64 [ %.807.us116.prol, %for.body.6.us105.prol ], [ 0, %for.body.6.us105.prol.preheader ]
  %.791.us108.prol = load double, double* %arg.w.4, align 8
  %scevgep148 = getelementptr double, double* %.708.fca.4.load, i64 %loop.index.6104.us106.prol
  %.800.us111.prol = load double, double* %scevgep148, align 8
  %.801.us112.prol = fsub double %.791.us108.prol, %.800.us111.prol
  %.805.us115.prol = inttoptr i64 %lsr.iv146 to double*
  store double %.801.us112.prol, double* %.805.us115.prol, align 8
  %.807.us116.prol = add nuw nsw i64 %loop.index.6104.us106.prol, 1
  %lsr.iv.next147 = add i64 %lsr.iv146, %arg.w.6.0
  %prol.iter252.cmp = icmp eq i64 %xtraiter247, %.807.us116.prol
  br i1 %prol.iter252.cmp, label %for.body.6.us105.prol.loopexit, label %for.body.6.us105.prol, !llvm.loop !30

for.body.6.us105.prol.loopexit:                   ; preds = %for.body.6.us105.prol, %for.body.6.us105.preheader
  %loop.index.6104.us106.unr = phi i64 [ 0, %for.body.6.us105.preheader ], [ %.807.us116.prol, %for.body.6.us105.prol ]
  %267 = icmp ult i64 %1, 3
  br i1 %267, label %for.end.6, label %for.body.6.us105.preheader.new

for.body.6.us105.preheader.new:                   ; preds = %for.body.6.us105.prol.loopexit
  %268 = ptrtoint double* %arg.w.4 to i64
  %269 = sub i64 %arg.w.5.0, %loop.index.6104.us106.unr
  %270 = add i64 %loop.index.6104.us106.unr, 3
  %271 = mul i64 %arg.w.6.0, %270
  %scevgep153 = getelementptr double, double* %.708.fca.4.load, i64 3
  %scevgep154 = getelementptr double, double* %scevgep153, i64 %loop.index.6104.us106.unr
  %272 = add i64 %loop.index.6104.us106.unr, 2
  %273 = mul i64 %arg.w.6.0, %272
  %274 = add i64 %loop.index.6104.us106.unr, 1
  %275 = mul i64 %arg.w.6.0, %274
  %276 = mul i64 %arg.w.6.0, %loop.index.6104.us106.unr
  br label %for.body.6.us105

for.body.6.us105:                                 ; preds = %for.body.6.us105, %for.body.6.us105.preheader.new
  %lsr.iv155 = phi double* [ %scevgep156, %for.body.6.us105 ], [ %scevgep154, %for.body.6.us105.preheader.new ]
  %lsr.iv151 = phi i64 [ %lsr.iv.next152, %for.body.6.us105 ], [ %268, %for.body.6.us105.preheader.new ]
  %lsr.iv149 = phi i64 [ %lsr.iv.next150, %for.body.6.us105 ], [ %269, %for.body.6.us105.preheader.new ]
  %.791.us108 = load double, double* %arg.w.4, align 8
  %scevgep159 = getelementptr double, double* %lsr.iv155, i64 -3
  %.800.us111 = load double, double* %scevgep159, align 8
  %.801.us112 = fsub double %.791.us108, %.800.us111
  %277 = add i64 %276, %lsr.iv151
  %.805.us115 = inttoptr i64 %277 to double*
  store double %.801.us112, double* %.805.us115, align 8
  %.791.us108.1 = load double, double* %arg.w.4, align 8
  %scevgep158 = getelementptr double, double* %lsr.iv155, i64 -2
  %.800.us111.1 = load double, double* %scevgep158, align 8
  %.801.us112.1 = fsub double %.791.us108.1, %.800.us111.1
  %278 = add i64 %275, %lsr.iv151
  %.805.us115.1 = inttoptr i64 %278 to double*
  store double %.801.us112.1, double* %.805.us115.1, align 8
  %.791.us108.2 = load double, double* %arg.w.4, align 8
  %scevgep157 = getelementptr double, double* %lsr.iv155, i64 -1
  %.800.us111.2 = load double, double* %scevgep157, align 8
  %.801.us112.2 = fsub double %.791.us108.2, %.800.us111.2
  %279 = add i64 %273, %lsr.iv151
  %.805.us115.2 = inttoptr i64 %279 to double*
  store double %.801.us112.2, double* %.805.us115.2, align 8
  %.791.us108.3 = load double, double* %arg.w.4, align 8
  %.800.us111.3 = load double, double* %lsr.iv155, align 8
  %.801.us112.3 = fsub double %.791.us108.3, %.800.us111.3
  %280 = add i64 %271, %lsr.iv151
  %.805.us115.3 = inttoptr i64 %280 to double*
  store double %.801.us112.3, double* %.805.us115.3, align 8
  %lsr.iv.next150 = add i64 %lsr.iv149, -4
  %lsr.iv.next152 = add i64 %lsr.iv151, %6
  %scevgep156 = getelementptr double, double* %lsr.iv155, i64 4
  %exitcond144.3 = icmp eq i64 %lsr.iv.next150, 0
  br i1 %exitcond144.3, label %for.end.6, label %for.body.6.us105

for.body.6:                                       ; preds = %for.body.6, %for.body.6.preheader.new
  %lsr.iv144 = phi i64 [ %lsr.iv.next145, %for.body.6 ], [ %256, %for.body.6.preheader.new ]
  %lsr.iv142 = phi i64 [ %lsr.iv.next143, %for.body.6 ], [ %257, %for.body.6.preheader.new ]
  %.791 = load double, double* %arg.w.4, align 8
  %.800 = load double, double* %.708.fca.4.load, align 8
  %.801 = fsub double %.791, %.800
  %281 = add i64 %264, %lsr.iv144
  %.805 = inttoptr i64 %281 to double*
  store double %.801, double* %.805, align 8
  %.791.1 = load double, double* %arg.w.4, align 8
  %.800.1 = load double, double* %.708.fca.4.load, align 8
  %.801.1 = fsub double %.791.1, %.800.1
  %282 = add i64 %263, %lsr.iv144
  %.805.1 = inttoptr i64 %282 to double*
  store double %.801.1, double* %.805.1, align 8
  %.791.2 = load double, double* %arg.w.4, align 8
  %.800.2 = load double, double* %.708.fca.4.load, align 8
  %.801.2 = fsub double %.791.2, %.800.2
  %283 = add i64 %261, %lsr.iv144
  %.805.2 = inttoptr i64 %283 to double*
  store double %.801.2, double* %.805.2, align 8
  %.791.3 = load double, double* %arg.w.4, align 8
  %.800.3 = load double, double* %.708.fca.4.load, align 8
  %.801.3 = fsub double %.791.3, %.800.3
  %284 = add i64 %259, %lsr.iv144
  %.805.3 = inttoptr i64 %284 to double*
  store double %.801.3, double* %.805.3, align 8
  %lsr.iv.next143 = add i64 %lsr.iv142, -4
  %lsr.iv.next145 = add i64 %lsr.iv144, %6
  %exitcond143.3 = icmp eq i64 %lsr.iv.next143, 0
  br i1 %exitcond143.3, label %for.end.6, label %for.body.6

for.end.6:                                        ; preds = %for.body.6, %for.body.6.us105, %for.body.6.us105.prol.loopexit, %for.body.6.prol.loopexit, %for.body.6.us, %for.body.6.us.us, %for.body.6.us.us.prol.loopexit, %for.body.6.us.prol.loopexit, %for.end.4.endif
  call void @NRT_decref(i8* %.708.fca.0.load)
  call void @NRT_decref(i8* %arg.w.0)
  %.145 = icmp sgt i64 %.90.0118, 1
  br i1 %.145, label %B13.if, label %B86
}

declare noalias i8* @NRT_MemInfo_alloc_safe_aligned(i64, i32) local_unnamed_addr

define { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } @"cfunc._ZN25numpy_logistic_regression29numpy_logistic_regression$241E5ArrayIdLi1E1A7mutable7alignedE5ArrayIdLi2E1A7mutable7alignedE5ArrayIdLi1E1A7mutable7alignedEx"({ i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.1, { i8*, i8*, i64, i64, double*, [2 x i64], [2 x i64] } %.2, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.3, i64 %.4) local_unnamed_addr {
entry:
  %.6 = alloca { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, align 8
  %.fca.0.gep1 = bitcast { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.6 to i8**
  %.fca.1.gep = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.6, i64 0, i32 1
  %.fca.2.gep = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.6, i64 0, i32 2
  %.fca.3.gep = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.6, i64 0, i32 3
  %.fca.4.gep = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.6, i64 0, i32 4
  %.fca.5.0.gep = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.6, i64 0, i32 5, i64 0
  %.fca.6.0.gep = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.6, i64 0, i32 6, i64 0
  %excinfo = alloca { i8*, i32 }*, align 8
  %extracted.meminfo = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.1, 0
  %extracted.parent = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.1, 1
  %extracted.nitems = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.1, 2
  %extracted.itemsize = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.1, 3
  %extracted.data = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.1, 4
  %extracted.shape = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.1, 5
  %.8 = extractvalue [1 x i64] %extracted.shape, 0
  %extracted.strides = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.1, 6
  %.9 = extractvalue [1 x i64] %extracted.strides, 0
  %extracted.meminfo.1 = extractvalue { i8*, i8*, i64, i64, double*, [2 x i64], [2 x i64] } %.2, 0
  %extracted.parent.1 = extractvalue { i8*, i8*, i64, i64, double*, [2 x i64], [2 x i64] } %.2, 1
  %extracted.nitems.1 = extractvalue { i8*, i8*, i64, i64, double*, [2 x i64], [2 x i64] } %.2, 2
  %extracted.itemsize.1 = extractvalue { i8*, i8*, i64, i64, double*, [2 x i64], [2 x i64] } %.2, 3
  %extracted.data.1 = extractvalue { i8*, i8*, i64, i64, double*, [2 x i64], [2 x i64] } %.2, 4
  %extracted.shape.1 = extractvalue { i8*, i8*, i64, i64, double*, [2 x i64], [2 x i64] } %.2, 5
  %.10 = extractvalue [2 x i64] %extracted.shape.1, 0
  %.11 = extractvalue [2 x i64] %extracted.shape.1, 1
  %extracted.strides.1 = extractvalue { i8*, i8*, i64, i64, double*, [2 x i64], [2 x i64] } %.2, 6
  %.12 = extractvalue [2 x i64] %extracted.strides.1, 0
  %.13 = extractvalue [2 x i64] %extracted.strides.1, 1
  %extracted.meminfo.2 = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.3, 0
  %extracted.parent.2 = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.3, 1
  %extracted.nitems.2 = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.3, 2
  %extracted.itemsize.2 = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.3, 3
  %extracted.data.2 = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.3, 4
  %extracted.shape.2 = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.3, 5
  %.14 = extractvalue [1 x i64] %extracted.shape.2, 0
  %extracted.strides.2 = extractvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.3, 6
  %.15 = extractvalue [1 x i64] %extracted.strides.2, 0
  %0 = bitcast { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %.6 to i8*
  call void @llvm.memset.p0i8.i64(i8* nonnull %0, i8 0, i64 56, i32 8, i1 false)
  %.16 = call i32 @"_ZN25numpy_logistic_regression29numpy_logistic_regression$241E5ArrayIdLi1E1A7mutable7alignedE5ArrayIdLi2E1A7mutable7alignedE5ArrayIdLi1E1A7mutable7alignedEx"({ i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* nonnull %.6, { i8*, i32 }** nonnull %excinfo, i8* undef, i8* %extracted.meminfo, i8* %extracted.parent, i64 %extracted.nitems, i64 %extracted.itemsize, double* %extracted.data, i64 %.8, i64 %.9, i8* %extracted.meminfo.1, i8* %extracted.parent.1, i64 %extracted.nitems.1, i64 %extracted.itemsize.1, double* %extracted.data.1, i64 %.10, i64 %.11, i64 %.12, i64 %.13, i8* %extracted.meminfo.2, i8* %extracted.parent.2, i64 %extracted.nitems.2, i64 %extracted.itemsize.2, double* %extracted.data.2, i64 %.14, i64 %.15, i64 %.4)
  %.17 = load { i8*, i32 }*, { i8*, i32 }** %excinfo, align 8
  %.18 = icmp eq i32 %.16, 0
  %notrhs = icmp ne i32 %.16, -2
  %.23 = xor i1 %.18, %notrhs
  %.26.fca.0.load = load i8*, i8** %.fca.0.gep1, align 8
  %.26.fca.0.insert = insertvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } undef, i8* %.26.fca.0.load, 0
  %.26.fca.1.load = load i8*, i8** %.fca.1.gep, align 8
  %.26.fca.1.insert = insertvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.26.fca.0.insert, i8* %.26.fca.1.load, 1
  %.26.fca.2.load = load i64, i64* %.fca.2.gep, align 8
  %.26.fca.2.insert = insertvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.26.fca.1.insert, i64 %.26.fca.2.load, 2
  %.26.fca.3.load = load i64, i64* %.fca.3.gep, align 8
  %.26.fca.3.insert = insertvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.26.fca.2.insert, i64 %.26.fca.3.load, 3
  %.26.fca.4.load = load double*, double** %.fca.4.gep, align 8
  %.26.fca.4.insert = insertvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.26.fca.3.insert, double* %.26.fca.4.load, 4
  %.26.fca.5.0.load = load i64, i64* %.fca.5.0.gep, align 8
  %.26.fca.6.0.load = load i64, i64* %.fca.6.0.gep, align 8
  %.32 = insertvalue [1 x i64] undef, i64 %.26.fca.5.0.load, 0
  %.33 = insertvalue [1 x i64] undef, i64 %.26.fca.6.0.load, 0
  %inserted.shape = insertvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %.26.fca.4.insert, [1 x i64] %.32, 5
  %inserted.strides = insertvalue { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %inserted.shape, [1 x i64] %.33, 6
  %.39 = alloca i32, align 4
  br i1 %.23, label %entry.if, label %entry.endif, !prof !3

entry.if:                                         ; preds = %entry
  %.24 = icmp sgt i32 %.16, 0
  call void @numba_gil_ensure(i32* nonnull %.39)
  br i1 %.24, label %entry.if.if, label %entry.if.endif

entry.endif:                                      ; preds = %.41, %entry
  ret { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] } %inserted.strides

.41:                                              ; preds = %entry.if.endif.endif.endif, %entry.if.endif.if, %entry.if.if.if, %entry.if.endif, %entry.if.if
  %.61 = call i8* @PyString_FromString(i8* getelementptr inbounds ([47 x i8], [47 x i8]* @".const.<Numba C callback 'numpy_logistic_regression'>", i64 0, i64 0))
  call void @PyErr_WriteUnraisable(i8* %.61)
  call void @Py_DecRef(i8* %.61)
  call void @numba_gil_release(i32* nonnull %.39)
  br label %entry.endif

entry.if.if:                                      ; preds = %entry.if
  call void @PyErr_Clear()
  %.44 = load { i8*, i32 }, { i8*, i32 }* %.17, align 8
  %.45 = extractvalue { i8*, i32 } %.44, 0
  %.47 = extractvalue { i8*, i32 } %.44, 1
  %.48 = call i8* @numba_unpickle(i8* %.45, i32 %.47)
  %.49 = icmp eq i8* %.48, null
  br i1 %.49, label %.41, label %entry.if.if.if, !prof !3

entry.if.endif:                                   ; preds = %entry.if
  switch i32 %.16, label %entry.if.endif.endif.endif [
    i32 -3, label %entry.if.endif.if
    i32 -1, label %.41
  ]

entry.if.if.if:                                   ; preds = %entry.if.if
  call void @numba_do_raise(i8* nonnull %.48)
  br label %.41

entry.if.endif.if:                                ; preds = %entry.if.endif
  call void @PyErr_SetNone(i8* nonnull @PyExc_StopIteration)
  br label %.41

entry.if.endif.endif.endif:                       ; preds = %entry.if.endif
  call void @PyErr_SetString(i8* nonnull @PyExc_SystemError, i8* getelementptr inbounds ([43 x i8], [43 x i8]* @".const.unknown error when calling native function", i64 0, i64 0))
  br label %.41
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

; Function Attrs: noinline norecurse nounwind
define linkonce_odr void @NRT_incref(i8* %.1) local_unnamed_addr #0 {
.3:
  %.4 = icmp eq i8* %.1, null
  br i1 %.4, label %.3.if, label %.3.endif, !prof !3

.3.if:                                            ; preds = %.3
  ret void

.3.endif:                                         ; preds = %.3
  %.7 = bitcast i8* %.1 to i64*
  %.4.i = atomicrmw add i64* %.7, i64 1 monotonic
  ret void
}

; Function Attrs: noinline
define linkonce_odr void @NRT_decref(i8* %.1) local_unnamed_addr #1 {
.3:
  %.4 = icmp eq i8* %.1, null
  br i1 %.4, label %.3.if, label %.3.endif, !prof !3

.3.if:                                            ; preds = %.3.endif, %.3
  ret void

.3.endif:                                         ; preds = %.3
  %.7 = bitcast i8* %.1 to i64*
  %.4.i = atomicrmw sub i64* %.7, i64 1 monotonic
  %.9 = icmp eq i64 %.4.i, 1
  br i1 %.9, label %.3.endif.if, label %.3.if, !prof !3

.3.endif.if:                                      ; preds = %.3.endif
  tail call void @NRT_MemInfo_call_dtor(i8* nonnull %.1)
  ret void
}

declare void @NRT_MemInfo_call_dtor(i8*) local_unnamed_addr

; Function Attrs: nounwind readnone speculatable
declare double @llvm.exp.f64(double) #2

define linkonce_odr i32 @"_ZN5numba7targets6linalg12dot_impl$242E5ArrayIdLi2E1C7mutable7alignedE5ArrayIdLi1E1C7mutable7alignedE"({ i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* noalias nocapture %retptr, { i8*, i32 }** noalias nocapture %excinfo, i8* noalias nocapture readnone %env, i8* %arg.a.0, i8* nocapture readnone %arg.a.1, i64 %arg.a.2, i64 %arg.a.3, double* %arg.a.4, i64 %arg.a.5.0, i64 %arg.a.5.1, i64 %arg.a.6.0, i64 %arg.a.6.1, i8* %arg.b.0, i8* nocapture readnone %arg.b.1, i64 %arg.b.2, i64 %arg.b.3, double* %arg.b.4, i64 %arg.b.5.0, i64 %arg.b.6.0) local_unnamed_addr {
entry:
  %.289 = alloca double, align 8
  %.291 = alloca double, align 8
  %.301 = alloca i32, align 4
  tail call void @NRT_incref(i8* %arg.a.0)
  tail call void @NRT_incref(i8* %arg.b.0)
  %.138 = icmp slt i64 %arg.a.5.0, 0
  br i1 %.138, label %B0.if, label %B0.endif, !prof !3

B0.if:                                            ; preds = %entry
  store { i8*, i32 }* @.const.picklebuf.26030507072, { i8*, i32 }** %excinfo, align 8
  ret i32 1

B0.endif:                                         ; preds = %entry
  %.145 = shl i64 %arg.a.5.0, 3
  %.146 = tail call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.145, i32 32)
  %.5.i = getelementptr i8, i8* %.146, i64 24
  %0 = bitcast i8* %.5.i to i8**
  %.6.i = load i8*, i8** %0, align 8
  %.133.i = icmp eq i64 %arg.b.5.0, %arg.a.5.1
  br i1 %.133.i, label %B0.endif.endif, label %B0.endif.if

B0.endif.if:                                      ; preds = %B0.endif
  %1 = bitcast { i8*, i32 }** %excinfo to i64*
  store i64 ptrtoint ({ i8*, i32 }* @.const.picklebuf.26031217480 to i64), i64* %1, align 8
  ret i32 1

B0.endif.endif:                                   ; preds = %B0.endif
  %.14.i = icmp sgt i64 %arg.a.5.0, 2147483647
  br i1 %.14.i, label %B0.endif.endif.if, label %B0.endif.endif.endif

B0.endif.endif.if:                                ; preds = %B0.endif.endif
  %2 = bitcast { i8*, i32 }** %excinfo to i64*
  store i64 ptrtoint ({ i8*, i32 }* @.const.picklebuf.26030746440 to i64), i64* %2, align 8
  ret i32 1

B0.endif.endif.endif:                             ; preds = %B0.endif.endif
  %.14.i20 = icmp sgt i64 %arg.b.5.0, 2147483647
  br i1 %.14.i20, label %B0.endif.endif.endif.if, label %B0.endif.endif.endif.endif

B0.endif.endif.endif.if:                          ; preds = %B0.endif.endif.endif
  %3 = bitcast { i8*, i32 }** %excinfo to i64*
  store i64 ptrtoint ({ i8*, i32 }* @.const.picklebuf.26030746440 to i64), i64* %3, align 8
  ret i32 1

B0.endif.endif.endif.endif:                       ; preds = %B0.endif.endif.endif
  store double 1.000000e+00, double* %.289, align 8
  store double 0.000000e+00, double* %.291, align 8
  %.293 = bitcast double* %.289 to i8*
  %.294 = bitcast double* %arg.a.4 to i8*
  %.295 = bitcast double* %arg.b.4 to i8*
  %.296 = bitcast double* %.291 to i8*
  %.298 = call i32 @numba_xxgemv(i8 100, i8 116, i64 %arg.b.5.0, i64 %arg.a.5.0, i8* nonnull %.293, i8* %.294, i64 %arg.b.5.0, i8* %.295, i8* nonnull %.296, i8* %.6.i)
  %.299 = icmp eq i32 %.298, 0
  br i1 %.299, label %B0.endif.endif.endif.endif.endif, label %B0.endif.endif.endif.endif.if, !prof !31

B0.endif.endif.endif.endif.if:                    ; preds = %B0.endif.endif.endif.endif
  call void @numba_gil_ensure(i32* nonnull %.301)
  call void @Py_FatalError(i8* getelementptr inbounds ([36 x i8], [36 x i8]* @".const.BLAS wrapper returned with an error", i64 0, i64 0))
  unreachable

B0.endif.endif.endif.endif.endif:                 ; preds = %B0.endif.endif.endif.endif
  call void @NRT_decref(i8* %arg.b.0)
  call void @NRT_decref(i8* %arg.a.0)
  %retptr.repack11 = bitcast { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr to i8**
  store i8* %.146, i8** %retptr.repack11, align 8
  %retptr.repack6 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 1
  store i8* null, i8** %retptr.repack6, align 8
  %retptr.repack8 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 2
  store i64 %arg.a.5.0, i64* %retptr.repack8, align 8
  %retptr.repack10 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 3
  store i64 8, i64* %retptr.repack10, align 8
  %retptr.repack12 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 4
  %4 = bitcast double** %retptr.repack12 to i8**
  store i8* %.6.i, i8** %4, align 8
  %5 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 5, i64 0
  store i64 %arg.a.5.0, i64* %5, align 8
  %6 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 6, i64 0
  store i64 8, i64* %6, align 8
  ret i32 0
}

declare i32 @numba_xxgemv(i8, i8, i64, i64, i8*, i8*, i64, i8*, i8*, i8*) local_unnamed_addr

; Function Attrs: noreturn
declare void @Py_FatalError(i8*) local_unnamed_addr #3

define linkonce_odr i32 @"_ZN5numba7targets6linalg12dot_impl$247E5ArrayIdLi1E1C7mutable7alignedE5ArrayIdLi2E1C7mutable7alignedE"({ i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* noalias nocapture %retptr, { i8*, i32 }** noalias nocapture %excinfo, i8* noalias nocapture readnone %env, i8* %arg.a.0, i8* nocapture readnone %arg.a.1, i64 %arg.a.2, i64 %arg.a.3, double* %arg.a.4, i64 %arg.a.5.0, i64 %arg.a.6.0, i8* %arg.b.0, i8* nocapture readnone %arg.b.1, i64 %arg.b.2, i64 %arg.b.3, double* %arg.b.4, i64 %arg.b.5.0, i64 %arg.b.5.1, i64 %arg.b.6.0, i64 %arg.b.6.1) local_unnamed_addr {
entry:
  %.289 = alloca double, align 8
  %.291 = alloca double, align 8
  %.301 = alloca i32, align 4
  tail call void @NRT_incref(i8* %arg.a.0)
  tail call void @NRT_incref(i8* %arg.b.0)
  %.138 = icmp slt i64 %arg.b.5.1, 0
  br i1 %.138, label %B0.if, label %B0.endif, !prof !3

B0.if:                                            ; preds = %entry
  store { i8*, i32 }* @.const.picklebuf.26031217480.15, { i8*, i32 }** %excinfo, align 8
  ret i32 1

B0.endif:                                         ; preds = %entry
  %.145 = shl i64 %arg.b.5.1, 3
  %.146 = tail call i8* @NRT_MemInfo_alloc_safe_aligned(i64 %.145, i32 32)
  %.5.i = getelementptr i8, i8* %.146, i64 24
  %0 = bitcast i8* %.5.i to i8**
  %.6.i = load i8*, i8** %0, align 8
  %.133.i = icmp eq i64 %arg.a.5.0, %arg.b.5.0
  br i1 %.133.i, label %B0.endif.endif, label %B0.endif.if

B0.endif.if:                                      ; preds = %B0.endif
  %1 = bitcast { i8*, i32 }** %excinfo to i64*
  store i64 ptrtoint ({ i8*, i32 }* @.const.picklebuf.26031115728 to i64), i64* %1, align 8
  ret i32 1

B0.endif.endif:                                   ; preds = %B0.endif
  %.14.i = icmp sgt i64 %arg.a.5.0, 2147483647
  br i1 %.14.i, label %B0.endif.endif.if, label %B0.endif.endif.endif

B0.endif.endif.if:                                ; preds = %B0.endif.endif
  %2 = bitcast { i8*, i32 }** %excinfo to i64*
  store i64 ptrtoint ({ i8*, i32 }* @.const.picklebuf.26030746440.16 to i64), i64* %2, align 8
  ret i32 1

B0.endif.endif.endif:                             ; preds = %B0.endif.endif
  %.14.i20 = icmp sgt i64 %arg.b.5.1, 2147483647
  br i1 %.14.i20, label %B0.endif.endif.endif.if, label %B0.endif.endif.endif.endif

B0.endif.endif.endif.if:                          ; preds = %B0.endif.endif.endif
  %3 = bitcast { i8*, i32 }** %excinfo to i64*
  store i64 ptrtoint ({ i8*, i32 }* @.const.picklebuf.26030746440.16 to i64), i64* %3, align 8
  ret i32 1

B0.endif.endif.endif.endif:                       ; preds = %B0.endif.endif.endif
  store double 1.000000e+00, double* %.289, align 8
  store double 0.000000e+00, double* %.291, align 8
  %.293 = bitcast double* %.289 to i8*
  %.294 = bitcast double* %arg.b.4 to i8*
  %.295 = bitcast double* %arg.a.4 to i8*
  %.296 = bitcast double* %.291 to i8*
  %.298 = call i32 @numba_xxgemv(i8 100, i8 110, i64 %arg.b.5.1, i64 %arg.a.5.0, i8* nonnull %.293, i8* %.294, i64 %arg.b.5.1, i8* %.295, i8* nonnull %.296, i8* %.6.i)
  %.299 = icmp eq i32 %.298, 0
  br i1 %.299, label %B0.endif.endif.endif.endif.endif, label %B0.endif.endif.endif.endif.if, !prof !31

B0.endif.endif.endif.endif.if:                    ; preds = %B0.endif.endif.endif.endif
  call void @numba_gil_ensure(i32* nonnull %.301)
  call void @Py_FatalError(i8* getelementptr inbounds ([36 x i8], [36 x i8]* @".const.BLAS wrapper returned with an error.17", i64 0, i64 0))
  unreachable

B0.endif.endif.endif.endif.endif:                 ; preds = %B0.endif.endif.endif.endif
  call void @NRT_decref(i8* %arg.b.0)
  call void @NRT_decref(i8* %arg.a.0)
  %retptr.repack11 = bitcast { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr to i8**
  store i8* %.146, i8** %retptr.repack11, align 8
  %retptr.repack6 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 1
  store i8* null, i8** %retptr.repack6, align 8
  %retptr.repack8 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 2
  store i64 %arg.b.5.1, i64* %retptr.repack8, align 8
  %retptr.repack10 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 3
  store i64 8, i64* %retptr.repack10, align 8
  %retptr.repack12 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 4
  %4 = bitcast double** %retptr.repack12 to i8**
  store i8* %.6.i, i8** %4, align 8
  %5 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 5, i64 0
  store i64 %arg.b.5.1, i64* %5, align 8
  %6 = getelementptr inbounds { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }, { i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }* %retptr, i64 0, i32 6, i64 0
  store i64 8, i64* %6, align 8
  ret i32 0
}

; Function Attrs: argmemonly nounwind
declare void @llvm.memset.p0i8.i64(i8* nocapture writeonly, i8, i64, i32, i1) #4

; Function Attrs: nounwind readnone speculatable
declare <4 x double> @llvm.exp.v4f64(<4 x double>) #2

; Function Attrs: nounwind
declare void @llvm.stackprotector(i8*, i8**) #5

attributes #0 = { noinline norecurse nounwind }
attributes #1 = { noinline }
attributes #2 = { nounwind readnone speculatable }
attributes #3 = { noreturn }
attributes #4 = { argmemonly nounwind }
attributes #5 = { nounwind }

!0 = distinct !{!0, !1}
!1 = !{!"llvm.loop.unroll.disable"}
!2 = distinct !{!2, !1}
!3 = !{!"branch_weights", i32 1, i32 99}
!4 = !{!5}
!5 = distinct !{!5, !6}
!6 = distinct !{!6, !"LVerDomain"}
!7 = !{!8}
!8 = distinct !{!8, !6}
!9 = !{!10}
!10 = distinct !{!10, !6}
!11 = !{!5, !8}
!12 = distinct !{!12, !13, !14}
!13 = !{!"llvm.loop.vectorize.width", i32 1}
!14 = !{!"llvm.loop.interleave.count", i32 1}
!15 = distinct !{!15, !13, !14}
!16 = !{!17}
!17 = distinct !{!17, !18}
!18 = distinct !{!18, !"LVerDomain"}
!19 = !{!20}
!20 = distinct !{!20, !18}
!21 = !{!22}
!22 = distinct !{!22, !18}
!23 = !{!17, !20}
!24 = distinct !{!24, !13, !14}
!25 = distinct !{!25, !1}
!26 = distinct !{!26, !13, !14}
!27 = distinct !{!27, !1}
!28 = distinct !{!28, !1}
!29 = distinct !{!29, !1}
!30 = distinct !{!30, !1}
!31 = !{!"branch_weights", i32 99, i32 1}
