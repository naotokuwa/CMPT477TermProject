package replacement;

import imp.condition.*;
import imp.condition.Boolean;
import imp.expression.*;
import imp.visitor.replacement.ConditionReplacementVisitor;
import imp.visitor.serialize.ConditionSerializeVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionReplacementVisitorTest {
    private void checkSerializationResult(Condition condition, String expectedResult){
        ConditionSerializeVisitor visitor = new ConditionSerializeVisitor();
        condition.accept(visitor);
        String actual = visitor.result;
        assertEquals(expectedResult, actual);
    }

    @Test
    public void testReplaceTrue() {
        // Test scenario
        // Q: {true}
        // S: x := 1
        // Q[1/x] => {true}

        Condition booleanTrue = new Boolean(true);
        String targetSymbol = "x";
        Expression toReplace = new IntegerExpression(1);
        ConditionReplacementVisitor visitor = new ConditionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        booleanTrue.accept(visitor);
        Condition resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "true";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceFalse() {
        // Test scenario
        // Q: {false}
        // S: x := 1
        // Q[1/x] => {false}

        Condition booleanTrue = new Boolean(false);
        String targetSymbol = "x";
        Expression toReplace = new IntegerExpression(1);
        ConditionReplacementVisitor visitor = new ConditionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        booleanTrue.accept(visitor);
        Condition resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "false";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceBinaryConditionToInteger() {
        // Test scenario
        // Q: {x == 1}
        // S: x := 1
        // Q[1/x] => {1 == 1}

        Expression x = new VariableExpression("x");
        Expression one = new IntegerExpression(1);
        Condition equalCondition = new BinaryCondition(ConditionType.EQUAL, x, one);

        String targetSymbol = "x";
        Expression toReplace = new IntegerExpression(1);
        ConditionReplacementVisitor visitor = new ConditionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        equalCondition.accept(visitor);
        Condition resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "1 == 1";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceBinaryConditionToVariable() {
        // Test scenario
        // Q: {x <= 1}
        // S: x := y
        // Q[y/x] => {y <= 1}

        Expression x = new VariableExpression("x");
        Expression one = new IntegerExpression(1);
        Condition equalCondition = new BinaryCondition(ConditionType.LE, x, one);

        String targetSymbol = "x";
        Expression toReplace = new VariableExpression("y");
        ConditionReplacementVisitor visitor = new ConditionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        equalCondition.accept(visitor);
        Condition resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "y <= 1";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceBinaryConditionToBinaryExpression() {
        // Test scenario
        // Q: {x == y}
        // S: x := x + 1
        // Q[x + 1/x] => {x + 1 == y}

        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Condition equalCondition = new BinaryCondition(ConditionType.EQUAL, x, y);

        String targetSymbol = "x";
        Expression toReplace = new BinaryExpression(ExpressionType.ADD, x, new IntegerExpression(1));
        ConditionReplacementVisitor visitor = new ConditionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        equalCondition.accept(visitor);
        Condition resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "x + 1 == y";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testReplaceBinaryConditionToNestedBinaryExpression() {
        // Test scenario
        // Q: {x == y}
        // S: x := ( z + 1 ) * x
        // Q[( z + 1 ) + x/x] => {(z + 1 ) * x == y}

        // Expressions
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression z = new VariableExpression("z");
        Expression one = new IntegerExpression(1);

        Condition equalCondition = new BinaryCondition(ConditionType.EQUAL, x, y);

        String targetSymbol = "x";
        Expression addition = new BinaryExpression(ExpressionType.ADD, z, one);
        Expression toReplace = new BinaryExpression(ExpressionType.MUL, addition, x);
        ConditionReplacementVisitor visitor = new ConditionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        equalCondition.accept(visitor);
        Condition resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "( z + 1 ) * x == y";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testBinaryConnectiveReplacement() {
        // Test scenario
        // Q: {( x == y ) AND ( x <= z )}
        // S: z := x + 1
        // Q[( x + 1 )/z] => {( x == y ) AND ( x <= x + 1 )}

        // Expressions
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression z = new VariableExpression("z");
        Expression one = new IntegerExpression(1);

        Condition equalCondition = new BinaryCondition(ConditionType.EQUAL, x, y);
        Condition leCondition = new BinaryCondition(ConditionType.LE, x, z);
        Condition andCondition = new BinaryConnective(ConnectiveType.AND, equalCondition, leCondition);

        String targetSymbol = "z";
        Expression toReplace = new BinaryExpression(ExpressionType.ADD, x, one);
        ConditionReplacementVisitor visitor = new ConditionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        andCondition.accept(visitor);
        Condition resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "( x == y ) AND ( x <= x + 1 )";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testUnaryConnectiveReplacement() {
        // Test scenario
        // Q: {NOT(x == y + 1)}
        // S: y := y + 100
        // Q[( y + 100 )/y] => {NOT( x == ( y + 100 ) + 1 )}

        // Expressions
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression one = new IntegerExpression(1);
        Expression hundred = new IntegerExpression(100);
        Expression addition = new BinaryExpression(ExpressionType.ADD, y, one);

        Condition equalCondition = new BinaryCondition(ConditionType.EQUAL, x, addition);
        Condition notCondition = new UnaryConnective(ConnectiveType.NOT, equalCondition);

        String targetSymbol = "y";
        Expression toReplace = new BinaryExpression(ExpressionType.ADD, y, hundred);
        ConditionReplacementVisitor visitor = new ConditionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        notCondition.accept(visitor);
        Condition resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "NOT( x == ( y + 100 ) + 1 )";
        checkSerializationResult(resultAfterReplacement, expected);
    }

    @Test
    public void testComplexConnectiveReplacement() {
        // Test scenario
        // Q: {( ( x == 0 ) ==> ( y <= 0 ) ) AND ( NOT( x == 0 ) ==> ( y <= 0 ) )}
        // S: x := x + 100
        // Q[( x + 100 )/x] => {( ( x + 100 == 0 ) ==> ( y <= 0 ) ) AND ( ( NOT( x + 100 == 0 ) ) ==> ( y <= 0 ) )}

        // Need to construct each expression and condition separately to avoid interference of object reference.
        // DO NOT re-use the same object in the real code

        // First Imply
        Expression x1 = new VariableExpression("x");
        Expression y1 = new VariableExpression("y");
        Expression zero1 = new IntegerExpression(0);
        Expression zero2 = new IntegerExpression(0);
        Condition xCondition1 = new BinaryCondition(ConditionType.EQUAL, x1, zero1);
        Condition yCondition1 = new BinaryCondition(ConditionType.LE, y1, zero2);
        Condition firstImply = new BinaryConnective(ConnectiveType.IMPLIES, xCondition1, yCondition1);

        // Second Imply
        Expression x2 = new VariableExpression("x");
        Expression y2 = new VariableExpression("y");
        Expression zero3 = new IntegerExpression(0);
        Expression zero4 = new IntegerExpression(0);
        Condition xCondition2 = new BinaryCondition(ConditionType.EQUAL, x2, zero3);
        Condition yCondition2 = new BinaryCondition(ConditionType.LE, y2, zero4);
        Condition notXCondition2 = new UnaryConnective(ConnectiveType.NOT, xCondition2);
        Condition secondImply = new BinaryConnective(ConnectiveType.IMPLIES, notXCondition2, yCondition2);

        Condition andCondition = new BinaryConnective(ConnectiveType.AND, firstImply, secondImply);

        String targetSymbol = "x";
        Expression x = new VariableExpression("x");
        Expression hundred = new IntegerExpression(100);
        Expression toReplace = new BinaryExpression(ExpressionType.ADD, x, hundred);
        ConditionReplacementVisitor visitor = new ConditionReplacementVisitor(targetSymbol, toReplace);

        // Perform Replacement
        andCondition.accept(visitor);
        Condition resultAfterReplacement = visitor.result;

        // Check the serialized expression
        String expected = "( ( x + 100 == 0 ) ==> ( y <= 0 ) ) AND ( ( NOT( x + 100 == 0 ) ) ==> ( y <= 0 ) )";
        checkSerializationResult(resultAfterReplacement, expected);
    }
}
