// if the integer has a positive sign (greater than 0), return 1
// if the integer is zero, return zero
// if the integer has a negative sign (smaller than 0), return -1

// ========================================= valid methods =========================================
method SignOfIntegerValidWithPre1(x: int) returns (y :int)
    requires !(x <= 0) && !(x == 0)
    ensures y == 1
{
    if (x == 0) {
        y := 0;
    }
    else {
        if (x <= 0) {
            y := -1;
        }
        else {
            y := 1;
        }
    }
}

method SignOfIntegerValidWithPre2(x: int) returns (y :int)
    requires x == 0
    ensures y == 0
{
    if (x == 0) {
        y := 0;
    }
    else {
        if (x <= 0) {
            y := -1;
        }
        else {
            y := 1;
        }
    }
}

method SignOfIntegerValidWithPre3(x: int) returns (y :int)
    requires x <= 0 && !(x == 0)
    ensures y == -1
{
    if (x == 0) {
        y := 0;
    }
    else {
        if (x <= 0) {
            y := -1;
        }
        else {
            y := 1;
        }
    }
}

method SignOfIntegerValidWithoutPre(x: int) returns (y :int)
    ensures !(x <= 0) && !(x == 0) ==> y == 1
    ensures x == 0 ==> y == 0
    ensures x <= 0 && !( x == 0) ==> y == -1
{
    if (x == 0) {
        y := 0;
    }
    else {
        if (x <= 0) {
            y := -1;
        }
        else {
            y := 1;
        }
    }
}

// ========================================= invalid methods =========================================
method SignOfIntegerInvalidWithoutPre(x: int) returns (y :int)
    ensures y == 1
{
    if (x == 0) {
        y := 0;
    }
    else {
        if (x <= 0) {
            y := -1;
        }
        else {
            y := 1;
        }
    }
}

method SignOfIntegerInvalidWithPre(x: int) returns (y :int)
    requires x == 0
    ensures y == -1
{
    if (x == 0) {
        y := 0;
    }
    else {
        if (x <= 0) {
            y := -1;
        }
        else {
            y := 1;
        }
    }
}

// ======================================== wrong methods ========================================
method WrongSignOfIntegerValidWithoutPre(x: int) returns (y :int)
    // Same post conditions for SignOfIntegerValidWithPre4
    ensures x == 0 ==> y == 0
    ensures x <= 0 && !( x == 0 )  ==> y == -1
    ensures !(x <= 0) && !(x == 0) ==> y == 1
{
    if (x <= 0) {
        y := -1;
    }
    else {
        y := 1;
    }
}