// Lets define a way to calculate Trajectory given two points. 
// Let a point be a pair of integers (a,b) which is either has neutral or changing tragectory. 
// If the trajectory is neutral, then the output is the same as the inputs. 
// given a==b, return b. 
// If the trajectory is changing, then the output is the change added to the second point. 
// given a!=b, return b + b-a

method TrajectoryValid1(a: int, b: int) returns (trajectory: int)
  ensures a == b ==> trajectory == a
  ensures a != b ==> trajectory == b + (b - a)
{
  if ( a == b ) {
    trajectory := b;
  } else {
    var changeInTrajectory: int := ( a * -1 ) + b;
    trajectory := b + changeInTrajectory;
  }
}
