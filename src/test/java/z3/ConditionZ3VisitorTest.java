package z3;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import imp.condition.*;
import imp.condition.Boolean;
import imp.expression.Expression;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.visitor.z3.ConditionZ3Visitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ConditionZ3VisitorTest {
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

    /* Public API Tests */

    @Test
    public void testGetResult() {
        // Test Scenario: calling getResult visits the condition and returns the result
        Expression x = new VariableExpression("x");
        Expression zero = new IntegerExpression(0);
        Condition le = new BinaryCondition(ConditionType.LE, x, zero);
        BoolExpr result = visitor.getResult(le);
        assertEquals(ctx.mkLe(ctx.mkIntConst("x"), ctx.mkInt(0)), result);
    }

    @Test
    public void testCheckValidity() {
        // Test Scenario: ( x <= 0 ) OR ( x > 0 ) is valid
        Expression x = new VariableExpression("x");
        Expression zero = new IntegerExpression(0);
        Condition left = new BinaryCondition(ConditionType.LE, x, zero);
        Condition right = new UnaryConnective(ConnectiveType.NOT, left);
        BinaryConnective or = new BinaryConnective(ConnectiveType.OR, left, right);
        assertTrue(visitor.checkValidity(or));

        // Test Scenario: ( x <= 0 ) is not valid
        assertFalse(visitor.checkValidity(left));
    }

    /* Boolean Tests */

    @Test
    public void testBoolean() {
        Condition t = new Boolean(true);
        BoolExpr result = visitor.getResult(t);
        assertEquals(ctx.mkTrue(), result);

        Condition f = new Boolean(false);
        result = visitor.getResult(f);
        assertEquals(ctx.mkFalse(), result);
    }

    /* Binary Condition Tests */

    @Test
    public void testEquals() {
        Expression one = new IntegerExpression(1);
        ArithExpr z_one = ctx.mkInt(1);

        // Test Scenario: 1 = 1
        BoolExpr expected = ctx.mkEq(z_one, z_one);
        Condition equals = new BinaryCondition(ConditionType.EQUAL, one, one);
        BoolExpr result = visitor.getResult(equals);
        assertEquals(expected, result);
    }

    @Test
    public void testLE() {
        Expression one = new IntegerExpression(1);
        Expression two = new IntegerExpression(2);

        // Test Scenario: 1 <= 2
        BoolExpr expected = ctx.mkLe(ctx.mkInt(1), ctx.mkInt(2));
        Condition le = new BinaryCondition(ConditionType.LE, one, two);
        BoolExpr result = visitor.getResult(le);
        assertEquals(expected, result);
    }

    /* Binary Connective Tests */

    @Test
    public void testAnd() {
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression zero = new IntegerExpression(0);

        // Test Scenario: x <= 0 AND y <= 0
        BoolExpr f1 = ctx.mkLe(ctx.mkIntConst("x"), ctx.mkInt(0));
        BoolExpr f2 = ctx.mkLe(ctx.mkIntConst("y"), ctx.mkInt(0));
        BoolExpr expected = ctx.mkAnd(f1, f2);

        Condition left = new BinaryCondition(ConditionType.LE, x, zero);
        Condition right = new BinaryCondition(ConditionType.LE, y, zero);
        BinaryConnective and = new BinaryConnective(ConnectiveType.AND, left, right);
        BoolExpr result = visitor.getResult(and);
        assertEquals(expected, result);
    }

    @Test
    public void testOr() {
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression zero = new IntegerExpression(0);

        // Test Scenario: x <= 0 OR y <= 0
        BoolExpr f1 = ctx.mkLe(ctx.mkIntConst("x"), ctx.mkInt(0));
        BoolExpr f2 = ctx.mkLe(ctx.mkIntConst("y"), ctx.mkInt(0));
        BoolExpr expected = ctx.mkOr(f1, f2);

        Condition left = new BinaryCondition(ConditionType.LE, x, zero);
        Condition right = new BinaryCondition(ConditionType.LE, y, zero);
        BinaryConnective or = new BinaryConnective(ConnectiveType.OR, left, right);
        BoolExpr result = visitor.getResult(or);
        assertEquals(expected, result);
    }

    @Test
    public void testImplies() {
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression zero = new IntegerExpression(0);

        // Test Scenario: ( x <= 0 ) ==> ( y <= 0 )
        BoolExpr f1 = ctx.mkLe(ctx.mkIntConst("x"), ctx.mkInt(0));
        BoolExpr f2 = ctx.mkLe(ctx.mkIntConst("y"), ctx.mkInt(0));
        BoolExpr expected = ctx.mkImplies(f1, f2);

        Condition left = new BinaryCondition(ConditionType.LE, x, zero);
        Condition right = new BinaryCondition(ConditionType.LE, y, zero);
        BinaryConnective implies = new BinaryConnective(ConnectiveType.IMPLIES, left, right);
        BoolExpr result = visitor.getResult(implies);
        assertEquals(expected, result);
    }

    @Test
    public void testNestedBinaryConnectives() {
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression z = new VariableExpression("z");
        Expression zero = new IntegerExpression(0);
        Expression ten = new IntegerExpression(10);

        ArithExpr z_x = ctx.mkIntConst("x");
        ArithExpr z_y = ctx.mkIntConst("y");
        ArithExpr z_z = ctx.mkIntConst("z");
        ArithExpr z_zero = ctx.mkInt(0);
        ArithExpr z_ten = ctx.mkInt(10);

        // Test Scenario: ( ( x <= 0 AND y <= 0 ) OR ( z <= 10 ) ) ==> ( x = y )
        BoolExpr z_left = ctx.mkOr(
                            ctx.mkAnd(
                                    ctx.mkLe(z_x, z_zero),
                                    ctx.mkLe(z_y, z_zero)
                            ),
                            ctx.mkLe(z_z, z_ten)
                        );

        BoolExpr z_right = ctx.mkEq(z_x, z_y);
        BoolExpr expected = ctx.mkImplies(z_left, z_right);

        Condition x_le_0 = new BinaryCondition(ConditionType.LE, x, zero);
        Condition y_le_0 = new BinaryCondition(ConditionType.LE, y, zero);
        Condition z_le_10 = new BinaryCondition(ConditionType.LE, z, ten);
        BinaryConnective innerAnd = new BinaryConnective(ConnectiveType.AND, x_le_0, y_le_0);
        BinaryConnective left = new BinaryConnective(ConnectiveType.OR, innerAnd, z_le_10);
        Condition right = new BinaryCondition(ConditionType.EQUAL, x, y);
        BinaryConnective outerImplies = new BinaryConnective(ConnectiveType.IMPLIES, left, right);
        BoolExpr result = visitor.getResult(outerImplies);
        assertEquals(expected, result);
    }

    /* Unary Connective Tests */

    @Test
    public void testNot() {
        Condition t = new Boolean(true);
        VariableExpression x = new VariableExpression("x");
        IntegerExpression zero = new IntegerExpression(0);

        // Test Scenario: NOT( true )
        UnaryConnective notTrue =  new UnaryConnective(ConnectiveType.NOT, t);
        BoolExpr expected = ctx.mkNot(ctx.mkTrue());
        BoolExpr result = visitor.getResult(notTrue);
        assertEquals(expected, result);

        // Test Scenario: NOT( x <= 0 )
        BinaryCondition f1 = new BinaryCondition(ConditionType.LE, x, zero);
        UnaryConnective notF1 = new UnaryConnective(ConnectiveType.NOT, f1);
        expected = ctx.mkNot(ctx.mkLe(ctx.mkIntConst("x"), ctx.mkInt(0)));
        result = visitor.getResult(notF1);
        assertEquals(expected, result);

        // Test Scenario: NOT( NOT( true ) )
        UnaryConnective notNotTrue = new UnaryConnective(ConnectiveType.NOT, notTrue);
        expected = ctx.mkNot(ctx.mkNot(ctx.mkTrue()));
        result = visitor.getResult(notNotTrue);
        assertEquals(expected, result);
    }
}
