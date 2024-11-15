package replacement;

import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.expression.BinaryExpression;
import imp.expression.Expression;
import imp.expression.ExpressionType;
import imp.visitor.replacement.ExprCopyVisitor;
import imp.visitor.ExpressionVisitor;
import imp.visitor.serialize.ExpressionSerializeVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ExprCopyVisitorTest
{
    private void checkSerializationResult(Expression expression, String expectedResult)
    {
        ExpressionSerializeVisitor visitor = new ExpressionSerializeVisitor();
        expression.accept(visitor);
        String actual = visitor.result;
        assertEquals(expectedResult, actual);
    }

   
    @Test
    public void test_int_copy()
    {
        IntegerExpression expr = new IntegerExpression(1);
        ExprCopyVisitor visitor = new ExprCopyVisitor();
        expr.accept(visitor);

        // contents are same but memory addresses are different
        assertTrue(visitor.result instanceof IntegerExpression);
        checkSerializationResult(visitor.result, "1");
        assertNotSame(expr, visitor.result);
        // original unchanged
        assertTrue(expr.integer == 1);
    }

    @Test
    public void test_var_copy()
    {
        VariableExpression expr = new VariableExpression("x");
        ExprCopyVisitor visitor = new ExprCopyVisitor();
        expr.accept(visitor);

        // contents are same but memory addresses are different
        assertTrue(visitor.result instanceof VariableExpression);
        checkSerializationResult(visitor.result, "x");
        assertNotSame(expr, visitor.result);
        // original unchanged
        assertTrue(expr.symbol == "x");
    }

    @Test
    public void test_simple_binary_copy()
    {
        VariableExpression expr1 = new VariableExpression("x");
        IntegerExpression expr2 = new IntegerExpression(1);
        BinaryExpression expr = new BinaryExpression(ExpressionType.ADD, expr1, expr2);
        ExprCopyVisitor visitor = new ExprCopyVisitor();
        expr.accept(visitor);

        // check outer
        // contents are same but memory addresses are different
        assertTrue(visitor.result instanceof BinaryExpression);
        checkSerializationResult(visitor.result, "x + 1");
        assertNotSame(expr, visitor.result);
        // original unchanged
        assertTrue(expr.type == ExpressionType.ADD);
        assertSame(expr.left, expr1);
        assertSame(expr.right, expr2);
        assertEquals(expr1.symbol, "x");
        assertEquals(expr2.integer, 1);

        // check inner
        BinaryExpression res_expr = (BinaryExpression)visitor.result;
        assertNotSame(expr1, res_expr.left);
        assertNotSame(expr2, res_expr.right);
    }

    @Test
    public void test_complex_binary_copy()
    {
        VariableExpression expr1 = new VariableExpression("x");
        IntegerExpression expr2 = new IntegerExpression(1);
        BinaryExpression expr = new BinaryExpression(ExpressionType.ADD, expr1, expr2);
        BinaryExpression expr_outer = new BinaryExpression(ExpressionType.MUL, expr1, expr);
        ExprCopyVisitor visitor = new ExprCopyVisitor();
        expr_outer.accept(visitor);

        // check outer
        // contents are same but memory addresses are different
        assertTrue(visitor.result instanceof BinaryExpression);
        checkSerializationResult(visitor.result, "x * ( x + 1 )");
        assertNotSame(expr_outer, visitor.result);

        // original outer unchanged
        assertTrue(expr_outer.type == ExpressionType.MUL);
        assertSame(expr_outer.left, expr1);
        assertSame(expr_outer.right, expr);


        // check inner
        BinaryExpression res_outer = (BinaryExpression) visitor.result;
        assertNotSame(expr1, res_outer.left);
        assertNotSame(expr, res_outer.right);

        // check nested
        BinaryExpression res_expr = (BinaryExpression) res_outer.right;
        assertTrue(res_expr.type == ExpressionType.ADD);
        assertNotSame(expr1, res_expr.left);
        assertNotSame(expr2, res_expr.right);

        // original inner unchanged
        assertTrue(expr.type == ExpressionType.ADD);
        assertSame(expr.left, expr1);
        assertSame(expr.right, expr2);
        assertEquals(expr1.symbol, "x");
        assertEquals(expr2.integer, 1);
    }
}
