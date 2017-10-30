CXX := c++
# CFLAGS := $(shell llvm-config --cxxflags --ldflags)
CFLAGS1 := $(shell llvm-config --cxxflags)
# CFLAGS2 := $(shell llvm-config --cxxflags)
# CFLAGS2 := $(shell llvm-config --cxxflags --ldflags llvm-config --system-libs --libs bitreader codegen selectiondag)
CFLAGS2 := $(shell llvm-config --cxxflags --ldflags --system-libs --libs all)
# CFLAGS2 := $(shell llvm-config --cxxflags --ldflags --system-libs)
# CFLAGS2 := $(shell llvm-config --cxxflags --system-libs)
# CFLAGS := $(shell llvm-config --cxxflags --ldflags --libs bitreader codegen selectiondag)
# CFLAGS := $(shell llvm-config --cxxflags --ldflags --libs all)
# CFLAGS := $(shell llvm-config --cxxflags --ldflags --libsfiles)

EXTRAFLAGS := -I../../../../include/inc_mac

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
	# $(CXX) ${EXTRAFLAGS} ${OBJS} ${CFLAGS2} -o lljvm-backend
	$(CXX) -shared ${EXTRAFLAGS} ${OBJS} ${CFLAGS2} -o liblljvm.dylib
	# $(CXX) -shared -static-libstdc++ ${EXTRAFLAGS} ${OBJS} ${CFLAGS2} -o lljvm-backend.so
	# $(CXX) -shared ${EXTRAFLAGS} ${OBJS} ${CFLAGS2} -o lljvm-backend.so /usr/local/Cellar/llvm/5.0.0/lib/*.a

doc:
	doxygen

clean:
	rm -f *.o *.so *.dylib lljvm-backend
	rm -rf doc

.cpp.o:
	$(CXX) ${EXTRAFLAGS} ${CFLAGS1} -c $<

${OBJS}: backend.h

