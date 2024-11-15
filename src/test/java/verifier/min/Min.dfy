method MinValid1 (x: int, y: int ) returns (minVal :int)
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