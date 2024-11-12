package replacement;

import imp.expression.*;
import imp.visitor.replacement.ExpressionReplacementVisitor;
import imp.visitor.serialize.ExpressionSerializeVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
