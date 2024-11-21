// To test if our boolean conditions work correctly, 
// we create the following program, whose execution can only result in y == x + 1  
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