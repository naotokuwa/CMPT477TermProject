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
	  String expected = "if x <= y\n";
    expected += "then\n";
    expected += "  if y <= x\n";
    expected += "  then\n";
    expected += "    equal := 1\n";
    expected += "  else\n";
    expected += "    equal := 0\n";
    expected += "else\n";
    expected += "  equal := 0";
    return expected;
	} 
	
	private Statement createProgram(){

    // -- Outer If Statement -- 
    // Outer Condition 
    Expression x1 = new VariableExpression("x");
    Expression y1 = new VariableExpression("y");
    Condition outerCondition = new BinaryCondition(ConditionType.LE, x1, y1);

    // -- Outer Then / Inner If Statement --
    // innerCondition
    VariableExpression y2 = new VariableExpression("y");
    VariableExpression x2 = new VariableExpression("x");
    Condition innerCondition = new BinaryCondition(ConditionType.LE, y2, x2);
    // innerThenBlock
    VariableExpression equal2 = new VariableExpression("equal");
    IntegerExpression one1 = new IntegerExpression(1);
    Assignment innerThenBlock = new Assignment(equal2, one1);
    // innerElseBlock
    VariableExpression equal3 = new VariableExpression("equal");
    IntegerExpression zero2 = new IntegerExpression(0);
    Assignment innerElseBlock = new Assignment(equal3, zero2);
    
    Statement innerIfStatement = new If(innerCondition, innerThenBlock, innerElseBlock);

    // Outer Else 
    VariableExpression equal1 = new VariableExpression("equal");
    IntegerExpression zero1 = new IntegerExpression(0);
    Assignment outerElseBlock = new Assignment(equal1, zero1);

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
  void EqualityValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}