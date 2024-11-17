package z3;

import com.microsoft.z3.Context;
import imp.condition.*;
import imp.condition.Boolean;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.visitor.z3.ConditionZ3Visitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the getCounterexample methods of ConditionZ3Visitor.
 *
 * @see imp.visitor.z3.ConditionZ3Visitor#getCounterexampleAsString(Condition)
 * @see imp.visitor.z3.ConditionZ3Visitor#getCounterexampleAsMap(Condition)
 */
public class CounterExampleTest {
    private Context ctx;
    private ConditionZ3Visitor visitor;

    @BeforeEach
    public void setUp() {
        ctx = new Context();
        visitor = new ConditionZ3Visitor(ctx);
    }

    @AfterEach
    public void tearDown() {
        ctx.close();
    }

    @Test
    public void testSingleVariable() {
        // Test Scenario: x <= 0 is not valid
        VariableExpression x = new VariableExpression("x");
        IntegerExpression zero = new IntegerExpression(0);
        BinaryCondition x_le_0 = new BinaryCondition(ConditionType.LE, x, zero);

        Map<String, Integer> symbolToVal = visitor.getCounterexampleAsMap(x_le_0);
        assertTrue(symbolToVal.get("x") > 0);
    }

    @Test
    public void testMultipleVariables() {
        // Test Scenario: ( x = 0 ) AND ( y = 0 ) AND ( z = 0 ) is not valid
        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");
        VariableExpression z = new VariableExpression("z");
        IntegerExpression zero = new IntegerExpression(0);

        BinaryCondition x_eq_y = new BinaryCondition(ConditionType.EQUAL, x, zero);
        BinaryCondition y_eq_z = new BinaryCondition(ConditionType.EQUAL, y, zero);
        BinaryCondition x_eq_z = new BinaryCondition(ConditionType.EQUAL, z, zero);
        BinaryConnective and = new BinaryConnective(ConnectiveType.AND, x_eq_y, y_eq_z);
        and = new BinaryConnective(ConnectiveType.AND, and, x_eq_z);

        Map<String, Integer> symbolToVal = visitor.getCounterexampleAsMap(and);

        boolean expected = symbolToVal.get("x") != 0
                        || symbolToVal.get("y") != 0
                        || symbolToVal.get("z") != 0;
        assertTrue(expected);
    }

    @Test
    public void testValidCondition() {
        // Test Scenario: TRUE is valid
        Boolean t = new Boolean(true);
        String counterExample = visitor.getCounterexampleAsString(t);
        String expected = "";
        assertEquals(expected, counterExample);
    }

    @Test
    public void testStringOutput() {
        // Using a simple test case forces the output to be determinate and
        // makes testing strings easier.
        // Test Scenario: NOT( x = 0 )
        VariableExpression x = new VariableExpression("x");
        IntegerExpression zero = new IntegerExpression(0);
        BinaryCondition x_eq_zero = new BinaryCondition(ConditionType.EQUAL, x, zero);
        UnaryConnective x_ne_zero = new UnaryConnective(ConnectiveType.NOT, x_eq_zero);

        String stringOutput = visitor.getCounterexampleAsString(x_ne_zero);
        String expected = """
                Counterexample:
                x = 0
                """;

        assertEquals(expected, stringOutput);
    }
}
