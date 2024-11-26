// Program Nor
// NOR is a truth-functional operator which produces a result that is the negation of logical or. 
// That is, a sentence of the form (p NOR q) is true precisely when neither p nor q is true
// —i.e. when both p and q are false.
// - Wikipedia, Logical NOR 

// Since we cannot assign true/false to variables, we can instead use
// 1 == true, 0 == false


// No Preconditions 
method NorValidNoPrecondition(a: int, b: int) returns (nor: int)
    ensures a == 0 && b == 0 ==> nor == 1
    ensures (a == 1 || b == 1) ==> nor == 0
{
    if( a == 0 ) {
        if ( b == 0 ) {
            nor := 1;
        } else {
            nor := 0;
        }
    } else {
        nor := 0;
    }
}

// With PreConditions
method NorValidPrecondition(a: int, b: int) returns (nor: int)
    requires a == 1 || a == 0
    requires b == 1 || b == 0
    ensures a == 0 && b == 0 ==> nor == 1
    ensures (a == 1 || b == 1) ==> nor == 0
{
    if( a == 0 ) {
        if ( b == 0 ) {
            nor := 1;
        } else {
            nor := 0;
        }
    } else {
        nor := 0;
    }
}


// Invalid No Pre 
method NorInvalidPost(a: int, b: int) returns (nor: int)
    ensures a == 0 && b == 0 ==> nor == 0  // Incorrect postcondition
    ensures (a == 1 || b == 1) ==> nor == 0
{
    if( a == 0 ) {
        if ( b == 0 ) {
            nor := 1;
        } else {
            nor := 0;
        }
    } else {
        nor := 0;
    }
}


// Incorrect Program 
method NorIncorrect(a: int, b: int) returns (nor: int)
    requires a == 1 || a == 0
    requires b == 1 || b == 0
    ensures a == 0 && b == 0 ==> nor == 1
    ensures (a == 1 || b == 1) ==> nor == 0
{
    if( a == 0 ) {
        if ( b == 0 ) {
            nor := 1;
        } else {
            nor := 0;
        }
    } else {
        nor := 1; // Incorrect assignment
    }
}
