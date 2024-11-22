package verifier.truefalsecondition;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.condition.Boolean;
import imp.visitor.serialize.ConditionSerializeVisitor;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verifier.Verifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VerifierTrueFalseConditionTest {
    private StatementSerializeVisitor programSerializer;
    private ConditionSerializeVisitor conditionSerializer;
    private Statement validProgram;
    private Statement invalidProgram;
    private Verifier verifier;

    private String expectedSerializedProgram(boolean valid) {
        String expected = "if true\n";
        expected += "then\n";
        expected += "  y := x\n";
        expected += "else\n";
        expected += "  y := x + 1\n";
        expected += "if false\n";
        expected += "then\n";
        expected += "  y := y\n";
        expected += "else\n";
        expected += valid ? "  y := y + 1" : "  y := y + 2";
        return expected;
    }

    private Statement createProgram(boolean valid) {

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
        IntegerExpression one2 = valid ? new IntegerExpression(1)
                                       : new IntegerExpression(2);
        BinaryExpression yPlusOne = new BinaryExpression(ExpressionType.ADD, y6, one2);
        Assignment elseAssign2 = new Assignment(y5, yPlusOne);

        Statement statement2 = new If(condition2, thenAssign2, elseAssign2);

        Statement program = new Composition(statement1, statement2);

        // Verify program serialization
        program.accept(programSerializer);
        String result = programSerializer.result;
        String expected = expectedSerializedProgram(valid);

        assertEquals(expected, result);

        return program;
    }

    @BeforeEach
    public void setUp() {
      programSerializer = new StatementSerializeVisitor();
      conditionSerializer = new ConditionSerializeVisitor();
      this.validProgram = createProgram(true);
      this.invalidProgram = createProgram(false);
      verifier = new Verifier();
    }

    @Test
    void TrueFalseConditionValidNoPrecondition() {
        // Postcondition: y == x + 1
        VariableExpression y1 = new VariableExpression("y");
        VariableExpression x1 = new VariableExpression("x");
        IntegerExpression one1 = new IntegerExpression(1);

        BinaryExpression xPlusOne = new BinaryExpression(ExpressionType.ADD, x1, one1);
        BinaryCondition yIsXPlusOne = new BinaryCondition(ConditionType.EQUAL, y1, xPlusOne);

        Condition postcondition = yIsXPlusOne;

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "y == x + 1";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertTrue(verifier.verify(validProgram, postcondition));
    }

    @Test
    void TrueFalseConditionValidWithPrecondition() {
        // Precondition: x <= 0
        // Postcondition: y == x + 1

        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");
        IntegerExpression zero = new IntegerExpression(0);
        IntegerExpression one = new IntegerExpression(1);

        BinaryCondition precondition = new BinaryCondition(ConditionType.LE, x, zero);
        BinaryExpression xPlusOne = new BinaryExpression(ExpressionType.ADD, x, one);
        BinaryCondition postcondition = new BinaryCondition(ConditionType.EQUAL, y, xPlusOne);

        precondition.accept(conditionSerializer);
        String serializedPre = conditionSerializer.result;
        postcondition.accept(conditionSerializer);
        String serializedPost = conditionSerializer.result;

        String expectedSerializedPre = "x <= 0";
        String expectedSerializedPost = "y == x + 1";

        assertEquals(expectedSerializedPre, serializedPre);
        assertEquals(expectedSerializedPost, serializedPost);

        assertTrue(verifier.verify(validProgram, precondition, postcondition));
    }

    @Test
    void TrueFalseConditionInvalidPostcondition() {
        // Postcondition: y == x (Invalid)

        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");
        BinaryCondition postcondition = new BinaryCondition(ConditionType.EQUAL, y, x);

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "y == x";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertFalse(verifier.verify(validProgram, postcondition));

        /* Test counterexamples */
        // String counterexampleString = verifier.getCounterexampleString();
        // System.out.println("Counterexample String: " + counterexampleString);
        // assertNotEquals("", counterexampleString);

        // Map<String, Integer> map = verifier.getCounterexampleMap();
        // System.out.println("Counterexample Map: " + map);
        // assertTrue(map.containsKey("x"));
        // assertTrue(map.containsKey("y"));
    }

    @Test
    void TrueFalseConditionInvalidWithPrecondition() {
        // Precondition: x <= 0
        // Postcondition: y == x (Invalid)

        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");
        IntegerExpression zero = new IntegerExpression(0);

        BinaryCondition precondition = new BinaryCondition(ConditionType.LE, x, zero);
        BinaryCondition postcondition = new BinaryCondition(ConditionType.EQUAL, y, x);

        precondition.accept(conditionSerializer);
        String serializedPre = conditionSerializer.result;
        postcondition.accept(conditionSerializer);
        String serializedPost = conditionSerializer.result;

        String expectedSerializedPre = "x <= 0";
        String expectedSerializedPost = "y == x";

        assertEquals(expectedSerializedPre, serializedPre);
        assertEquals(expectedSerializedPost, serializedPost);

        assertFalse(verifier.verify(validProgram, precondition, postcondition));

        /* Test counterexamples */
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.containsKey("x"));
        // assertTrue(map.containsKey("y"));
    }

    @Test
    void TrueFalseConditionInvalidProgramValidSpec() {
        // Postcondition: y == x + 1

        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");
        IntegerExpression one = new IntegerExpression(1);

        BinaryExpression xPlusOne = new BinaryExpression(ExpressionType.ADD, x, one);
        BinaryCondition postcondition = new BinaryCondition(ConditionType.EQUAL, y, xPlusOne);

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "y == x + 1";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        boolean isValid = verifier.verify(invalidProgram, postcondition);
        assertFalse(isValid);

        /* Test counterexamples */
        // String counterexampleString = verifier.getCounterexampleString();
        // assertNotEquals("", counterexampleString);

        // Map<String, Integer> map = verifier.getCounterexampleMap();
        // assertTrue(map.containsKey("x"));
    }

}