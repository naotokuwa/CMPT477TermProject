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
	  String expected = "nor := NOT(a OR b )";
	  return expected;
	} 
	
	private Statement createProgram(){
    
    VariableExpression nor = new VariableExpression("nor");
    Expression a = new VariableExpression("a");
    Expression b = new VariableExpression("b");
    Condition c = new BinaryConnective(ConnectiveType.OR, a, b);


    


		Statement program = new Statement();
		
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