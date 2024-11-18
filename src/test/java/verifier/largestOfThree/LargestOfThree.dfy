// Given three integer inputs, find the largest one and return it. 
method LargestOfThreeValid1 (x: int, y: int, z: int ) returns (largest :int)
  ensures x <= largest && y <= largest && z <= largest
{
  largest := x;

  if (largest <= y) {
    largest := y;
  } else {
    // Meaningless code
    var tmp: int := 1;
  }

  if (largest <= z) {
    largest := z;
  } else {
    // Meaningless code
    var tmp: int := 1;
  }
}