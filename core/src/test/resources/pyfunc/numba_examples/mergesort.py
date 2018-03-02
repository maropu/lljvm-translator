from numba import jit, float64

@jit("float64[:](float64[:])")
def mergesort_inplace(arr):
  if arr.size > 2:
    mid = arr.size // 2
    first = arr[:mid]
    second = arr[mid:]
    mergesort_inplace(first)
    mergesort_inplace(second)

    left = 0
    right = mid
    while left < mid and right < arr.size:
      if arr[left] <= arr[right]:
        left += 1
      else:
        temp = arr[right]
        right += 1
        # copy left array to the right by one
        for i in range(mid, left, -1):
          arr[i] = arr[i - 1]
        arr[left] = temp
        left += 1
        mid += 1
  elif arr.size == 2:
    a, b = arr
    arr[0], arr[1] = ((a, b) if a <= b else (b, a))
  return arr

@jit("float64[:](float64[:])")
def mergesort_internal(arr):
  if arr.size > 2:
    mid = arr.size // 2
    first = mergesort_internal(arr[:mid].copy())
    second = mergesort_internal(arr[mid:].copy())

    left = right = 0
    writeidx = 0
    while left < first.size and right < second.size:
      if first[left] <= second[right]:
        arr[writeidx] = first[left]
        left += 1
      else:
        arr[writeidx] = second[right]
        right += 1
      writeidx += 1

    while left < first.size:
      arr[writeidx] = first[left]
      writeidx += 1
      left += 1

    while right < second.size:
      arr[writeidx] = second[right]
      writeidx += 1
      right += 1

  elif arr.size == 2:
    a, b = arr
    arr[0], arr[1] = ((a, b) if a <= b else (b, a))
  return arr

def mergesort(arr):
  return mergesort_internal(arr)
