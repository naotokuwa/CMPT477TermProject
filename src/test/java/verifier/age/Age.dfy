method AgeValid1(birthyear: int) returns (age: int)
  requires birthyear <= 2024
  ensures age == 2024 - birthyear
{
  var year := 2024;
  age := 2024 + (-1 * birthyear);
}