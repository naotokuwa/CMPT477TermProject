package verifier.signOfInteger;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierSignOfIntegerTest {
	private StatementSerializeVisitor visitor;
	private Statement program;
	
	private String expectedSerializedProgram(){
	  String expected = "if x == 0\n";
    expected += "then\n";
    expected += "  y := 0\n";
    expected += "else\n";
    expected += "  if x <= 0\n";
    expected += "  then\n";
    expected += "    y := -1\n";
    expected += "  else\n";
    expected += "    y := 1";
	  return expected;
	} 
	
	private Statement createProgram(){

    // -- Inner If -- 
    // Condition
    Expression x1 = new VariableExpression("x");
    IntegerExpression zero1 = new IntegerExpression(0);
    Condition innerIfCondition = new BinaryCondition(ConditionType.LE, x1, zero1);
    // Then Block 
    VariableExpression y1 = new VariableExpression("y");
    IntegerExpression negativeOne = new IntegerExpression(-1);
    Assignment innerIfThenBlock = new Assignment(y1, negativeOne);
    // Else Block
    VariableExpression y2 = new VariableExpression("y");
    IntegerExpression one = new IntegerExpression(1);
    Assignment innerIfElseBlock = new Assignment(y2, one);
    // Inner If Statement 
    Statement innerIfStatement = new If(innerIfCondition, innerIfThenBlock, innerIfElseBlock);


    // -- Outer If -- 
    // Condition
    Expression x2 = new VariableExpression("x");
    IntegerExpression zero2 = new IntegerExpression(0);
    Condition outerIfCondition = new BinaryCondition(ConditionType.EQUAL, x2, zero2);
    // Then Block
    VariableExpression y3 = new VariableExpression("y");
    IntegerExpression zero3 = new IntegerExpression(0);
    Assignment outerIfThenBlock = new Assignment(y3, zero3);
    // Else Block
    // See Inner If Statement 
    // Outer If Statment  
    Statement outerIfStatement = new If(outerIfCondition, outerIfThenBlock, innerIfStatement);

		Statement program = outerIfStatement;
		
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
  void SignOfIntegerValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}