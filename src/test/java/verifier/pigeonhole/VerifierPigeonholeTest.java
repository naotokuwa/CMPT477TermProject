package verifier.pigeonhole;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierPigeonholeTest {
	private StatementSerializeVisitor visitor;
	private Statement program;
	
	private String expectedSerializedProgram(){
	  String expected = "if ( pigeons <= holes ) ==> ( pigeonsHaveHole == 1 )\n";
    expected += "then\n";
    expected += "  if NOT( pigeons <= holes )\n";
    expected += "  then\n";
    expected += "    if pigeonsHaveHole == 0\n";
    expected += "    then\n";
    expected += "      correctAnswer := 1\n";
    expected += "    else\n";
    expected += "      correctAnswer := 0\n";
    expected += "  else\n";
    expected += "    correctAnswer := 1\n";
    expected += "else\n";
    expected += "  correctAnswer := 0";
	  return expected;
	} 
	
	private Statement createProgram(){

    // -- OuterIfStatement -- 
    // outerIfInnerCondition1 
    VariableExpression pigeons1 = new VariableExpression("pigeons");
    VariableExpression holes1 = new VariableExpression("holes");
    BinaryCondition outerIfInnerCondition1 = new BinaryCondition(ConditionType.LE, pigeons1, holes1);

    // outerIfInnerCondition2
    VariableExpression pigeonsHaveHole1 = new VariableExpression("pigeonsHaveHole");
    IntegerExpression one1 = new IntegerExpression(1);
    BinaryCondition outerIfInnerCondition2 = new BinaryCondition(ConditionType.EQUAL, pigeonsHaveHole1, one1);

    // outerIfCondition
    Condition outerIfCondition = new BinaryConnective(ConnectiveType.IMPLIES, outerIfInnerCondition1, outerIfInnerCondition2);

    // -- MiddleIfStatement -- 
    // middleIfCondition 
    VariableExpression pigeons2 = new VariableExpression("pigeons");
    VariableExpression holes2 = new VariableExpression("holes");
    BinaryCondition theninner1 = new BinaryCondition(ConditionType.LE, pigeons2, holes2);
    Condition middleIfCondition = new UnaryConnective(ConnectiveType.NOT, theninner1);

    // -- InnerIfStatement --
    // innerIfCondition
    VariableExpression pigeonsHaveHole2 = new VariableExpression("pigeonsHaveHole");
    IntegerExpression zero2 = new IntegerExpression(0);
    BinaryCondition innerIfCondition = new BinaryCondition(ConditionType.EQUAL, pigeonsHaveHole2, zero2);

    // innerThenBlock
    VariableExpression correctAnswer3 = new VariableExpression("correctAnswer");
    IntegerExpression one3 = new IntegerExpression(1);
    Assignment innerThenBlock = new Assignment(correctAnswer3, one3);

    // innerElseBlock
    VariableExpression correctAnswer4 = new VariableExpression("correctAnswer");
    IntegerExpression zero3 = new IntegerExpression(0);
    Assignment innerElseBlock = new Assignment(correctAnswer4, zero3);

		Statement middleThenBlock = new If(innerIfCondition, innerThenBlock, innerElseBlock);
    // -- END InnerIfStatement --

    // middleElseBlock
    VariableExpression correctAnswer2 = new VariableExpression("correctAnswer");
    IntegerExpression one2 = new IntegerExpression(1);
    Assignment middleElseBlock = new Assignment(correctAnswer2, one2);

		Statement outerThenBlock = new If(middleIfCondition, middleThenBlock, middleElseBlock);
    // -- END MiddleIfStatement -- 

    // outerElseBlock
    VariableExpression correctAnswer1 = new VariableExpression("correctAnswer");
    IntegerExpression zero1 = new IntegerExpression(0);
    Assignment outerElseBlock = new Assignment(correctAnswer1, zero1);
	
		Statement program = new If(outerIfCondition, outerThenBlock, outerElseBlock);
		
		// Verify program serialization
		program.accept(visitor);
		String result = visitor.result;
		String expected = expectedSerializedProgram();
		
		assertEquals(expected, result);
		
		return program;
	}

  @BeforeEach public void setUp() {
    visitor = new StatementSerializeVisitor();
    this.program = createProgram();
  }
    
  @Test
  void PigeonholeValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}