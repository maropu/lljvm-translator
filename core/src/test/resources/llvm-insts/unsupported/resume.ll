; ModuleID = 'resume.bc'
source_filename = "resume.cc"

define void @resume({ i8*, i32 } %x) personality i8* bitcast (i32 (...)* @_personality to i8*) {
  resume { i8*, i32 } %x
}

declare i32 @_personality(...)

