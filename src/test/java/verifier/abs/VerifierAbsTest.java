package verifier.abs;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.ConditionSerializeVisitor;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verifier.Verifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VerifierAbsTest {
    private StatementSerializeVisitor programSerializer;
    private ConditionSerializeVisitor conditionSerializer;
    private Statement validProgram;
    private Statement invalidProgram;
    private Verifier verifier;

    // Expected Serialized Abs IMP Program
    private String expectedSerializedProgram(boolean valid) {
      String expected = valid ? "if x <= 0\n" : "if x == 0\n";
      expected += "then\n";
      expected += "  y := x * -1\n";
      expected += "else\n";
      expected += "  y := x";
      return expected;
    }

    private Statement createProgram(boolean valid) {

      // IF Condition
      Expression x1 = new VariableExpression("x");
      Expression zero = new IntegerExpression(0);
      Condition condition = valid ? new BinaryCondition(ConditionType.LE, x1, zero)
                                  : new BinaryCondition(ConditionType.EQUAL, x1, zero);

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
      program.accept(programSerializer);
      String result = programSerializer.result;
      String expected = expectedSerializedProgram(valid);

      assertEquals(expected, result);

      return program;
    }

    @BeforeEach public void setUp() {
      programSerializer = new StatementSerializeVisitor();
      conditionSerializer = new ConditionSerializeVisitor();
      this.validProgram = createProgram(true);
      this.invalidProgram = createProgram(false);
      verifier = new Verifier();
    }
    
    @Test
    void AbsValidNoPrecondition() {
        // Postcondition: 0 <= y
        Condition postcondition = new BinaryCondition(ConditionType.LE,
                                  new IntegerExpression(0),
                                  new VariableExpression("y"));

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "0 <= y";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertTrue(verifier.verify(validProgram, postcondition));
    }

    @Test
    void AbsValidWithPrecondition() {
        // Precondition: x <= 0
        // Postcondition: 0 <= y
        Condition precondition = new BinaryCondition(ConditionType.LE,
                                 new VariableExpression("x"),
                                 new IntegerExpression(0));
        Condition postcondition = new BinaryCondition(ConditionType.LE,
                                  new IntegerExpression(0),
                                  new VariableExpression("y"));

        precondition.accept(conditionSerializer);
        String serializedPre = conditionSerializer.result;
        postcondition.accept(conditionSerializer);
        String serializedPost = conditionSerializer.result;
        String expectedSerializedPre = "x <= 0";
        String expectedSerializedPost = "0 <= y";

        assertEquals(expectedSerializedPre, serializedPre);
        assertEquals(expectedSerializedPost, serializedPost);

        assertTrue(verifier.verify(validProgram, precondition, postcondition));
    }

    @Test
    void AbsInvalidNoPrecondition() {
        // Postcondition: y <= 0
        Condition postcondition = new BinaryCondition(ConditionType.LE,
                                  new VariableExpression("y"),
                                  new IntegerExpression(0));

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "y <= 0";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertFalse(verifier.verify(validProgram, postcondition));

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.containsKey("x"));
    }

    @Test
    void AbsInvalidWithPrecondition() {
        // Precondition: x == -1
        // Postcondition: y <= 0
        Condition precondition = new BinaryCondition(ConditionType.EQUAL,
                                 new VariableExpression("x"),
                                 new IntegerExpression(-1));
        Condition postcondition = new BinaryCondition(ConditionType.LE,
                                  new VariableExpression("y"),
                                  new IntegerExpression(0));

        assertFalse(verifier.verify(validProgram, precondition, postcondition));

        precondition.accept(conditionSerializer);
        String serializedPre = conditionSerializer.result;
        postcondition.accept(conditionSerializer);
        String serializedPost = conditionSerializer.result;
        String expectedSerializedPre = "x == -1";
        String expectedSerializedPost = "y <= 0";

        assertEquals(expectedSerializedPre, serializedPre);
        assertEquals(expectedSerializedPost, serializedPost);

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertEquals(-1, map.get("x"));
    }

    @Test
    public void InvalidProgramValidSpec() {
        // Same postcondition as AbsValidNoPrecondition() but with an invalid program
        // Postcondition: 0 <= y
        Condition postcondition = new BinaryCondition(ConditionType.LE,
                new IntegerExpression(0),
                new VariableExpression("y"));

        boolean isValid = verifier.verify(invalidProgram, postcondition);
        assertFalse(isValid);

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.get("x") < 0);
    }
}