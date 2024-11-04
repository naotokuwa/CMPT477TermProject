package imp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SerializeVisitorTest {
    @Test
    public void testAdditionPrint() {
        SerializeVisitor visitor = new SerializeVisitor();
        Expression addition = new AdditionExpression(new IntegerExpression(1), new IntegerExpression(1));

        // Perform serialization
        addition.accept(visitor);

        String result = visitor.result;
        String expected = "1 + 1";

        assertEquals(expected, result);
    }

    @Test
    public void testMultiplyPrint() {
        SerializeVisitor visitor = new SerializeVisitor();
        Expression addition = new MultiplyExpression(new VariableExpression("x"), new VariableExpression("y"));

        // Perform serialization
        addition.accept(visitor);

        String result = visitor.result;
        String expected = "x * y";

        assertEquals(expected, result);
    }

    @Test
    public void testTrue() {
        SerializeVisitor visitor = new SerializeVisitor();
        Conditional c = new True();

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "true";

        assertEquals(expected, result);
    }

    @Test
    public void testFalse() {
        SerializeVisitor visitor = new SerializeVisitor();
        Conditional c = new False();

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "false";

        assertEquals(expected, result);
    }

    @Test
    public void testEqual() {
        SerializeVisitor visitor = new SerializeVisitor();
        Conditional c = new Equal(new VariableExpression("x"), new IntegerExpression((1)));

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "x == 1";

        assertEquals(expected, result);
    }

    @Test
    public void testLe() {
        SerializeVisitor visitor = new SerializeVisitor();
        Conditional c = new Le(new VariableExpression("x"), new IntegerExpression((5)));

        // Perform serialization
        c.accept(visitor);
        String result = visitor.result;
        String expected = "x <= 5";

        assertEquals(expected, result);
    }

    @Test
    public void testAssignment() {
        SerializeVisitor visitor = new SerializeVisitor();
        Statement s = new Assignment(new VariableExpression("x"), new IntegerExpression((5)));

        // Perform serialization
        s.accept(visitor);
        String result = visitor.result;
        String expected = "x := 5";

        assertEquals(expected, result);
    }

    @Test
    public void testComposition() {
        SerializeVisitor visitor = new SerializeVisitor();
        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");
        VariableExpression z = new VariableExpression("z");

        Statement s1 = new Assignment(x, new IntegerExpression((5)));
        Statement s2 = new Assignment(y, new IntegerExpression((2)));
        AdditionExpression additionExpression = new AdditionExpression(x, y);
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
        SerializeVisitor visitor = new SerializeVisitor();

        // Variable initialization
        VariableExpression minVar = new VariableExpression("minVar");
        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");

        // Variable assignments
        Statement s1 = new Assignment(x, new IntegerExpression(10));
        Statement s2 = new Assignment(y, new IntegerExpression((100)));

        // If block
        Conditional c = new Le(x, y);
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
        SerializeVisitor visitor = new SerializeVisitor();

        // Const
        IntegerExpression zero = new IntegerExpression(0);
        IntegerExpression one = new IntegerExpression(1);
        IntegerExpression negativeOne = new IntegerExpression(-1);

        // Variable initialization
        VariableExpression result = new VariableExpression("result");
        VariableExpression x = new VariableExpression("x");
        VariableExpression y = new VariableExpression("y");

        // First nested if
        Conditional c1 = new Equal(y, zero);
        Statement s1 = new Assignment(result, one);
        Statement s2 = new Assignment(result, negativeOne);
        Statement s3 = new If(c1, s1, s2);

        // Second nested if
        Conditional c2 = new Equal(y, zero);
        Statement s4 = new Assignment(result, negativeOne);
        Statement s5 = new Assignment(result, one);
        Statement s6 = new If(c2, s4, s5);

        // Outer if statement
        Conditional c3 = new Equal(x, zero);
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
