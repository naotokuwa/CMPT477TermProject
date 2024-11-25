package verifier.nor;

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

public class VerifierNorTest {
    private StatementSerializeVisitor programSerializer;
    private ConditionSerializeVisitor conditionSerializer;
    private Statement validProgram;
    private Statement invalidProgram;
    private Verifier verifier;

    private String expectedSerializedProgram(boolean valid) {
        String expected = "if a == 0\n";
        expected += "then\n";
        expected += "  if b == 0\n";
        expected += "  then\n";
        expected += "    nor := 1\n";
        expected += "  else\n";
        expected += "    nor := 0\n";
        expected += "else\n";
        expected += valid ? "  nor := 0" : "  nor := 1";
        return expected;
    }

    private Statement createProgram(boolean valid) {

        // -- Outer If Statement --
        // Outer Condition
        VariableExpression a1 = new VariableExpression("a");
        IntegerExpression zero1 = new IntegerExpression(0);
        Condition outerCondition = new BinaryCondition(ConditionType.EQUAL, a1, zero1);

        // -- Outer Then / Inner If Statement --
        // innerCondition
        VariableExpression b1 = new VariableExpression("b");
        IntegerExpression zero2 = new IntegerExpression(0);
        Condition innerCondition = new BinaryCondition(ConditionType.EQUAL, b1, zero2);
        // innerThenBlock
        VariableExpression nor1 = new VariableExpression("nor");
        IntegerExpression one1 = new IntegerExpression(1);
        Assignment innerThenBlock = new Assignment(nor1, one1);
        // innerElseBlock
        VariableExpression nor2 = new VariableExpression("nor");
        IntegerExpression zero3 = new IntegerExpression(0);
        Assignment innerElseBlock = new Assignment(nor2, zero3);

        Statement innerIfStatement = new If(innerCondition, innerThenBlock, innerElseBlock);

        // Outer Else
        VariableExpression nor3 = new VariableExpression("nor");
        IntegerExpression zero4 = valid ? new IntegerExpression(0) : new IntegerExpression(1);
        Assignment outerElseBlock = new Assignment(nor3, zero4);

        Statement program = new If(outerCondition, innerIfStatement, outerElseBlock);

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
    void NorValidNoPrecondition() {
        // Postcondition: a == 0 && b == 0 ==> nor == 1
        Condition postcondition1 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(0));
        Condition postcondition2 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(0));
        Condition postcondition3 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("nor"),
                new IntegerExpression(1));
        Condition implication1 = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);
        Condition implication2 = new BinaryConnective(ConnectiveType.IMPLIES, implication1, postcondition3);

        // Postcondition: (a == 1 || b == 1) ==> nor == 0
        Condition postcondition4 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("nor"),
                new IntegerExpression(0));
        Condition postcondition5 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(1));
        Condition postcondition6 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(1));
        Condition orCondition = new BinaryConnective(ConnectiveType.OR, postcondition5, postcondition6);
        Condition implication3 = new BinaryConnective(ConnectiveType.IMPLIES, orCondition, postcondition4);

        Condition combineConditions = new BinaryConnective(ConnectiveType.AND, implication2, implication3);

        combineConditions.accept(conditionSerializer);
        String expectedSerializedPost = "( ( ( a == 0 ) AND ( b == 0 ) ) ==> ( nor == 1 ) ) AND ( ( ( a == 1 ) OR ( b == 1 ) ) ==> ( nor == 0 ) )";

        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertTrue(verifier.verify(validProgram, combineConditions));
    }

    @Test
    void NorValidPrecondition() {
        // Preconditions: a == 1 || a == 0
        Condition precondition1 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(1));
        Condition precondition2 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(0));
        Condition orConditionA = new BinaryConnective(ConnectiveType.OR, precondition1, precondition2);

        // Preconditions: b == 1 || b == 0
        Condition precondition3 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(1));
        Condition precondition4 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(0));
        Condition orConditionB = new BinaryConnective(ConnectiveType.OR, precondition3, precondition4);

        Condition preconditions = new BinaryConnective(ConnectiveType.AND, orConditionA, orConditionB);

        // Postcondition: a == 0 && b == 0 ==> nor == 1
        Condition postcondition1 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(0));
        Condition postcondition2 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(0));
        Condition postcondition3 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("nor"),
                new IntegerExpression(1));
        Condition implication1 = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);
        Condition implication2 = new BinaryConnective(ConnectiveType.IMPLIES, implication1, postcondition3);

        // Postcondition: (a == 1 || b == 1) ==> nor == 0
        Condition postcondition4 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("nor"),
                new IntegerExpression(0));
        Condition postcondition5 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(1));
        Condition postcondition6 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(1));
        Condition orCondition = new BinaryConnective(ConnectiveType.OR, postcondition5, postcondition6);
        Condition implication3 = new BinaryConnective(ConnectiveType.IMPLIES, orCondition, postcondition4);

        Condition postconditions = new BinaryConnective(ConnectiveType.AND, implication2, implication3);

        // Check Pre and Post were properly made.
        preconditions.accept(conditionSerializer);
        String serializedPre = conditionSerializer.result;
        String expectedSerializedPre = "( ( a == 1 ) OR ( a == 0 ) ) AND ( ( b == 1 ) OR ( b == 0 ) )";
        assertEquals(expectedSerializedPre, serializedPre);

        postconditions.accept(conditionSerializer);
        String serializedPost = conditionSerializer.result;
        String expectedSerializedPost = "( ( ( a == 0 ) AND ( b == 0 ) ) ==> ( nor == 1 ) ) AND ( ( ( a == 1 ) OR ( b == 1 ) ) ==> ( nor == 0 ) )";
        assertEquals(expectedSerializedPost, serializedPost);

        assertTrue(verifier.verify(validProgram, preconditions, postconditions));
    }

    @Test
    void NorInvalidPost() {
        // Postcondition: a == 0 && b == 0 ==> nor == 0 (Incorrect postcondition)
        Condition postcondition1 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(0));
        Condition postcondition2 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(0));
        Condition postcondition3 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("nor"),
                new IntegerExpression(0)); // Incorrect postcondition
        Condition implication1 = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);
        Condition implication2 = new BinaryConnective(ConnectiveType.IMPLIES, implication1, postcondition3);

        // Postcondition: (a == 1 || b == 1) ==> nor == 0
        Condition postcondition4 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("nor"),
                new IntegerExpression(0));
        Condition postcondition5 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(1));
        Condition postcondition6 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(1));
        Condition orCondition = new BinaryConnective(ConnectiveType.OR, postcondition5, postcondition6);
        Condition implication3 = new BinaryConnective(ConnectiveType.IMPLIES, orCondition, postcondition4);

        Condition postconditions = new BinaryConnective(ConnectiveType.AND, implication2, implication3);

        postconditions.accept(conditionSerializer);
        String expectedSerializedPost = "( ( ( a == 0 ) AND ( b == 0 ) ) ==> ( nor == 0 ) ) AND ( ( ( a == 1 ) OR ( b == 1 ) ) ==> ( nor == 0 ) )";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertFalse(verifier.verify(invalidProgram, postconditions));

        /* Test counterexamples */
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        System.out.println(map);
        assertTrue(map.get("a") == 0 && map.get("b") == 0); // nor should return 0, but returns 1
    }

    @Test
    void NorIncorrect() {
        // Precondition: a == 1 || a == 0, b == 1 || b == 0
        Condition precondition1 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(1));
        Condition precondition2 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(0));
        Condition preconditions1 = new BinaryConnective(ConnectiveType.OR, precondition1, precondition2);

        Condition precondition3 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(1));
        Condition precondition4 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(0));
        Condition preconditions2 = new BinaryConnective(ConnectiveType.OR, precondition3, precondition4);

        Condition preconditions = new BinaryConnective(ConnectiveType.AND, preconditions1, preconditions2);

        // Postcondition: a == 0 && b == 0 ==> nor == 1
        Condition postcondition1 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(0));
        Condition postcondition2 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(0));
        Condition postcondition3 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("nor"),
                new IntegerExpression(1)); // Correct postcondition
        Condition implication1 = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);
        Condition implication2 = new BinaryConnective(ConnectiveType.IMPLIES, implication1, postcondition3);

        // Postcondition: (a == 1 || b == 1) ==> nor == 0
        Condition postcondition4 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("nor"),
                new IntegerExpression(0));
        Condition postcondition5 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new IntegerExpression(1));
        Condition postcondition6 = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("b"),
                new IntegerExpression(1));
        Condition orCondition = new BinaryConnective(ConnectiveType.OR, postcondition5, postcondition6);
        Condition implication3 = new BinaryConnective(ConnectiveType.IMPLIES, orCondition, postcondition4);

        Condition postconditions = new BinaryConnective(ConnectiveType.AND, implication2, implication3);

        // Check Pre and Post were properly made.
        preconditions.accept(conditionSerializer);
        String serializedPre = conditionSerializer.result;
        String expectedSerializedPre = "( ( a == 1 ) OR ( a == 0 ) ) AND ( ( b == 1 ) OR ( b == 0 ) )";
        assertEquals(expectedSerializedPre, serializedPre);

        postconditions.accept(conditionSerializer);
        String expectedSerializedPost = "( ( ( a == 0 ) AND ( b == 0 ) ) ==> ( nor == 1 ) ) AND ( ( ( a == 1 ) OR ( b == 1 ) ) ==> ( nor == 0 ) )";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertFalse(verifier.verify(invalidProgram, preconditions, postconditions));

        /* Test counterexamples */
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        System.out.println(map);
        assertTrue(map.get("a") == 1 && map.get("b") == 0); // nor should return 0, but returns 1
    }
}