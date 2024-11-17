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
	  String expected = "if ( a == 0 ) AND ( b == 0 )\n";
    expected += "then\n";
    expected += "  nor := 1\n";
    expected += "else\n";
    expected += "  nor := 0";
	  return expected;
	} 
	
	private Statement createProgram(){

    // Condition Inner 1
    VariableExpression a1 = new VariableExpression("a");
    IntegerExpression zero1 = new IntegerExpression(0);
    Condition c1 = new BinaryCondition(ConditionType.EQUAL, a1, zero1);

    // Condition Inner 2
    VariableExpression b1 = new VariableExpression("b");
    IntegerExpression zero2 = new IntegerExpression(0);
    Condition c2 = new BinaryCondition(ConditionType.EQUAL, b1, zero2);

    Condition condition = new BinaryConnective(ConnectiveType.AND, c1, c2);

    // Then Block
    VariableExpression nor1 = new VariableExpression("nor");
    IntegerExpression one1 = new IntegerExpression(1);
    Assignment thenBlock = new Assignment(nor1, one1);

    // Else Block 
    VariableExpression nor2 = new VariableExpression("nor");
    IntegerExpression zero3 = new IntegerExpression(0);
    Assignment elseBlock = new Assignment(nor2, zero3);

    // If Statement 
    Statement program = new If(condition, thenBlock, elseBlock);

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