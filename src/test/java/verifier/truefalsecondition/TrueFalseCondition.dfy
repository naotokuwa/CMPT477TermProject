method TrueFalseConditionValid1(x: int) returns (y: int) 
  ensures y == x + 1
{
  if (true) {
    y := x;
  } else {
    y := x + 1;
  }

  if (false) {
    y := y;
  } else {
    y := y + 1;
  }
}