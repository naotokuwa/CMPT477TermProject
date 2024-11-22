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
    requires x <= 0
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
    // {(true -> false -> (x == x + 1) AND true -> (x + 2 == x + 1)) AND (false -> (x + 1 == x + 1) AND true -> (x + 1 + 2 == x + 1))}
    if (true) {
        y := x;
        // { false -> (x == x + 1) AND true -> (x + 2 == x + 1)}
    } else {
        y := x + 1;
        // { false -> (x + 1 == x + 1) AND true -> (x + 1 + 2 == x + 1)}
    }

    // { false -> (y == x + 1) AND true -> (y + 2 == x + 1)}
    if (false) {
        y := y;
        // {y == x + 1}
    } else {
        y := y + 2;  // Program Invalid Line
        // {y + 2 == x + 1}
    }

    // {y == x + 1}
}
