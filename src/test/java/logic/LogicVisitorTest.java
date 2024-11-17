package logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import imp.condition.BinaryCondition;
import imp.condition.BinaryConnective;
import imp.condition.Condition;
import imp.condition.ConditionType;
import imp.condition.ConnectiveType;
import imp.expression.BinaryExpression;
import imp.expression.ExpressionType;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.statement.Assignment;
import imp.statement.Composition;
import imp.statement.If;
import imp.visitor.logic.LogicVisitor;
import imp.visitor.serialize.ConditionSerializeVisitor;


public class LogicVisitorTest
{
    private void checkSerializationResult(Condition condition, String expectedResult)
    {
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();
        condition.accept(visitor);
        String actual = visitor.result;
        assertEquals(expectedResult, actual);
    }


    private VariableExpression x;
    private IntegerExpression one;
    private Assignment x_eq_1;
    private VariableExpression y;
    private VariableExpression y2;
    private VariableExpression x2;
    private VariableExpression x3;
    private Assignment y_eq_x;
    private Assignment x_eq_y;
    private Composition comp;
    private BinaryCondition x_le_y;
    private BinaryCondition is_x_eq_y;
    private If if_stmnt;
    private BinaryCondition q;


    @BeforeEach
    public void setUp()
    {
        // x := 1
        x = new VariableExpression("x");
        one = new IntegerExpression(1);
        x_eq_1 = new Assignment(x, one);
        // y := x
        y2 = new VariableExpression("y");
        x2 = new VariableExpression("x");
        y_eq_x = new Assignment(y2, x2);
        // x := y
        x3 = new VariableExpression("x");
        y = new VariableExpression("y");
        x_eq_y = new Assignment(x3, y);

        // y := x; x := 1;
        comp = new Composition(y_eq_x, x_eq_1);

        // x <= y
        x_le_y = new BinaryCondition(ConditionType.LE, x, y);
        // x == y
        is_x_eq_y = new BinaryCondition(ConditionType.EQUAL, x, y);

        // if x == y then x := 1 else x := y
        if_stmnt = new If(is_x_eq_y, x_eq_1, x_eq_y);


        // x <= y
        q = new BinaryCondition(ConditionType.LE, x, y);
    }


    @Test
    public void test_assignment()
    {
        // x := 1
        // {x <= y}
        LogicVisitor solver = new LogicVisitor(q);
        x_eq_1.accept(solver);

        checkSerializationResult(solver.result, "1 <= y");


        // more than one occurence
        // x := 1
        // {x <= x}
        BinaryCondition q2 = new BinaryCondition(ConditionType.LE, x, x);
        solver = new LogicVisitor(q2);
        x_eq_1.accept(solver);

        checkSerializationResult(solver.result, "1 <= 1");
    }

    @Test
    public void test_composition()
    {
        // y := x; x := 1;
        // {x <= y}
        LogicVisitor solver = new LogicVisitor(q);
        comp.accept(solver);

        checkSerializationResult(solver.result, "1 <= x");


        // test reverse order
        // x := 1; y := x;
        // {x <= y}
        solver = new LogicVisitor(q);
        Composition comp2 = new Composition(x_eq_1, y_eq_x);
        comp2.accept(solver);

        checkSerializationResult(solver.result, "1 <= 1");
    }

    @Test
    public void test_if()
    {
        // if x == y then x := 1 else x := y
        // {x <= y}
        LogicVisitor solver = new LogicVisitor(q);
        if_stmnt.accept(solver);

        String res = "( ( x == y ) ==> ( 1 <= y ) ) AND ( ( NOT( x == y ) ) ==> ( y <= y ) )";
        checkSerializationResult(solver.result, res);
    }

    @Test
    public void test_complex_q()
    {
        // x := 1
        // {(x <= y) && (x + 2 == y)}
        BinaryCondition left = x_le_y;

        IntegerExpression two = new IntegerExpression(2);
        BinaryExpression x_plus_2 = new BinaryExpression(ExpressionType.ADD, x, two);
        BinaryCondition right = new BinaryCondition(ConditionType.EQUAL, x_plus_2, y);

        BinaryConnective complex_q = new BinaryConnective(ConnectiveType.AND, left, right);

        LogicVisitor solver = new LogicVisitor(complex_q);
        x_eq_1.accept(solver);

        checkSerializationResult(solver.result, "( 1 <= y ) AND ( 1 + 2 == y )");

        // check q wasn't changed
        assertEquals(complex_q.type, ConnectiveType.AND);
        assertSame(complex_q.left, left);
        assertSame(complex_q.right, right);

        BinaryCondition res_left = (BinaryCondition)complex_q.left;
        assertSame(res_left.type, ConditionType.LE);
        assertSame(res_left.left, x);
        assertSame(res_left.right, y);
        BinaryCondition res_right = (BinaryCondition)complex_q.right;
        assertSame(res_right.type, ConditionType.EQUAL);
        assertSame(res_right.left, x_plus_2);
        assertSame(res_right.right, y);

        BinaryExpression res_right_left = (BinaryExpression)res_right.left;
        assertSame(res_right_left.type, ExpressionType.ADD);
        assertSame(res_right_left.left, x);
        assertSame(res_right_left.right, two);
    }

    @Test
    public void test_if_comp()
    {
        // if x == y then y := x; x := 1; else x := y
        // {x <= y}
        If tmp_if = if_stmnt;
        if_stmnt = new If(is_x_eq_y, comp, x_eq_y);
        LogicVisitor solver = new LogicVisitor(q);
        if_stmnt.accept(solver);

        String res = "( ( x == y ) ==> ( 1 <= x ) ) AND ( ( NOT( x == y ) ) ==> ( y <= y ) )";
        checkSerializationResult(solver.result, res);

        // check inputs weren't changed
        assertEquals(if_stmnt.thenStatement, comp);
        assertEquals(if_stmnt.elseStatement, x_eq_y);
        assertEquals(is_x_eq_y.type, ConditionType.EQUAL);

        // check q wasn't changed
        assertEquals(q.type, ConditionType.LE);
        assertSame(q.left, x);
        assertSame(q.right, y);

        // reset if_stmnt so later asserts dont fail
        if_stmnt = tmp_if;
    }

    @Test
    public void test_comp_if()
    {
        // (if x == y then x := 1; else x := y); x := 1
        // {x <= y}
        Composition tmp_comp = comp;
        comp = new Composition(x_eq_1, if_stmnt);
        LogicVisitor solver = new LogicVisitor(q);
        comp.accept(solver);

        String res = "( ( 1 == y ) ==> ( 1 <= y ) ) AND ( ( NOT( 1 == y ) ) ==> ( y <= y ) )";
        checkSerializationResult(solver.result, res);

        // check inputs weren't changed
        assertSame(comp.before, x_eq_1);
        assertSame(comp.after, if_stmnt);

        // reset comp so later asserts dont fail
        comp = tmp_comp;
    }

    @Test
    public void test_if_if()
    {
        // if x <= y then (if x == y then x := 1 else x := y) else y := x
        // {x <= y}
        If outer_if = new If(q, if_stmnt, y_eq_x);
        LogicVisitor solver = new LogicVisitor(q);
        outer_if.accept(solver);

        String if_res = "( ( x == y ) ==> ( 1 <= y ) ) AND ( ( NOT( x == y ) ) ==> ( y <= y ) )";
        String res = "( ( x <= y ) ==> ( " + if_res + " ) ) AND ( ( NOT( x <= y ) ) ==> ( x <= x ) )";
        checkSerializationResult(solver.result, res);

        // check inputs weren't changed
        assertSame(outer_if.c, q);
        assertSame(outer_if.thenStatement, if_stmnt);
        assertSame(outer_if.elseStatement, y_eq_x);
    }
    
    @Test
    public void test_comp_comp()
    {
        // (y := x; x := 1;) x := z;
        // {x <= y}
        VariableExpression z = new VariableExpression("z");
        Assignment x_eq_z = new Assignment(x, z);
        Composition comp_comp = new Composition(comp, x_eq_z);
        LogicVisitor solver = new LogicVisitor(q);
        comp_comp.accept(solver);

        checkSerializationResult(solver.result, "z <= x");

        // check inputs unchanged
        assertSame(comp_comp.after, x_eq_z);
        assertSame(comp_comp.before, comp);
        assertSame(x_eq_z.e, z);
        assertSame(x_eq_z.v, x);
        assertEquals(z.symbol, "z");


        // test reverse order
        // x := z; (y := x; x := 1);
        // {x <= y}
        solver = new LogicVisitor(q);
        comp_comp = new Composition(x_eq_z, comp);
        comp_comp.accept(solver);

        checkSerializationResult(solver.result, "1 <= z");

        // check inputs unchanged
        assertSame(comp_comp.before, x_eq_z);
        assertSame(comp_comp.after, comp);
        assertSame(x_eq_z.e, z);
        assertSame(x_eq_z.v, x);
        assertEquals(z.symbol, "z");
    }


    @AfterEach
    public void checkAllSame()
    {
        // assignments
        assertSame(x_eq_y.v, x3);
        assertSame(x_eq_y.e, y);

        assertSame(y_eq_x.v, y2);
        assertSame(y_eq_x.e, x2);

        assertSame(x_eq_1.v, x);
        assertSame(x_eq_1.e, one);


        // variables
        assertEquals(one.integer, 1);

        assertEquals(x.symbol, "x");
        assertEquals(x2.symbol, "x");
        assertEquals(x3.symbol, "x");

        assertEquals(y2.symbol, "y");
        assertEquals(y.symbol, "y");


        // if statements
        assertEquals(if_stmnt.c, is_x_eq_y);
        assertEquals(if_stmnt.thenStatement, x_eq_1);
        assertEquals(if_stmnt.elseStatement, x_eq_y);


        // conditions
        assertEquals(is_x_eq_y.type, ConditionType.EQUAL);
        assertSame(is_x_eq_y.left, x);
        assertSame(is_x_eq_y.right, y);


        // composition
        assertSame(comp.before, y_eq_x);
        assertSame(comp.after, x_eq_1);


        // check q wasn't changed
        assertEquals(q.type, ConditionType.LE);
        assertSame(q.left, x);
        assertSame(q.right, y);
    }
}
