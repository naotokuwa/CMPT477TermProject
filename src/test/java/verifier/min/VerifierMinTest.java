package verifier.min;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verifier.Verifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VerifierMinTest {
    private StatementSerializeVisitor visitor;
    private Statement program;
    private Verifier verifier;

    private String expectedSerializedProgram(){
        String expected = "minVal := x\n";
        expected += "if y <= minVal\n";
        expected += "then\n";
        expected += "  minVal := y\n";
        expected += "else\n";
        expected += "  tmp := 1";

        return expected;
    }

    private Statement createMeaningLessStatement(){
        VariableExpression tmp = new VariableExpression("tmp");
        Expression one  = new IntegerExpression(1);
        return new Assignment(tmp, one);
    }

    private Statement createProgram(){
        // First line
        VariableExpression minVal1 = new VariableExpression("minVal");
        Expression x1 = new VariableExpression("x");
        Statement firstLine = new Assignment(minVal1, x1);
        
        // If condition
        Expression y1  = new VariableExpression("y");
        Expression minVal2 = new VariableExpression("minVal");
        Condition condition = new BinaryCondition(ConditionType.LE, y1, minVal2);

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
        program.accept(visitor);
        String result = visitor.result;
        String expected = expectedSerializedProgram();

        assertEquals(expected, result);

        return program;
    }

    @BeforeEach public void setUp() {
        visitor = new StatementSerializeVisitor();
        this.program = createProgram();
        verifier = new Verifier();
    }
    
    @Test
    void MinValidNoPrecondition(){
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

        boolean isValid = verifier.verify(program, postcondition);
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

        boolean isValid = verifier.verify(program, precondition, postcondition);
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

        boolean isValid = verifier.verify(program, postcondition);
        assertFalse(isValid);

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

        boolean isValid = verifier.verify(program, precondition, postcondition);
        assertFalse(isValid);

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.get("x") > map.get("y"));
    }
}