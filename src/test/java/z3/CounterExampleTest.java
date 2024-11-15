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

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the getCounterExample method of ConditionZ3Visitor.
 *
 * @see imp.visitor.z3.ConditionZ3Visitor#getCounterExample(Condition)
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

        String counterExample = visitor.getCounterExample(x_le_0);
        Map<String, Integer> symbolToVal = parseCounterexample(counterExample);

        assertTrue(symbolToVal.get("x") > 0);
    }

    @Test
    public void testMultipleVariables() {
        // Test Scenario: ( x = y ) AND ( y = z ) ==> NOT( x = z ) is not valid
        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");
        VariableExpression z = new VariableExpression("z");

        BinaryCondition x_eq_y = new BinaryCondition(ConditionType.EQUAL, x, y);
        BinaryCondition y_eq_z = new BinaryCondition(ConditionType.EQUAL, y, z);
        BinaryCondition x_eq_z = new BinaryCondition(ConditionType.EQUAL, x, z);
        UnaryConnective x_neq_q = new UnaryConnective(ConnectiveType.NOT, x_eq_z);
        BinaryConnective premise = new BinaryConnective(ConnectiveType.AND, x_eq_y, y_eq_z);
        BinaryConnective implies = new BinaryConnective(ConnectiveType.IMPLIES, premise, x_neq_q);

        String counterExample = visitor.getCounterExample(implies);
        Map<String, Integer> symbolToVal = parseCounterexample(counterExample);
        assertEquals(symbolToVal.get("x"), symbolToVal.get("y"));
        assertEquals(symbolToVal.get("y"), symbolToVal.get("z"));
        assertNotEquals(symbolToVal.get("x"), symbolToVal.get("z"));
    }

    @Test
    public void testValidCondition() {
        // Test Scenario: TRUE is valid
        Boolean t = new Boolean(true);
        String counterExample = visitor.getCounterExample(t);
        String expected = "";
        assertEquals(expected, counterExample);
    }

    /**
     *  A helper function to make testing of string outputs less brittle
     *
     * @param input The output of a call to getCounterExample
     * @return A map from symbols to values
     */
    private Map<String, Integer> parseCounterexample(String input) {
        Map<String, Integer> symbolToVal = new HashMap<>();
        Scanner scanner = new Scanner(input);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split("=");
            if (tokens.length == 2) {
                String symbol = tokens[0].trim();
                int val = Integer.parseInt(tokens[1].trim());
                symbolToVal.put(symbol, val);
            }
        }
        return symbolToVal;
    }
}
