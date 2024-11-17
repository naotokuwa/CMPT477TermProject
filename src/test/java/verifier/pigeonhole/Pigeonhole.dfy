// We are marking students who are solving the Pigeonhole problem.
// Given integers pigeons and holes, they should answer if every pigoen has a hole (1 if yes, 0 if no). 
// Our method should mark the students answers, returning 1 if they gave the right answer, 0 if wrong.

method PigeonholeValid1(pigeons: int, holes: int, pigeonsHaveHole: int) returns (correctAnswer: int) 
  requires pigeonsHaveHole == 1 || pigeonsHaveHole == 0
  requires pigeons >= 0
  requires holes >= 0
  ensures pigeons <= holes && pigeonsHaveHole == 0 ==> correctAnswer == 0
  ensures pigeons <= holes && pigeonsHaveHole == 1 ==> correctAnswer == 1
  ensures pigeons > holes && pigeonsHaveHole == 1 ==> correctAnswer == 0
  ensures pigeons > holes && pigeonsHaveHole == 0 ==> correctAnswer == 1
{
  if (pigeons <= holes ==> pigeonsHaveHole == 1) {
    // either pigeons > holes 
    // or pigeons <= holes AND pigeonsHaveHole == 1
    if( !(pigeons <= holes) ) {
      // pigeons > holes 
      if (pigeonsHaveHole == 0) {
        // pigeons > holes AND pigeonsHaveHole == 0
        correctAnswer := 1;
      } else {
        // pigeons > holes AND pigeonsHaveHole == 1
        correctAnswer := 0;
      }
    } else {
      // pigeons <= holes AND pigeonsHaveHole == 1
      correctAnswer := 1;
    }
  } else {
    // pigeons <= holes AND pigeohsHaveHole == 0
    correctAnswer := 0;
  }
}