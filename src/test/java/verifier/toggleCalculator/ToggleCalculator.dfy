// if toggleAddMul is 1 then Add, and if 0 then multiply. 
// given two input integers, depending on the toggleAddMul, 
// the program will add them together and return it as result
// or the program will multiply them together and return it as result

method ToggleCalculatorValid1(toggleAddMul: int, a: int, b: int) returns (result: int)
  requires toggleAddMul == 1 || toggleAddMul == 0
  ensures (toggleAddMul == 1) ==> (result == a + b)
  ensures (toggleAddMul == 0) ==> (result == a * b)
{
  if(toggleAddMul == 1) {
    result := a + b;
  } else {
    result := a * b;
  }
}
