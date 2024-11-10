package imp.visitor.serialize;

import imp.condition.BinaryCondition;
import imp.condition.Boolean;
import imp.condition.ConditionType;
import imp.condition.Conditional;
import imp.expression.Expression;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionSerializeVisitorTest {
    @Test
    public void testTrue() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();
        Conditional c = new Boolean(true);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "true";

        assertEquals(expected, result);
    }

    @Test
    public void testFalse() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();
        Conditional c = new Boolean(false);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "false";

        assertEquals(expected, result);
    }

    @Test
    public void testEqual() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();
        Expression x = new VariableExpression("x");
        Expression one = new IntegerExpression(1);
        Conditional c = new BinaryCondition(ConditionType.EQUAL, x, one);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "x == 1";

        assertEquals(expected, result);
    }

    @Test
    public void testLe() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();
        Expression x = new VariableExpression("x");
        Expression five = new IntegerExpression(5);
        Conditional c = new BinaryCondition(ConditionType.LE, x, five);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "x <= 5";

        assertEquals(expected, result);
    }
}
