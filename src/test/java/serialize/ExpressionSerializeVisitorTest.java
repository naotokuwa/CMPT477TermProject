package serialize;

import imp.expression.*;
import imp.visitor.serialize.ExpressionSerializeVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionSerializeVisitorTest {
    @Test
    public void testAdditionPrint() {
        ExpressionSerializeVisitor visitor = new ExpressionSerializeVisitor();
        Expression one = new IntegerExpression(1);
        Expression addition = new BinaryExpression(ExpressionType.ADD, one, one);

        // Perform serialization
        addition.accept(visitor);

        String result = visitor.result;
        String expected = "1 + 1";

        assertEquals(expected, result);
    }

    @Test
    public void testMultiplyPrint() {
        ExpressionSerializeVisitor visitor = new ExpressionSerializeVisitor();
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression multiply = new BinaryExpression(ExpressionType.MUL, x, y);

        // Perform serialization
        multiply.accept(visitor);

        String result = visitor.result;
        String expected = "x * y";

        assertEquals(expected, result);
    }
}
