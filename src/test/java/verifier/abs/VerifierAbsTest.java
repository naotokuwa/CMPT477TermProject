package verifier.abs;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierAbsTest {
    private StatementSerializeVisitor visitor;
    private Statement program;

    // Expected Serialized Abs IMP Program
    private String expectedSerializedProgram() {
      String expected = "if x <= 0\n";
      expected += "then\n";
      expected += "  y := x * -1\n";
      expected += "else\n";
      expected += "  y := x";
      return expected;
    }

    private Statement createProgram() {

      // IF Condition
      Expression x1 = new VariableExpression("x");
      Expression zero = new IntegerExpression(0);
      Condition condition = new BinaryCondition(ConditionType.LE, x1, zero);

      // THEN Block 
      VariableExpression y1 = new VariableExpression("y");
      Expression x2 = new VariableExpression("x");
      Expression negOne = new IntegerExpression(-1);
      Expression negx = new BinaryExpression(ExpressionType.MUL, x2, negOne);
      Assignment thenBlock = new Assignment(y1, negx);

      // ELSE Block
      VariableExpression y2 = new VariableExpression("y");
      Expression x3 = new VariableExpression("x");
      Assignment elseBlock = new Assignment(y2, x3);
      
      // IF Statement 
      Statement program = new If(condition, thenBlock, elseBlock);

      // Verify Program Serialization
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
    void AbsValid1(){
      System.out.println("TO BE IMPLEMENTED");
    }
}