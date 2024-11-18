package verifier.nor;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierNorTest {
	private StatementSerializeVisitor visitor;
	private Statement program;
	
	private String expectedSerializedProgram(){
	  String expected = "if a == 0\n";
    expected += "then\n";
    expected += "  if b == 0\n";
    expected += "  then\n";
    expected += "    nor := 1\n";
    expected += "  else\n";
    expected += "    nor := 0\n";
    expected += "else\n";
    expected += "  nor := 0";
	  return expected;
	} 
	
	private Statement createProgram(){


    // -- Outer If Statement -- 
    // Outer Condition 
    VariableExpression a1 = new VariableExpression("a");
    IntegerExpression zero1 = new IntegerExpression(0);
    Condition outerCondition = new BinaryCondition(ConditionType.EQUAL, a1, zero1);

    // -- Outer Then / Inner If Statement --
    // innerCondition
    VariableExpression b1 = new VariableExpression("b");
    IntegerExpression zero2 = new IntegerExpression(0);
    Condition innerCondition = new BinaryCondition(ConditionType.EQUAL, b1, zero2);
    // innerThenBlock
    VariableExpression nor1 = new VariableExpression("nor");
    IntegerExpression one1 = new IntegerExpression(1);
    Assignment innerThenBlock = new Assignment(nor1, one1);
    // innerElseBlock
    VariableExpression nor2 = new VariableExpression("nor");
    IntegerExpression zero3 = new IntegerExpression(0);
    Assignment innerElseBlock = new Assignment(nor2, zero3);

    Statement innerIfStatement = new If(innerCondition, innerThenBlock, innerElseBlock);

    // Outer Else 
    VariableExpression nor3 = new VariableExpression("nor");
    IntegerExpression zero4 = new IntegerExpression(0);
    Assignment outerElseBlock = new Assignment(nor3, zero4);

    Statement program = new If(outerCondition, innerIfStatement, outerElseBlock);

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
  void NorValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}