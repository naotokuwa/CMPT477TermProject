package verifier.equality;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.condition.Boolean;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierEqualityTest {
	private StatementSerializeVisitor visitor;
	private Statement program;
	
	private String expectedSerializedProgram(){
	  String expected = "if ( x <= y ) AND ( y <= x )\n";
    expected += "then\n";
    expected += "  equal := 1\n";
    expected += "else\n";
    expected += "  equal := 0";
	  return expected;
	} 
	
	private Statement createProgram(){

    // If Condition 
    Expression x1 = new VariableExpression("x");
    Expression y1 = new VariableExpression("y");
    Condition c1 = new BinaryCondition(ConditionType.LE, x1, y1);

    Expression y2 = new VariableExpression("y");
    Expression x2 = new VariableExpression("x");
    Condition c2 = new BinaryCondition(ConditionType.LE, y2, x2);
    
    Condition condition = new BinaryConnective(ConnectiveType.AND, c1, c2);

    // Then Block
    VariableExpression equal1 = new VariableExpression("equal");
    IntegerExpression one = new IntegerExpression(1);
    Assignment thenBlock = new Assignment(equal1, one);

    // Else Block 
    VariableExpression equal2 = new VariableExpression("equal");
    IntegerExpression zero = new IntegerExpression(0);
    Assignment elseBlock = new Assignment(equal2, zero);

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
  void EqualityValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}