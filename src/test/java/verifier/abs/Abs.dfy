method AbsValid1(x: int) returns (y: int)
  ensures 0 <= y  
{
  if x <= 0 {
    y := x * -1;
  } else {
    y := x;
  }
}