package verifier.age;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierAgeTest {
	private StatementSerializeVisitor visitor;
	private Statement program;
	
	private String expectedSerializedProgram(){
	  String expected = "year := 2024\n";
    expected += "age := 2024 + ( -1 * birthyear )";
	  return expected;
	} 
	
	private Statement createProgram(){

    // Statement 1
    VariableExpression year = new VariableExpression("year");
    IntegerExpression currentyear1 = new IntegerExpression(2024);
    Assignment statement1 = new Assignment(year, currentyear1);
    
    // Statement 2
    IntegerExpression negativeOne = new IntegerExpression(-1);
    VariableExpression birthyear = new VariableExpression("birthyear");
    BinaryExpression negativeBirthyear = new BinaryExpression(ExpressionType.MUL, negativeOne, birthyear);
    IntegerExpression currentyear2 = new IntegerExpression(2024);
    BinaryExpression calculateAge = new BinaryExpression(ExpressionType.ADD, currentyear2, negativeBirthyear);
    VariableExpression age = new VariableExpression("age");
    Assignment statement2 = new Assignment(age, calculateAge);
    
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
  void AgeValid1(){
    System.out.println("TO BE IMPLEMENTED");
  }
}