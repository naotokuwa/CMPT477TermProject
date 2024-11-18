package verifier.truefalsecondition;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.condition.Boolean;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierTrueFalseConditionTest {
	private StatementSerializeVisitor visitor;
	private Statement program;
	
	private String expectedSerializedProgram(){
	  String expected = "if true\n";
    expected += "then\n";
    expected += "  y := x\n";
    expected += "else\n";
    expected += "  y := x + 1\n";
    expected += "if false\n";
    expected += "then\n";
    expected += "  y := y\n";
    expected += "else\n";
    expected += "  y := y + 1";
	  return expected;
	} 
	
	private Statement createProgram(){
	
    // If Statement 1 
    Condition condition1 = new Boolean(true);

    VariableExpression y1 = new VariableExpression("y");
    VariableExpression x1 = new VariableExpression("x");
    Assignment thenAssign1 = new Assignment(y1, x1);

    VariableExpression y2 = new VariableExpression("y");
    VariableExpression x2 = new VariableExpression("x");
    IntegerExpression one1 = new IntegerExpression(1);
    BinaryExpression xPlusOne = new BinaryExpression(ExpressionType.ADD, x2, one1);
    Assignment elseAssign1 = new Assignment(y2, xPlusOne);

    Statement statement1 = new If(condition1, thenAssign1, elseAssign1);


    // If Statement 2 
    Condition condition2 = new Boolean(false);

    VariableExpression y3 = new VariableExpression("y");
    VariableExpression y4 = new VariableExpression("y");
    Assignment thenAssign2 = new Assignment(y3, y4);

    VariableExpression y5 = new VariableExpression("y");
    VariableExpression y6 = new VariableExpression("y");
    IntegerExpression one2 = new IntegerExpression(1);
    BinaryExpression yPlusOne = new BinaryExpression(ExpressionType.ADD, y6, one2);
    Assignment elseAssign2 = new Assignment(y5, yPlusOne);

    Statement statement2 = new If(condition2, thenAssign2, elseAssign2);
		
    Statement program = new Composition(statement1, statement2);

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
  void TODOValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}