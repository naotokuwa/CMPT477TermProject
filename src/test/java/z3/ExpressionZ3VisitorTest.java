package z3;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;
import imp.expression.*;
import imp.visitor.z3.ExpressionZ3Visitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionZ3VisitorTest {
    private Context ctx;
    private ExpressionZ3Visitor visitor;

    @BeforeEach public void setUp() {
        ctx = new Context();
        visitor = new ExpressionZ3Visitor(ctx);
    }

    @AfterEach public void tearDown() {
        ctx.close();
    }

    /* Public API tests */

    @Test
    public void testGetResult() {
        // Test Scenario: calling getResult visits the expression and returns the result
        Expression x = new VariableExpression("x");
        ArithExpr result = visitor.getResult(x);
        assertEquals(ctx.mkIntConst("x"), result);
    }

    /* Simple type conversion tests */

    @Test
    public void testInteger() {
        Expression one = new IntegerExpression(1);
        ArithExpr result = visitor.getResult(one);
        assertEquals(ctx.mkInt(1), result);
    }

    @Test
    public void testVariable() {
        Expression x = new VariableExpression("x");
        ArithExpr result = visitor.getResult(x);
        assertEquals(ctx.mkIntConst("x"), result);
    }

    /* Binary expression conversion tests */

    @Test
    public void testBinary() {
        Expression one = new IntegerExpression(1);
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        ArithExpr z_one = ctx.mkInt(1);
        ArithExpr z_x = ctx.mkIntConst("x");
        ArithExpr z_y = ctx.mkIntConst("y");

        /* Test ADD expressions */

        // Add integers: 1 + 1
        ArithExpr expected = ctx.mkAdd(z_one, z_one);
        testBinaryExpression(expected, ExpressionType.ADD, one, one);

        // Add same variables: x + x
        expected = ctx.mkAdd(z_x, z_x);
        testBinaryExpression(expected, ExpressionType.ADD, x, x);

        // Add different variables: x + y
        expected = ctx.mkAdd(z_x, z_y);
        testBinaryExpression(expected, ExpressionType.ADD, x, y);

        /* Test MUL expressions */

        // Multiply integers: 1 * 1
        expected = ctx.mkMul(z_one, z_one);
        testBinaryExpression(expected, ExpressionType.MUL, one, one);

        // Multiply same variables: x * x
        expected = ctx.mkMul(z_x, z_x);
        testBinaryExpression(expected, ExpressionType.MUL, x, x);

        // Multiply different variables: x * y
        expected = ctx.mkMul(z_x, z_y);
        testBinaryExpression(expected, ExpressionType.MUL, x, y);
    }

    @Test
    public void testNestedBinary() {
        Expression one = new IntegerExpression(1);
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        ArithExpr z_one = ctx.mkInt(1);
        ArithExpr z_x = ctx.mkIntConst("x");
        ArithExpr z_y = ctx.mkIntConst("y");

        // ( x + 1 ) * ( y + 1 )
        ArithExpr expected = ctx.mkMul(ctx.mkAdd(z_x, z_one), ctx.mkAdd(z_y, z_one));
        Expression left = new BinaryExpression(ExpressionType.ADD, x, one);
        Expression right = new BinaryExpression(ExpressionType.ADD, y, one);
        testBinaryExpression(expected, ExpressionType.MUL, left, right);
    }

    /* Test Helpers */

    private void testBinaryExpression(ArithExpr expected, ExpressionType type, Expression left, Expression right) {
        Expression expr = new BinaryExpression(type, left, right);
        ArithExpr result = visitor.getResult(expr);
        assertEquals(expected, result);
    }
}
