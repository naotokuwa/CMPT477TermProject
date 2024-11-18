package verifier.min;

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

public class VerifierMinTest {
    private StatementSerializeVisitor programSerializer;
    private ConditionSerializeVisitor conditionSerializer;
    private Statement validProgram;
    private Statement invalidProgram;
    private Verifier verifier;

    private String expectedSerializedProgram(boolean valid) {
        String expected = "minVal := x\n";
        expected += valid ? "if y <= minVal\n" : "if y == minVal\n";
        expected += "then\n";
        expected += "  minVal := y\n";
        expected += "else\n";
        expected += "  tmp := 1";

        return expected;
    }

    private Statement createMeaningLessStatement() {
        VariableExpression tmp = new VariableExpression("tmp");
        Expression one  = new IntegerExpression(1);
        return new Assignment(tmp, one);
    }

    private Statement createProgram(boolean valid) {
        // First line
        VariableExpression minVal1 = new VariableExpression("minVal");
        Expression x1 = new VariableExpression("x");
        Statement firstLine = new Assignment(minVal1, x1);
        
        // If condition
        Expression y1  = new VariableExpression("y");
        Expression minVal2 = new VariableExpression("minVal");
        Condition condition = valid ? new BinaryCondition(ConditionType.LE, y1, minVal2)
                                    : new BinaryCondition(ConditionType.EQUAL, y1, minVal2);

        // Then block
        VariableExpression minVal3 = new VariableExpression("minVal");
        Expression y2  = new VariableExpression("y");
        Assignment thenBlock = new Assignment(minVal3, y2);

        // Else block
        // We don't need to use a else block as you can tell in Dafny implementation.
        // So, just make a meaningless program which does not affect the semantic
        // of program and put it to else branch
        Statement elseBlock = createMeaningLessStatement();

        // If statement
        Statement ifStatement = new If(condition, thenBlock, elseBlock);
        Statement program = new Composition(firstLine, ifStatement);

        // Verify program serialization
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
    void MinValidNoPrecondition() {
        // Postcondition:
        // minVal == x || minVal == y
        // minVal <= x && minVal <= y
        BinaryConnective postcondition = getPostcondition1();

        boolean isValid = verifier.verify(validProgram, postcondition);
        assertTrue(isValid);
    }

    @Test
    void MinValidWithPrecondition() {
        // Precondition: x == y
        // Postcondition: minVal == x && minVal == y
        VariableExpression minVal = new VariableExpression("minVal");
        Expression x = new VariableExpression("x");
        Expression y  = new VariableExpression("y");
        BinaryCondition precondition = new BinaryCondition(ConditionType.EQUAL, x, y);
        BinaryCondition x_eq_minVal = new BinaryCondition(ConditionType.EQUAL, minVal, x);
        BinaryCondition y_eq_minVal = new BinaryCondition(ConditionType.EQUAL, minVal, y);
        BinaryConnective postcondition = new BinaryConnective(ConnectiveType.AND, x_eq_minVal, y_eq_minVal);

        precondition.accept(conditionSerializer);
        String serializedPre = conditionSerializer.result;
        postcondition.accept(conditionSerializer);
        String serializedPost = conditionSerializer.result;
        String expectedSerializedPre = "x == y";
        String expectedSerializedPost = "( minVal == x ) AND ( minVal == y )";

        assertEquals(expectedSerializedPre, serializedPre);
        assertEquals(expectedSerializedPost, serializedPost);

        boolean isValid = verifier.verify(validProgram, precondition, postcondition);
        assertTrue(isValid);
    }

    @Test
    void MinInvalidNoPrecondition() {
        // Postcondition: minVal == x && minVal == y
        VariableExpression minVal = new VariableExpression("minVal");
        Expression x = new VariableExpression("x");
        Expression y  = new VariableExpression("y");
        BinaryCondition x_eq_minVal = new BinaryCondition(ConditionType.EQUAL, minVal, x);
        BinaryCondition y_eq_minVal = new BinaryCondition(ConditionType.EQUAL, minVal, y);
        BinaryConnective postcondition = new BinaryConnective(ConnectiveType.AND, x_eq_minVal, y_eq_minVal);

        boolean isValid = verifier.verify(validProgram, postcondition);
        assertFalse(isValid);

        postcondition.accept(conditionSerializer);
        String expectedSerializedPre =
                "( minVal == x ) AND ( minVal == y )";
        assertEquals(expectedSerializedPre, conditionSerializer.result);

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertNotEquals(map.get("x"), map.get("y"));
    }

    @Test
    void MinInvalidWithPrecondition() {
        // Precondition: x <= 0
        // Postcondition: x == minVal
        Expression x1 = new VariableExpression("x");
        Expression x2 = new VariableExpression("x");
        IntegerExpression zero = new IntegerExpression(0);
        VariableExpression minVal = new VariableExpression("minVal");
        BinaryCondition precondition = new BinaryCondition(ConditionType.LE, x1, zero);
        BinaryCondition postcondition = new BinaryCondition(ConditionType.EQUAL, x2, minVal);

        boolean isValid = verifier.verify(validProgram, precondition, postcondition);
        assertFalse(isValid);

        precondition.accept(conditionSerializer);
        String serializedPre = conditionSerializer.result;
        postcondition.accept(conditionSerializer);
        String serializedPost = conditionSerializer.result;
        String expectedSerializedPre = "x <= 0";
        String expectedSerializedPost = "x == minVal";

        assertEquals(expectedSerializedPre, serializedPre);
        assertEquals(expectedSerializedPost, serializedPost);

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.get("x") > map.get("y"));
    }

    @Test
    void InvalidProgramValidSpec() {
        // Same postcondition as MinValidNoPrecondition() but with an invalid program
        BinaryConnective postcondition = getPostcondition1();


        boolean isValid = verifier.verify(invalidProgram, postcondition);
        assertFalse(isValid);

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        int minVal = Math.min(map.get("x"), map.get("y"));
        assertTrue(map.get("x") != minVal || map.get("y") != minVal);
    }

    private BinaryConnective getPostcondition1() {
        // Postcondition:
        // minVal == x || minVal == y
        // minVal <= x && minVal <= y
        VariableExpression minVal = new VariableExpression("minVal");
        Expression x = new VariableExpression("x");
        Expression y  = new VariableExpression("y");
        BinaryCondition x_eq_minVal = new BinaryCondition(ConditionType.EQUAL, minVal, x);
        BinaryCondition y_eq_minVal = new BinaryCondition(ConditionType.EQUAL, minVal, y);
        BinaryConnective condition1 = new BinaryConnective(ConnectiveType.OR, x_eq_minVal, y_eq_minVal);
        BinaryCondition minVal_le_x = new BinaryCondition(ConditionType.LE, minVal, x);
        BinaryCondition minVal_le_y = new BinaryCondition(ConditionType.LE, minVal, y);
        BinaryConnective condition2 = new BinaryConnective(ConnectiveType.AND, minVal_le_x, minVal_le_y);
        BinaryConnective postcondition = new BinaryConnective(ConnectiveType.AND, condition1, condition2);

        postcondition.accept(conditionSerializer);
        String expectedSerializedPre =
                "( ( minVal == x ) OR ( minVal == y ) ) AND ( ( minVal <= x ) AND ( minVal <= y ) )";
        assertEquals(expectedSerializedPre, conditionSerializer.result);

        return postcondition;
    }
}