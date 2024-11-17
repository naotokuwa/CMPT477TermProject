method MinValidNoPrecondition (x: int, y: int ) returns (minVal :int)
    ensures minVal == x || minVal == y
    ensures minVal <= x && minVal <= y
{
    minVal := x;

    if (y <= minVal){
        minVal := y;
    }
    else{
        // Meaningless code
        var tmp: int := 1;
    }
}

method MinValidWithPrecondition (x: int, y: int ) returns (minVal :int)
    requires x == y
    ensures minVal == x && minVal == y
{
    minVal := x;

    if (y <= minVal){
        minVal := y;
    }
    else{
        // Meaningless code
        var tmp: int := 1;
    }
}

method MinInvalidNoPrecondition (x: int, y: int ) returns (minVal :int)
    ensures minVal == x && minVal == y
{
    minVal := x;

    if (y <= minVal){
        minVal := y;
    }
    else{
        // Meaningless code
        var tmp: int := 1;
    }
}

method MinInvalidWithPrecondition (x: int, y: int ) returns (minVal :int)
    requires x <= 0
    ensures x == minVal
{
    minVal := x;

    if (y <= minVal){
        minVal := y;
    }
    else{
        // Meaningless code
        var tmp: int := 1;
    }
}
