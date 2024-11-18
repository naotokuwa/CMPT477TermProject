// NOR is a truth-functional operator which produces a result that is the negation of logical or. 
// That is, a sentence of the form (p NOR q) is true precisely when neither p nor q is true
// —i.e. when both p and q are false.
// - Wikipedia, Logical NOR 

// 1 == true, 2 == false
method NorValid1(a: int, b: int) returns (nor: int)
  requires a == 1 || a == 0
  requires b == 1 || b == 0
  ensures a == 0 && b == 0 ==> nor == 1
  ensures a == 1 && b == 0 ==> nor == 0
  ensures a == 0 && b == 1 ==> nor == 0
  ensures a == 1 && b == 1 ==> nor == 0
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
