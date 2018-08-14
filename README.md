[![License](http://img.shields.io/:license-Apache_v2-blue.svg)](https://github.com/maropu/lljvm-translator/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/maropu/lljvm-translator.svg?branch=master)](https://travis-ci.org/maropu/lljvm-translator)
<!-- [![Coverage Status](https://coveralls.io/repos/github/maropu/lljvm-translator/badge.svg?branch=master)](https://coveralls.io/github/maropu/lljvm-translator?branch=master) -->
<!-- [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.maropu.lljvm/lljvm-translator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.maropu.lljvm/lljvm-translator/) -->

This is an experimental low-level translator from LLVM bitcode to JVM bytecode.
Since existing tools can generate LLVM bitcode from functions written in some languages
(e.g.,  [Numba](https://numba.pydata.org/) for Python, [Clang](https://clang.llvm.org/) for C/C++,
[DragonEgg](https://dragonegg.llvm.org/) for Fortran/Go, and [Weld](https://www.weld.rs/) for cross-library optimization),
this library targets at easily injecting the bitcode into JVMs.

Note that the core component is refactored from [lljvm](https://github.com/davidar/lljvm) (credit should go to the original author).

## Python functions to JVM class methods

First, you need to install `Numba` to generate LLVM bitcode from python functions:

    $ pip install numba

You run code blow to get LLVM bitcode for a python function:

```python
import math

from numba import cfunc

def pyfunc(x, y):
  return math.log10(2 * x) + y

## Compiles the python function above and writes as LLVM bitcode
with open("pyfunc.bc", "wb") as out:
  f = cfunc("float64(float64, float64)")(pyfunc)
  out.write(f._library._final_module.as_bitcode())
```

Finally, you get a JVM class file for the python function `pyfunc`:

    $ ./bin/lljvm-translator ./pyfunc.bc

To check gen'd bytecode in the JVM class file, you can use `javap`:

    $ javap -c -s pyfunc.class

```java
public final class GeneratedClass {
  ...
  public static double _cfunc__ZN8__main__10pyfunc_241Edd(double, double);
    descriptor: (DD)D
    Code:
       0: dconst_0
       1: dstore        4
       3: dconst_0
       4: dstore        6
       6: dconst_0
       7: dstore        8
       9: dload_0
      10: ldc2_w        #26                 // double 2.0d
      13: dmul
      14: dstore        4
      16: dload         4
      18: invokestatic  #8                  // Method java/lang/Math.log10:(D)D
      21: dstore        6
      23: dload         6
      25: dload_2
      26: dadd
      27: dstore        8
      29: dload         8
      31: dreturn
}
```

You can load this gen'd class file via Java Runtime Reflection and run in JVMs:

```java
import io.github.maropu.lljvm.LLJVMClassLoader;
import io.github.maropu.lljvm.LLJVMUtils;

public class LLJVMTest {

  public static void main(String[] args) {
    try {
      /**
       * If you want to load a class from LLVM bitcode directly, you write a line below;
       * Class<?> clazz = LLJVMClassLoader.currentClassLoader.loadClassFromBitcodeFile("pyfunc.bc");
       */
      Class<?> clazz = LLJVMClassLoader.currentClassLoader.loadClassFromBytecodeFile("pyfunc.class");
      System.out.println(LLJVMUtils.invoke(clazz, 3, 6));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
```

## For C/C++ functions

You can use `clang` to get LLVM bitcode for C/C++ functions:

    $ cat cfunc.c
    #include <math.h>
    double cfunc(double a, double b) {
      return pow(3.0 * a, 2.0) + 4.0 * b;
    }

    $ clang -c -O2 -emit-llvm -o cfunc.bc cfunc.c
    $ ./bin/lljvm-translator ./cfunc.bc

Then, you dump gen'd bytecode:

    $ javap -c cfunc.class

```java
public final class GeneratedClass {
  ...
  public static double __Z5cfuncdd(double, double);
    descriptor: (DD)D
    Code:
       0: dconst_0
       1: dstore        4
       3: dconst_0
       4: dstore        6
       6: dconst_0
       7: dstore        8
       9: dconst_0
      10: dstore        10
      12: dload_0
      13: ldc2_w        #8                  // double 3.0d
      16: dmul
      17: dstore        4
      19: dload         4
      21: dload         4
      23: dmul
      24: dstore        6
      26: dload_2
      27: ldc2_w        #13                 // double 4.0d
      30: dmul
      31: dstore        8
      33: dload         6
      35: dload         8
      37: dadd
      38: dstore        10
      40: dload         10
      42: dreturn
}
```

## Example application

See [lljvm-example](https://github.com/maropu/lljvm-example).

## Array supports

Let's say that you have a function below;

    $ cat cfunc.c
    #include <stdio.h>
    long cfunc(long x[], int size) {
      long sum = 0;
      for (int i = 0; i < size; i++) {
        sum += x[i];
      }
      return sum;
    }

Then, you can handle the array in Java like;

```java
import io.github.maropu.lljvm.LLJVMClassLoader;
import io.github.maropu.lljvm.LLJVMUtils;
import io.github.maropu.lljvm.util.ArrayUtils;

long[] javaArray = {1L, 2L, 3L};
System.out.println(LLJVMUtils.invoke(clazz, ArrayUtils.addressOf(javaArray), javaArray.length));
```

## Compilation in Java

If `clang` is installed in your platform, you can say a line to get LLVM bitcode;

```
import io.github.maropu.lljvm.util.ClangRunner;

byte[] bitcode = ClangRunner.exec(
  "#include <math.h>                     \n" +
  "double cfunc(double a, double b) {    \n" +
  "  return pow(2.0 * a, 2.0) + 4.0 * b; \n" +
  "}");
...
```

## Gen'd bytecode verification

An objective of this library is to provide not a full-fledge translator but a restricted one for simple LLVM bitcode.
So, it is important to verify that gen'd bytecode is correct and supported before execution.
The library does so when loading it in `LLJVMClassLoader` and, if it detects illegal code, it throws `LLJVMException`;

```java
try {
  Class<?> clazz = LLJVMClassLoader.currentClassLoader.loadClassFromBitcodeFile("func.bc");
  ...
} catch (LLJVMException e) {
  // Writes fallback code here
  ...
}
```

## Some notes

 - Supports OpenJDK 8 (64bit) only
 - Bundles native binaries for Linux/x86_64 and Mac/x86_64
   - For Linux, it is built by clang++ v3.6.2 (w/ glibc v2.17 and libstdc++ v4.8.5) on AWS Linux AMI (ami-0ad99772)
   - For Mac, it is built by Apple clang++ v900.0.39.2 on macOS Sierra v10.12.1
 - LLVM v5.0.2 used internally

## Maven dependency

  <dependency>
    <groupId>io.github.maropu</groupId>
    <artifactId>lljvm-core</artifactId>
    <version>0.1.0-EXPERIMENTAL</version>
    <scope>compile</scope>
  </dependency>

## Builds a native binary for your platform

To comile it, you need to check requirements below;

 - gcc-c++ v4.8.0+ / clang++ v3.1.0+
 - cmake v3.4.3+
 - python v2.7+
 - zlib v1.2.3.4+
 - ncurses v5.7+

Then, you run lines below;

    $ git clone https://github.com/maropu/lljvm-translator.git
    $ cd lljvm-translator/lib/lljvm-native

    // Downloads/compiles LLVM, builds a native library based on the compiled LLVM,
    // and then copys the library into a proper location.
    //
    // Or, you can use the pre-built LLVM in http://releases.llvm.org/download.html#5.0.2;
    //  $ wget http://releases.llvm.org/5.0.2/clang+llvm-5.0.2-<your platform>.tar.xz
    //  $ tar xvf clang+llvm-5.0.2-<your platform>.tar.xz
    //  $ LLVM_DIR=`pwd`/clang+llvm-5.0.2-<your platform> CXX=clang++ ./waf configure
    //  $ ./waf -v
    $ ./build-lljvm.sh

    // Moves the root and builds jar with the binary above
    $ cd ../..
    $ ./build/mvn clean package
    $ ls target
    lljvm-core_0.1.0-EXPERIMENTAL-with-dependencies.jar
    lljvm-core_0.1.0-EXPERIMENTAL.jar
    ...

## Current development topics

You can check [a document](./resources/WIP.md) for WIP features.

## Use cases: injects python UDFs into Apache Spark gen'd code

Python UDFs in [Apache Spark](https://spark.apache.org/) have well-known overheads and the recent work of
[Vectorized UDFs](https://issues.apache.org/jira/browse/SPARK-21190) in the community
significantly improves the performance. But, Python UDFs still incur
[large performance gaps](https://gist.github.com/maropu/9f995f65b1cb160865e79e14e5216320) against Scala UDFs.
If we could safely inject python UDFs into Spark gen'd code, we would make the Python UDF overheads close to zero.
Here is [a sample patch](https://github.com/apache/spark/compare/master...maropu:LLJVMSpike) and
a quick benchmark below shows that the injection could make it around 50x faster than
the performance of the Vectorized UDFs:

![Python UDF benchmark results](resources/udf_benchmark_results.png)

## Advanced topics: optimizes UDFs in DBMS-like systems

There are many advanced research activities (listed below) to improve UDF processings in DBMS-like systems.
This library has the almost same goal with these activities though, it employs more naive and practical approach;
Apache Spark internally converts input declarative quries (e.g., SQL/DataFrame) to imperative Java code and
compiles the code into JVM bytecode with [Janino](http://janino-compiler.github.io/janino/).
To get the full JVM optimization (e.g., inlining), one can merge the Spark gen'd bytecode
with UDF bytecode generated by this library.

 - Karthik Ramachandra et al., Froid: Optimization of Imperative Programs in a Relational Database, Proceedings of the VLDB Endowment, Volume 11, Issue 4, Pages 432-444, 2017.
 - K. Venkatesh Emani et al., DBridge: Translating Imperative Code to SQL, Proceedings of SIGMOD, Pages 1663-1666, 2017.
 - Andrew Crotty, et al., An Architecture for Compiling UDF-centric Workflows, Proceedings of the VLDB Endowment, Volume 8, Issue 12, Pages 1466-1477, 2015.
 - Varun Simhadri et al., Decorrelation of User Defined Function Invocations in Queries, Proceddings of ICDE, Pages 532–543, 2014.
 - (more existing researches will be listed here...)

Other-related papers are lists below:

 - K. Venkatesh Emani and S. Sudarshan, COBRA: A Framework for Cost Based Rewriting of Database Applications, Proceddings of ICDE, 2018.

## TODO

 - Supports NumPy-aware translation
 - Adds more platform-dependent binaries in `src/main/resources/native`
 - Needs more tests to check if the translation works correctly
 - Statically Links BSD libc++ for native binaries
 - Upgrades LLVM to v6.x
 - Uses docker to build native binaries

## Bug reports

If you hit some bugs and requests, please leave some comments on [Issues](https://github.com/maropu/llvm-jdc/issues)
or Twitter([@maropu](http://twitter.com/#!/maropu)).

