package replacement;

import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.expression.BinaryExpression;
import imp.expression.Expression;
import imp.expression.ExpressionType;
import imp.visitor.replacement.CopyVisitor;
import imp.visitor.ExpressionVisitor;
import imp.visitor.serialize.ExpressionSerializeVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CopyVisitorTest
{
    private void checkSerializationResult(Expression expression, String expectedResult)
    {
        ExpressionSerializeVisitor visitor = new ExpressionSerializeVisitor();
        expression.accept(visitor);
        String actual = visitor.result;
        assertEquals(expectedResult, actual);
    }

    // getters for validation
    private class getBinaryInnerVisitor extends ExpressionVisitor
    {
        Expression left;
        Expression right;
        @Override
        public void visit(IntegerExpression e) {  }

        @Override
        public void visit(VariableExpression e) {  }

        @Override
        public void visit(BinaryExpression e)
        {
            left = e.left;
            right = e.right;
        }
    }
    private class getBinaryExprVisitor extends ExpressionVisitor
    {
        private int depth = 0;
        BinaryExpression res;
        @Override
        public void visit(IntegerExpression e) {  }

        @Override
        public void visit(VariableExpression e) {  }

        @Override
        public void visit(BinaryExpression e)
        {
            if(depth > 0)
            { res = e; }
            depth++;
            e.right.accept(this);
        }
    }

    @Test
    public void test_int_copy()
    {
        IntegerExpression expr = new IntegerExpression(1);
        CopyVisitor visitor = new CopyVisitor();
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
        CopyVisitor visitor = new CopyVisitor();
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
        CopyVisitor visitor = new CopyVisitor();
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
        getBinaryInnerVisitor inner_visitor = new getBinaryInnerVisitor();
        visitor.result.accept(inner_visitor);
        assertNotSame(expr1, inner_visitor.left);
        assertNotSame(expr2, inner_visitor.right);
    }

    @Test
    public void test_complex_binary_copy()
    {
        VariableExpression expr1 = new VariableExpression("x");
        IntegerExpression expr2 = new IntegerExpression(1);
        BinaryExpression expr = new BinaryExpression(ExpressionType.ADD, expr1, expr2);
        BinaryExpression expr_outer = new BinaryExpression(ExpressionType.MUL, expr1, expr);
        CopyVisitor visitor = new CopyVisitor();
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
        getBinaryInnerVisitor inner_visitor = new getBinaryInnerVisitor();
        visitor.result.accept(inner_visitor);
        assertNotSame(expr1, inner_visitor.left);
        assertNotSame(expr, inner_visitor.right);

        // get nested binary expression to check objects in nested binaryexpr are new
        getBinaryExprVisitor binary_e_visitor = new getBinaryExprVisitor();
        visitor.result.accept(binary_e_visitor);
        inner_visitor = new getBinaryInnerVisitor();
        binary_e_visitor.res.accept(inner_visitor);

        assertTrue(binary_e_visitor.res.type == ExpressionType.ADD);
        assertNotSame(expr1, inner_visitor.left);
        assertNotSame(expr2, inner_visitor.right);

        // original inner unchanged
        assertTrue(expr.type == ExpressionType.ADD);
        assertSame(expr.left, expr1);
        assertSame(expr.right, expr2);
        assertEquals(expr1.symbol, "x");
        assertEquals(expr2.integer, 1);
    }
}
