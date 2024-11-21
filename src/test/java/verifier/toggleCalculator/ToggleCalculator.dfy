// if toggleAddMul is 1 then Add, and if 0 then multiply.
// given two input integers, depending on the toggleAddMul,
// the program will add them together and return it as result
// or the program will multiply them together and return it as result

// ========================================= valid methods =========================================
method ToggleCalculatorValidWithPre(toggleAddMul: int, a: int, b: int) returns (result: int)
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

method ToggleCalculatorValidWithoutPre(toggleAddMul: int, a: int, b: int) returns (result: int)
  ensures (toggleAddMul == 1) ==> (result == a + b)
  ensures (toggleAddMul == 0) ==> (result == a * b)
{
    if(toggleAddMul == 1) {
        result := a + b;
    } else {
        result := a * b;
    }
}


// ======================================== invalid methods ========================================
method ToggleCalculatorInvalidWithPre(toggleAddMul: int, a: int, b: int) returns (result: int)
  requires toggleAddMul == -1
  ensures (toggleAddMul == 1) ==> (result == a + b)
  ensures (toggleAddMul == 0) ==> (result == a * b)
  ensures toggleAddMul != -1
{
    if(toggleAddMul == 1) {
        result := a + b;
    } else {
        result := a * b;
    }
}

method ToggleCalculatorInvalidWithoutPre(toggleAddMul: int, a: int, b: int) returns (result: int)
  ensures result == a * b
{
    if(toggleAddMul == 1) {
        result := a + b;
    } else {
        result := a * b;
    }
}


// ======================================== wrong methods ========================================
method ToggleCalculatorInvalidValidSpec(toggleAddMul: int, a: int, b: int) returns (result: int)
    requires toggleAddMul == 2
    ensures (result == a * b) || (result == a + b)
{
    if(toggleAddMul == 1) {
        result := a + b;
    } else {
        result := a * b + toggleAddMul;
    }
}
