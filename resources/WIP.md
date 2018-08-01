Work-in-progress features are as follows;

## NumPy-aware translation and runtime

Most of python users possibly write functions with `NumPy`, so it is useful to translate them for JVMs.
Let's say that you write a function below with `NumPy` and
you get [LLVM assembly code](../examples/numpy_logistic_regression.ll) for the function via `Numba`:

```python
import numpy as np

def numpy_logistic_regression(Y, X, w, iterations):
  for i in range(iterations):
    w -= np.dot(((1.0 / (1.0 + np.exp(-Y * np.dot(X, w))) - 1.0) * Y), X)
    return w
```

Then, you invoke the bitcode of the function as follows:

```java
// Placeholders for Python arrays
PyArrayHolder Y = new PyArrayHolder();
PyArrayHolder X = new PyArrayHolder();
PyArrayHolder w = new PyArrayHolder();

// Loads LLVM bitcode and runs it
Class<?> clazz = LLJVMClassLoader.currentClassLoader.loadClassFromBitcodeFile("numpy_logistic_regression.bc");

LLJVMUtils.invoke(
  clazz,
  Y.with(double[] { 1.0, 1.0 }).addr(),
  X.with(double[] { 1.0, 1.0, 1.0, 1.0 }).reshape(2, 2).addr(),
  w.with(double[] { 1.0, 1.0 }).addr(),
  1L);
```

