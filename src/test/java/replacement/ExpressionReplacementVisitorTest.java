package replacement;

import imp.expression.*;
import imp.visitor.replacement.ExpressionReplacementVisitor;
import imp.visitor.replacement.CopyVisitor;
import imp.visitor.serialize.ExpressionSerializeVisitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class ExpressionReplacementVisitorTest {
    private void checkSerializationResult(Expression expression, String expectedResult){
        ExpressionSerializeVisitor visitor = new ExpressionSerializeVisitor();
        expression.accept(visitor);
        String actual = visitor.result;
        assertEquals(expectedResult, actual);
    }

    @Test
    public void testReplaceInteger() {
        // Test scenario
        // Q: {1}
        // S: x := 1
        // Q[1/x] => {1}

        Expression one = new IntegerExpression(1);
        String targetSymbol = "x";
        Expression toReplace = new IntegerExpression(1);
        ExpressionReplacementVisitor visitor = new ExpressionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        one.accept(visitor);
        Expression resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "1";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceVariableToInteger() {
        // Test scenario
        // Q: {x}
        // S: x := 1
        // Q[1/x] => {1}

        Expression x = new VariableExpression("x");
        String targetSymbol = "x";
        Expression toReplace = new IntegerExpression(1);
        ExpressionReplacementVisitor visitor = new ExpressionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        x.accept(visitor);
        Expression resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "1";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceVariableToAddition() {
        // Test scenario
        // Q: {x}
        // S: x := x + 1
        // Q[x + 1 /x] => {x + 1}

        Expression x = new VariableExpression("x");
        String targetSymbol = "x";

        Expression toReplaceLeft = new VariableExpression("x");
        Expression toReplaceRight = new IntegerExpression(1);
        Expression toReplace = new BinaryExpression(ExpressionType.ADD,toReplaceLeft, toReplaceRight);
        ExpressionReplacementVisitor visitor = new ExpressionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        x.accept(visitor);
        Expression resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "x + 1";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceBinaryExpressionToInteger() {
        // Test scenario
        // Q: {x + 1}
        // S: x := 1
        // Q[1/x] => {1 + 1}

        Expression x = new VariableExpression("x");
        Expression one = new IntegerExpression(1);
        Expression addition = new BinaryExpression(ExpressionType.ADD, x, one);
        String targetSymbol = "x";

        Expression toReplace = new IntegerExpression(1);
        ExpressionReplacementVisitor visitor = new ExpressionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        addition.accept(visitor);
        Expression resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "1 + 1";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceBinaryExpressionToVariable() {
        // Test scenario
        // Q: {x + x}
        // S: x := y
        // Q[y/x] => {y + y}

        Expression x = new VariableExpression("x");
        Expression addition = new BinaryExpression(ExpressionType.ADD, x, x);
        String targetSymbol = "x";

        Expression toReplace = new VariableExpression("y");
        ExpressionReplacementVisitor visitor = new ExpressionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        addition.accept(visitor);
        Expression resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "y + y";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceBinaryExpressionToBinaryExpression() {
        // Test scenario
        // Q: {x * x}
        // S: x := y + 1
        // Q[y/x] => { (y + 1) * (y + 1) }

        Expression x = new VariableExpression("x");
        Expression addition = new BinaryExpression(ExpressionType.MUL, x, x);
        String targetSymbol = "x";

        Expression y = new VariableExpression("y");
        Expression one = new IntegerExpression(1);
        Expression toReplace = new BinaryExpression(ExpressionType.ADD, y, one);
        ExpressionReplacementVisitor visitor = new ExpressionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        addition.accept(visitor);
        Expression resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "( y + 1 ) * ( y + 1 )";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceDeepCopy()
    {
        // Test scenario
        // Q: {(x * y) * (x * y)}
        // S: x := y; y := y + 1
        // Q[y/x] => { ((y + 1) * (y + 1)) * ((y + 1) * (y + 1)) }

        CopyVisitor copier = new CopyVisitor();
        ExpressionReplacementVisitor replacer;
        // Q
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression expr1 = new BinaryExpression(ExpressionType.MUL, x, y);
        expr1.accept(copier);
        Expression expr2 = copier.result;
        Expression expr = new BinaryExpression(ExpressionType.MUL, expr1, expr2);

        // replace x
        Expression y1 = new VariableExpression("y");
        replacer = new ExpressionReplacementVisitor("x", y1);
        expr.accept(replacer);
        Expression after1 = replacer.result;

        // replace y
        Expression y2 = new VariableExpression("y");
        Expression one = new IntegerExpression(1);
        BinaryExpression add = new BinaryExpression(ExpressionType.ADD, y2, one);
        replacer = new ExpressionReplacementVisitor("y", add);
        after1.accept(replacer);
        Expression after2 = replacer.result;

        // change repalcement expr to ensure the new result copied it (and not its reference)
        add.left = new VariableExpression("z");
        // Check the serialized expression
        String expected = "( ( y + 1 ) * ( y + 1 ) ) * ( ( y + 1 ) * ( y + 1 ) )";
        checkSerializationResult(after2, expected);
    }
}
