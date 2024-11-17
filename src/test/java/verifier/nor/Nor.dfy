// NOR is a truth-functional operator which produces a result that is the negation of logical or. 
// That is, a sentence of the form (p NOR q) is true precisely when neither p nor q is true
// —i.e. when both p and q are false.
// - Wikipedia, Logical NOR 

method NorValid1(a: bool, b: bool) returns (nor: bool)
  ensures a == false && b == false ==> nor == true 
  ensures a == true && b == false ==> nor == false
  ensures a == false && b == true ==> nor == false
  ensures a == true && b == true ==> nor == false
{
  nor := !(a || b);
}