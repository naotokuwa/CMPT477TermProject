// If x and y are equal return 1, if not equal return 0

// ========================================= valid methods =========================================
method EqualityWithPre1(x: int, y: int) returns (equal: int)
    requires x == 0 && y == 0
    ensures equal == 1
{
    if ( x <= y ) {
        if ( y <= x ) {
            equal := 1;
        }
        else {
            equal := 0; 
        }
    }
    else {
        equal := 0;
    }
}

method EqualityWithPre2(x: int, y: int) returns (equal: int)
    requires x <= 0 && !(y <= 0)
    ensures equal == 0
{
    if ( x <= y ) {
        if ( y <= x ) {
            equal := 1;
        }
        else {
            equal := 0; 
        }
    }
    else {
        equal := 0;
    }
}

method EqualityWithoutPre(x: int, y: int) returns (equal: int)
    ensures (x == y) ==> equal == 1
    ensures !(x == y) ==> equal == 0
{
    if ( x <= y ) {
        if ( y <= x ) {
            equal := 1;
        }
        else {
            equal := 0; 
        }
    }
    else {
        equal := 0;
    }
}

// ========================================= invalid methods =========================================
method EqualityWithInvalidPre1(x: int, y: int) returns (equal: int)
    requires x == 0
    ensures equal == 1
{
    if ( x <= y ) {
        if ( y <= x ) {
            equal := 1;
        }
        else {
            equal := 0; 
        }
    }
    else {
        equal := 0;
    }
}

method EqualityWithInvalidPre2(x: int, y: int) returns (equal: int)
    requires x == y + y
    ensures equal == 1
{
    if ( x <= y ) {
        if ( y <= x ) {
            equal := 1;
        }
        else {
            equal := 0; 
        }
    }
    else {
        equal := 0;
    }
}

// ======================================== wrong methods ========================================
method WrongEqualityWithoutPre(x: int, y: int) returns (equal: int)
    // Same post conditions for equalityWithoutPre
    ensures (x == y) ==> equal == 1
    ensures !(x == y) ==> equal == 0
{
    if ( x <= y ) {
        if ( y <= x ) {
            equal := 1;
        }
        else {
            equal := 0; 
        }
    }
    else {
        equal := 1;
    }
}