// To test if our boolean conditions work correctly, 
// we create the following program, whose execution can only result in y == x + 1  
method TrueFalseConditionValidNoPrecondition(x: int) returns (y: int)
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

method TrueFalseConditionValidWithPrecondition(x: int) returns (y: int)
    requires x <= 0  // Precondition
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

method TrueFalseConditionInvalidPostcondition(x: int) returns (y: int)
    ensures y == x // Postcondition makes it so that the program is never valid.
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

method TrueFalseConditionInvalidWithPrecondition(x: int) returns (y: int)
    requires x == 0
    ensures y == x // Postcondition makes it so that the program is never valid.
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

method TrueFalseConditionInvalidProgramValidSpec(x: int) returns (y: int)
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
        y := y + 2;  // Program Invalid Line
    }
}
