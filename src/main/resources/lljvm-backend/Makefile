CXX := clang++

CFLAGS1 := $(shell llvm-config --cxxflags)
CFLAGS2 := $(shell llvm-config --cxxflags --ldflags --system-libs --libs all)

ifeq (${shell uname}, Darwin)
  EXTRAFLAGS := -I../../../../include/inc_mac
  SUFFIX := .dylib
else
  EXTRAFLAGS := -I../../../../include/inc_linux
  SUFFIX := .so
endif

OBJS := \
    lljvm.o \
    backend.o \
    block.o \
    branch.o \
    const.o \
    function.o \
    instruction.o \
    loadstore.o \
    name.o \
    printinst.o \
    sections.o \
    types.o

.PHONY: all doc clean

all: lljvm-backend

lljvm-backend: ${OBJS}
	$(CXX) -shared ${EXTRAFLAGS} ${OBJS} ${CFLAGS2} -o liblljvm.${SUFFIX}

doc:
	doxygen

clean:
	rm -f *.o *.so *.dylib lljvm-backend
	rm -rf doc

.cpp.o:
	$(CXX) ${EXTRAFLAGS} ${CFLAGS1} -c $<

${OBJS}: backend.h

