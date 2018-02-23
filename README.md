[![Build Status](https://travis-ci.org/maropu/lljvm-translator.svg?branch=master)](https://travis-ci.org/maropu/lljvm-translator)

This is an experimental translator to build JVM bytecode from LLVM bitcode.
Since some existing tools can generate LLVM bitcode from functions written in other languages
(e.g.,  [Numba](https://numba.pydata.org/) for python functions,
[clang](https://clang.llvm.org/) for C/C++ functions, and [DragonEgg](https://dragonegg.llvm.org/) for Fortran/Go functions),
this library targets at easily injecting the bitcode into JVMs.

## Python functions to JVM class methods

First, you need to install `Numba` to generate LLVM bitcode from python functions:

    $ pip install numba

You run code blow to get LLVM bitcode for a python function:

```python
import math

from numba import cfunc

def pyfunc(x, y):
  return math.log10(2 * x) + y

# Compiles the python function above and writes as LLVM bitcode
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

You can load this gen'd class file by the code below and run in JVMs:

```java
import java.lang.reflect.Method;

import maropu.lljvm.*;

public class LLJVMTest {

  public static void main(String[] args) {
    try {
      Class<?> clazz = LLJVMClassLoader.currentClassLoader.loadClassFromBytecodeFile("GeneratedClass", "pyfunc.class");
      Method pyfunc = LLJVMUtils.getMethod(clazz, Double.TYPE, Double.TYPE);
      System.out.println(pyfunc.invoke(null, 3, 6));
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

## NumPy-aware translation and runtime

Most of python users possibly write python functions with `NumPy`, so it is useful to translate these functions for JVMs:
Let's say you write a python function below with `NumPy` and
you get [LLVM assembly code](./examples/numpy_logistic_regression.ll) for this function via `Numba`:

```python
import numpy as np

def numpy_logistic_regression(Y, X, w, iterations):
  for i in range(iterations):
    w -= np.dot(((1.0 / (1.0 + np.exp(-Y * np.dot(X, w))) - 1.0) * Y), X)
    return w
```

Then, you can generate [a JVM class file](./examples/numpy_logistic_regression.jasmin) for the function
via `lljvm-translator` and invoke this as follows:

```java
  // Placeholders for python arrays
  PyArrayHolder Y = new PyArrayHolder();
  PyArrayHolder X = new PyArrayHolder();
  PyArrayHolder w = new PyArrayHolder();

  // Loads the gen'd class file
  Class<?> clazz = LLJVMClassLoader.currentClassLoader.loadClassFromBytecodeFile("GeneratedClass", "numpy_logistic_regression.class");
  Method pyfunc = LLJVMUtils.getMethod(clazz, Long.TYPE, Long.TYPE, Long.TYPE, Long.TYPE);

  // Invokes it
  pyfunc.invoke(
    null,
    Y.with(double[] { 1.0, 1.0 }).addr(),
    X.with(double[] { 1.0, 1.0, 1.0, 1.0 }).reshape(2, 2).addr(),
    w.with(double[] { 1.0, 1.0 }).addr(),
    1L);
```

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

## Advanced topics: Optimizes UDFs in DBMS-like systems

There are many advanced research activities (listed below) to improve UDF processings in DBMS-like systems.
This library has the almost same goal with these activities though, it employs more naive and practical approach;
Apache Spark internally converts input declarative quries (e.g., SQL/DataFrame) to imperative Java code and
compiles the code into JVM bytecode with [Janino](http://janino-compiler.github.io/janino/).
To get the full JVM optimization (e.g., inlining), one can merge the Spark gen'd bytecode
with UDF bytecode generated by this library.

 * Karthik Ramachandra et al., Froid: Optimization of Imperative Programs in a Relational Database, Proceedings of the VLDB Endowment, Volume 11, Issue 4, Pages 432-444, 2017.
 * K. Venkatesh Emani et al., DBridge: Translating Imperative Code to SQL, Proceedings of SIGMOD, Pages 1663-1666, 2017.
 * Andrew Crotty, et al., An Architecture for Compiling UDF-centric Workflows, Proceedings of the VLDB Endowment, Volume 8, Issue 12, Pages 1466-1477, 2015.
 * Varun Simhadri et al., Decorrelation of User Defined Function Invocations in Queries, Proceddings of ICDE, Pages 532–543, 2014.
 * (more existing researches will be listed here...)

## TODO

 * Fix many bugs in `lljvm-native` and add tests
 * Add more platform-dependent binaries in `src/main/resources/native`
 * Make less dependencies in the native binaries
 * Register this library in the Maven Central Repository

## Bug reports

If you hit some bugs and requests, please leave some comments on [Issues](https://github.com/maropu/llvm-jdc/issues)
or Twitter([@maropu](http://twitter.com/#!/maropu)).

