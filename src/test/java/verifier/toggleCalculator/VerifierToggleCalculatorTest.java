package verifier.toggleCalculator;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierToggleCalculatorTest {
	private StatementSerializeVisitor visitor;
	private Statement program;
	
	private String expectedSerializedProgram(){
	  String expected = "if toggleAddMul == 1\n";
    expected += "then\n";
    expected += "  result := a + b\n";
    expected += "else\n";
    expected += "  result := a * b";
	  return expected;
	} 
	
	private Statement createProgram(){

    // Condition
    VariableExpression toggleAddMul1 = new VariableExpression("toggleAddMul");
    IntegerExpression one1 = new IntegerExpression(1);
    Condition condition = new BinaryCondition(ConditionType.EQUAL, toggleAddMul1, one1);

    // Then Block
    VariableExpression result1 = new VariableExpression("result");
    
    VariableExpression a1 = new VariableExpression("a");
    VariableExpression b1 = new VariableExpression("b");
    Expression add = new BinaryExpression(ExpressionType.ADD, a1, b1);

    Assignment thenBlock = new Assignment(result1, add);

    // Else Block 
    VariableExpression result2 = new VariableExpression("result");
    
    VariableExpression a2 = new VariableExpression("a");
    VariableExpression b2 = new VariableExpression("b");
    Expression mul = new BinaryExpression(ExpressionType.MUL, a2, b2);

    Assignment elseBlock = new Assignment(result2, mul);

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
  void ToggleCalculatorValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}