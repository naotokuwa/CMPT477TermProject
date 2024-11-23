// Program LargestOfThree 
// Given three integer inputs, find the largest one and return it.


// No Preconditions 
method LargestOfThreeValidNoPrecondition (x: int, y: int, z: int ) returns (largest :int)
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

// With PreConditions 
method LargestOfThreeValidWithPre(x: int, y: int, z: int) returns (largest: int)
    requires x <= y && y <= z
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

// Invalid Pre 
method LargestOfThreeInvalidWithPre(x: int, y: int, z: int) returns (largest: int)
    requires x == y
    ensures  x == largest
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

// Invalid No Pre
method LargestOfThreeInvalidWithoutPre(x: int, y: int, z: int) returns (largest: int)
    ensures y == largest
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

// Incorrect Program
method LargestOfThreeWrong(x: int, y: int, z: int) returns (largest: int)
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
        largest := x;
    } else {
        // Meaningless code
        var tmp: int := 1;
    }
}
