// if the integer has a positive sign (greater than 0), return 1
// if the integer is zero, return zero
// if the integer has a negative sign (smaller than 0), return -1
method SignOfIntegerValid1 (x: int) returns (y :int)
{
  if (x == 0) {
    y := 0;
  } else {
    if (x <= 0) {
      y := -1;
    } else {
      y := 1;
    }
  }
}
