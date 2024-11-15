package replacement;

import org.junit.jupiter.api.Test;

import imp.condition.Condition;
import imp.condition.ConditionType;
import imp.condition.ConnectiveType;
import imp.condition.Boolean;
import imp.condition.BinaryCondition;
import imp.condition.BinaryConnective;
import imp.condition.UnaryConnective;
import imp.expression.IntegerExpression;
import imp.visitor.replacement.CondCopyVisitor;
import imp.visitor.serialize.ConditionSerializeVisitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CondCopyVisitorTest
{
    private void checkSerializationResult(Condition condition, String expectedResult){
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();
        condition.accept(visitor);
        String actual = visitor.result;
        assertEquals(expectedResult, actual);
    }


    @Test
    public void test_bool_copy()
    {
        Boolean expr = new Boolean(true);
        CondCopyVisitor visitor = new CondCopyVisitor();
        expr.accept(visitor);

        // contents are same but memory addresses are different
        assertTrue(visitor.result instanceof Boolean);
        checkSerializationResult(visitor.result, "true");
        assertNotSame(expr, visitor.result);
        // original unchanged
        assertTrue(expr.value);
    }

    @Test
    public void test_bin_cond_copy()
    {
        IntegerExpression one = new IntegerExpression(1);
        IntegerExpression two = new IntegerExpression(2);
        BinaryCondition cond = new BinaryCondition(ConditionType.EQUAL, one, two);
        CondCopyVisitor copier = new CondCopyVisitor();
        cond.accept(copier);

        // contents are same but memory addresses are different
        assertTrue(copier.result instanceof BinaryCondition);
        checkSerializationResult(copier.result, "1 == 2");
        assertNotSame(cond, copier.result);

        // check inner
        BinaryCondition res_cond = (BinaryCondition)copier.result;
        assertNotSame(res_cond.left, one);
        assertNotSame(res_cond.right, two);

        // original unchanged
        assertSame(cond.left, one);
        assertSame(cond.right, two);
        assertTrue(one.integer == 1);
        assertTrue(two.integer == 2);
    }

    @Test
    public void test_bin_conn_copy()
    {
        Boolean t = new Boolean(true);
        Boolean f = new Boolean(false);
        BinaryConnective cond = new BinaryConnective(ConnectiveType.AND, t, f);
        CondCopyVisitor copier = new CondCopyVisitor();
        cond.accept(copier);

        // contents are same but memory addresses are different
        assertTrue(copier.result instanceof BinaryConnective);
        checkSerializationResult(copier.result, "( true ) AND ( false )");
        assertNotSame(cond, copier.result);

        // check inner
        BinaryConnective res_cond = (BinaryConnective)copier.result;
        assertNotSame(res_cond.left, t);
        assertNotSame(res_cond.right, f);

        // original unchanged
        assertSame(cond.left, t);
        assertSame(cond.right, f);
        assertTrue(t.value);
        assertFalse(f.value);
    }

    @Test
    public void test_unary_conn_copy()
    {
        Boolean t = new Boolean(true);
        UnaryConnective cond = new UnaryConnective(ConnectiveType.NOT, t);
        CondCopyVisitor copier = new CondCopyVisitor();
        cond.accept(copier);

        // contents are same but memory addresses are different
        assertTrue(copier.result instanceof UnaryConnective);
        checkSerializationResult(copier.result, "NOT( true )");
        assertNotSame(cond, copier.result);

        // check inner
        UnaryConnective res_cond = (UnaryConnective)copier.result;
        assertNotSame(res_cond.condition, t);

        // original unchanged
        assertSame(cond.condition, t);
        assertTrue(t.value);
    }

    @Test
    public void test_nested_bin_conn_copy()
    {
        Boolean t = new Boolean(true);
        Boolean f = new Boolean(false);
        BinaryConnective cond = new BinaryConnective(ConnectiveType.AND, t, f);
        BinaryConnective outer_cond = new BinaryConnective(ConnectiveType.OR, cond, f);
        CondCopyVisitor copier = new CondCopyVisitor();
        outer_cond.accept(copier);

        // contents are same but memory addresses are different
        assertTrue(copier.result instanceof BinaryConnective);
        checkSerializationResult(copier.result, "( ( true ) AND ( false ) ) OR ( false )");
        assertNotSame(outer_cond, copier.result);

        // check inner
        BinaryConnective res_cond = (BinaryConnective)copier.result;
        assertNotSame(res_cond.left, cond);
        assertNotSame(res_cond.right, f);
        // check nested
        BinaryConnective res_inner_cond = (BinaryConnective)res_cond.left;
        assertNotSame(res_inner_cond.left, t);
        assertNotSame(res_inner_cond.right, f);

        // original unchanged
        assertSame(outer_cond.left, cond);
        assertSame(outer_cond.right, f);
        assertSame(cond.left, t);
        assertSame(cond.right, f);
        assertTrue(t.value);
        assertFalse(f.value);
    }
}
