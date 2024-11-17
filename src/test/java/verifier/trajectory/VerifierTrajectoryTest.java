package verifier.trajectory;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierTrajectoryTest {
	private StatementSerializeVisitor visitor;
	private Statement program;
	
	private String expectedSerializedProgram(){
	  String expected = "if a == b\n";
    expected += "then\n";
    expected += "  trajectory := b\n";
    expected += "else\n";
    expected += "    changeInTrajectory := ( a * -1 ) + b\n";
    expected += "    trajectory := b + changeInTrajectory";
	  return expected;
	} 
	
	private Statement createProgram(){
	
    // Condition
    VariableExpression a1 = new VariableExpression("a");
    VariableExpression b1 = new VariableExpression("b");
    Condition condition = new BinaryCondition(ConditionType.EQUAL, a1, b1);

    // Then Block
    VariableExpression trajectory1 = new VariableExpression("trajectory");
    VariableExpression b2 = new VariableExpression("b");
    Assignment thenBlock = new Assignment(trajectory1, b2);

    // Else Block 
    // ElseBlockStatement1
    VariableExpression a2 = new VariableExpression("a");
    IntegerExpression negOne = new IntegerExpression(-1);
    Expression mul = new BinaryExpression(ExpressionType.MUL, a2, negOne);
    VariableExpression b3 = new VariableExpression("b");
    Expression add = new BinaryExpression(ExpressionType.ADD, mul, b3);
    VariableExpression changeInTrajectory1 = new VariableExpression("changeInTrajectory");
    Assignment changeInTrajectoryAssignment = new Assignment(changeInTrajectory1, add);

    // ElseBlockStatement2
    VariableExpression trajectory3 = new VariableExpression("trajectory");

    VariableExpression b4 = new VariableExpression("b");
    VariableExpression changeInTrajectory2 = new VariableExpression("changeInTrajectory");
    Expression add2 = new BinaryExpression(ExpressionType.ADD, b4, changeInTrajectory2);
    Assignment trajectoryAssignment = new Assignment(trajectory3, add2);

    Statement elseBlock = new Composition(changeInTrajectoryAssignment, trajectoryAssignment);

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
  void TrajectoryValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}