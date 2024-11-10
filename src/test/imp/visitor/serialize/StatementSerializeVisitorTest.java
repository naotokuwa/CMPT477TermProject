package imp.visitor.serialize;

import imp.condition.BinaryCondition;
import imp.condition.ConditionType;
import imp.condition.Condition;
import imp.expression.*;
import imp.statement.Assignment;
import imp.statement.Composition;
import imp.statement.If;
import imp.statement.Statement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatementSerializeVisitorTest {
    @Test
    public void testAssignment() {
        StatementSerializeVisitor visitor = new StatementSerializeVisitor();
        Statement s = new Assignment(new VariableExpression("x"), new IntegerExpression((5)));

        // Perform serialization
        s.accept(visitor);
        String result = visitor.result;
        String expected = "x := 5";

        assertEquals(expected, result);
    }

    @Test
    public void testComposition() {
        StatementSerializeVisitor visitor = new StatementSerializeVisitor();
        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");
        VariableExpression z = new VariableExpression("z");

        Statement s1 = new Assignment(x, new IntegerExpression((5)));
        Statement s2 = new Assignment(y, new IntegerExpression((2)));
        Expression additionExpression = new BinaryExpression(ExpressionType.ADD, x, y);
        Statement s3 = new Assignment(z, additionExpression);

        Statement s = new Composition(s1, new Composition(s2, s3));

        // Perform serialization
        s.accept(visitor);
        String result = visitor.result;
        String expected = "x := 5\n";
        expected += "y := 2\n";
        expected += "z := x + y";

        assertEquals(expected, result);
    }

    @Test
    public void testIf() {
        StatementSerializeVisitor visitor = new StatementSerializeVisitor();

        // Variable initialization
        VariableExpression minVar = new VariableExpression("minVar");
        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");

        // Variable assignments
        Statement s1 = new Assignment(x, new IntegerExpression(10));
        Statement s2 = new Assignment(y, new IntegerExpression((100)));

        // If block
        Condition c = new BinaryCondition(ConditionType.LE, x, y);
        Statement s3 = new Assignment(minVar, x);
        Statement s4 = new Assignment(minVar, y);
        Statement s5 = new If(c, s3, s4);

        Statement s = new Composition(s1, new Composition(s2, s5));

        // Perform serialization
        s.accept(visitor);
        String result = visitor.result;
        String expected = "x := 10\n";
        expected += "y := 100\n";
        expected += "if x <= y\n";
        expected += "then\n";
        expected += "  minVar := x\n";
        expected += "else\n";
        expected += "  minVar := y";

        assertEquals(expected, result);
    }

    @Test
    public void testNestedIf() {
        StatementSerializeVisitor visitor = new StatementSerializeVisitor();

        // Const
        IntegerExpression zero = new IntegerExpression(0);
        IntegerExpression one = new IntegerExpression(1);
        IntegerExpression negativeOne = new IntegerExpression(-1);

        // Variable initialization
        VariableExpression result = new VariableExpression("result");
        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");

        // First nested if
        Condition c1 = new BinaryCondition(ConditionType.EQUAL, y, zero);
        Statement s1 = new Assignment(result, one);
        Statement s2 = new Assignment(result, negativeOne);
        Statement s3 = new If(c1, s1, s2);

        // Second nested if
        Condition c2 = new BinaryCondition(ConditionType.EQUAL, y, zero);
        Statement s4 = new Assignment(result, negativeOne);
        Statement s5 = new Assignment(result, one);
        Statement s6 = new If(c2, s4, s5);

        // Outer if statement
        Condition c3 = new BinaryCondition(ConditionType.EQUAL, x, zero);
        Statement s = new If(c3, s3, s6);

        // Perform serialization
        s.accept(visitor);
        String actual = visitor.result;
        String expected = "";
        expected += "if x == 0\n";
        expected += "then\n";
        expected += "  if y == 0\n";
        expected += "  then\n";
        expected += "    result := 1\n";
        expected += "  else\n";
        expected += "    result := -1\n";
        expected += "else\n";
        expected += "  if y == 0\n";
        expected += "  then\n";
        expected += "    result := -1\n";
        expected += "  else\n";
        expected += "    result := 1";

        assertEquals(expected, actual);
    }
}
