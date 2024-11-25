// Program Trajectory
// Lets define Trajectory as the arithemetic growth of a variable. 
// Let a be the first value of the variable, and b be the second value of the variable. 
// If the variable stays the same value, (a==b), then there is no growth.
// If the variable has a different value, (a!=b), then there is growth, equal to b-a.  
// The trajectory is the next value of the variable, equal to b + (b-a).


// No Preconditions 
method TrajectoryValidNoPrecondition(a: int, b: int) returns (trajectory: int)
	ensures a == b ==> trajectory == a
	ensures !(a == b) ==> trajectory == b + (b + (-1 * a))
{
	if ( a == b ) {
		trajectory := b;
	} else {
		var changeInTrajectory: int := ( a * -1 ) + b;
		trajectory := b + changeInTrajectory;
	}
}

// With Preconditions 
method TrajectoryValidWithPrecondition(a: int, b: int) returns (trajectory: int)
    requires a == b
	ensures a == b ==> trajectory == a
	ensures !(a == b) ==> trajectory == b + (b + (-1 * a))
{
	if ( a == b ) {
		trajectory := b;
	} else {
		var changeInTrajectory: int := ( a * -1 ) + b;
		trajectory := b + changeInTrajectory;
	}
}

// Invalid No Preconditions 
method TrajectoryInvalidNoPrecondition(a: int, b: int) returns (trajectory: int)
	ensures a == b ==> trajectory == a
	ensures !(a == b) ==> trajectory == b + (-1 * a) // Invalid Postcondition
{
	if ( a == b ) {
		trajectory := b;
	} else {
		var changeInTrajectory: int := ( a * -1 ) + b;
		trajectory := b + changeInTrajectory;
	}
}

// Invalid Program
method InvalidTrajectoryValidSpec(a: int, b: int) returns (trajectory: int)
	ensures a == b ==> trajectory == a
	ensures !(a == b) ==> trajectory == b + (b + (-1 * a))
{
	if ( a == b ) {
		trajectory := b + 1; // Invalid Assignment
	} else {
		var changeInTrajectory: int := ( a * -1 ) + b;
		trajectory := b + changeInTrajectory;
	}
}