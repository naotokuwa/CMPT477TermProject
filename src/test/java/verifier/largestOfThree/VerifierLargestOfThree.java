package verifier.largestOfThree;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierLargestOfThree {
	private StatementSerializeVisitor visitor;
	private Statement program;
	
	private String expectedSerializedProgram(){
	  String expected = "largest := x\n";
    expected += "if largest <= y\n";
    expected += "then\n";
    expected += "  largest := y\n";
    expected += "else\n";
    expected += "  tmp := 1\n";
    expected += "if largest <= z\n";
    expected += "then\n";
    expected += "  largest := z\n";
    expected += "else\n";
    expected += "  tmp := 1";
	  return expected;
	} 
	
  private Statement createMeaningLessStatement(){
    VariableExpression tmp = new VariableExpression("tmp");
    Expression one  = new IntegerExpression(1);
    Assignment statement = new Assignment(tmp, one);
    return statement;
}

	private Statement createProgram(){
    
    // -- statement 1 -- 
    VariableExpression largest1 = new VariableExpression("largest");
    VariableExpression x1  = new VariableExpression("x");
    Assignment statement1 = new Assignment(largest1, x1);

    // -- statement 2 -- 
    // statement2Condition
    VariableExpression largest2 = new VariableExpression("largest");
    VariableExpression y1 = new VariableExpression("y");
    Condition statement2Condition = new BinaryCondition(ConditionType.LE, largest2, y1);
    // statement2ThenBlock
    VariableExpression largest3 = new VariableExpression("largest");
    VariableExpression y2 = new VariableExpression("y");
    Assignment statement2ThenBlock = new Assignment(largest3, y2);
    // statement2ElseBlock
    Statement statement2ElseBlock = createMeaningLessStatement();
    // statement2IfStatement
    Statement statement2IfStatement = new If(statement2Condition, statement2ThenBlock, statement2ElseBlock);

    // -- statement 3 -- 
    // statement3Condition
    VariableExpression largest4 = new VariableExpression("largest");
    VariableExpression z1 = new VariableExpression("z");
    Condition statement3Condition = new BinaryCondition(ConditionType.LE, largest4, z1);
    // statement3ThenBlock
    VariableExpression largest5 = new VariableExpression("largest");
    VariableExpression z2 = new VariableExpression("z");
    Assignment statement3ThenBlock = new Assignment(largest5, z2);
    // statement3ElseBlock
    Statement statement3ElseBlock = createMeaningLessStatement();
    // statement3IfStatement
    Statement statement3IfStatement = new If(statement3Condition, statement3ThenBlock, statement3ElseBlock);

    Statement comp1 = new Composition(statement1, statement2IfStatement);
    Statement comp2 = new Composition(comp1, statement3IfStatement);
		Statement program = comp2;
		
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
  void LargestOfThreeValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}
