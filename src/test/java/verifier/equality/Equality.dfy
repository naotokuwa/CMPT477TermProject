// If x and y are equal return 1, if not equal return 0
method CheckIfEqual(x: int, y: int) returns (equal: int)
  ensures (x <= y) && (y <= x) ==> equal == 1
  ensures !(x <= y) || !(y <= x) ==> equal == 0
{
  if (x <= y) {
    if(y <= x) {
      equal := 1;
    } else {
      equal := 0; 
    }
  } else {
    equal := 0;
  }
}