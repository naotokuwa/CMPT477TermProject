package imp.visitor.serialize;

import imp.condition.*;
import imp.condition.Boolean;
import imp.expression.Expression;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionSerializeVisitorTest {
    @Test
    public void testTrue() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();
        Condition c = new Boolean(true);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "true";

        assertEquals(expected, result);
    }

    @Test
    public void testFalse() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();
        Condition c = new Boolean(false);

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
        Condition c = new BinaryCondition(ConditionType.EQUAL, x, one);

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
        Condition c = new BinaryCondition(ConditionType.LE, x, five);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "x <= 5";

        assertEquals(expected, result);
    }

    @Test
    public void testAnd() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();

        // 1 <= x
        Expression x = new VariableExpression("x");
        Expression one = new IntegerExpression(1);
        Condition c1 = new BinaryCondition(ConditionType.LE, one, x);

        // x <= 5
        Expression five = new IntegerExpression(5);
        Condition c2 = new BinaryCondition(ConditionType.LE, x, five);

        Condition c = new BinaryConnective(ConnectiveType.AND, c1, c2);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "( 1 <= x ) AND ( x <= 5 )";

        assertEquals(expected, result);
    }

    @Test
    public void testOr() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();

        // x == 1
        Expression x = new VariableExpression("x");
        Expression one = new IntegerExpression(1);
        Condition c1 = new BinaryCondition(ConditionType.EQUAL, x, one);

        // x == -1
        Expression negativeOne = new IntegerExpression(-1);
        Condition c2 = new BinaryCondition(ConditionType.EQUAL, x, negativeOne);

        Condition c = new BinaryConnective(ConnectiveType.OR, c1, c2);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "( x == 1 ) OR ( x == -1 )";

        assertEquals(expected, result);
    }

    @Test
    public void testImplies() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();

        // (x == -1)
        Expression x = new VariableExpression("x");
        Expression negativeOne = new IntegerExpression(-1);
        Condition c1 = new BinaryCondition(ConditionType.EQUAL, x, negativeOne);

        // (x <= 0)
        Expression zero = new IntegerExpression(0);
        Condition c2 = new BinaryCondition(ConditionType.LE, x, zero);

        Condition c = new BinaryConnective(ConnectiveType.IMPLIES, c1, c2);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "( x == -1 ) ==> ( x <= 0 )";

        assertEquals(expected, result);
    }

    @Test
    public void testNot() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();

        // NOT (x == 1)
        Expression x = new VariableExpression("x");
        Expression one = new IntegerExpression(1);
        Condition c1 = new BinaryCondition(ConditionType.EQUAL, x, one);

        Condition c = new UnaryConnective(ConnectiveType.NOT, c1);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "NOT( x == 1 )";

        assertEquals(expected, result);
    }

    @Test
    public void testNestedConnective() {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();

        Expression x = new VariableExpression("x");

        // (x == -1)
        Expression negativeOne = new IntegerExpression(-1);
        Condition c1 = new BinaryCondition(ConditionType.EQUAL, x, negativeOne);

        // x <= 0
        Expression zero = new IntegerExpression(0);
        Condition c2 = new BinaryCondition(ConditionType.LE, x, zero);

        Condition firstImply = new BinaryConnective(ConnectiveType.IMPLIES, c1, c2);

        // Second Imply
        Condition notC1 = new UnaryConnective(ConnectiveType.NOT, c1);
        Condition notC2 = new UnaryConnective(ConnectiveType.NOT, c2);
        Condition secondImply = new BinaryConnective(ConnectiveType.IMPLIES, notC1, notC2);

        Condition c = new BinaryConnective(ConnectiveType.AND, firstImply, secondImply);

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "( ( x == -1 ) ==> ( x <= 0 ) ) AND ( ( NOT( x == -1 ) ) ==> ( NOT( x <= 0 ) ) )";

        assertEquals(expected, result);
    }
}
