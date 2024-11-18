method AbsValidNoPrecondition(x: int) returns (y: int)
  ensures 0 <= y  
{
  if x <= 0 {
    y := x * -1;
  } else {
    y := x;
  }
}

method AbsValidWithPrecondition(x: int) returns (y: int)
  requires x <= 0
  ensures 0 <= y
{
  if x <= 0 {
    y := x * -1;
  } else {
    y := x;
  }
}

method AbsInvalidNoPrecondition(x: int) returns (y: int)
  ensures y <= 0
{
  if x <= 0 {
    y := x * -1;
  } else {
    y := x;
  }
}

method AbsInvalidWithPrecondition(x: int) returns (y: int)
  requires x == -1
  ensures y <= 0
{
  if x <= 0 {
    y := x * -1;
  } else {
    y := x;
  }
}

method InvalidProgramValidSpec(x: int) returns (y: int)
  ensures 0 <= y
{
  if x == 0 {
    y := x * -1;
  } else {
    y := x;
  }
}
