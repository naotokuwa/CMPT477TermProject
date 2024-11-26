package verifier.trajectory;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.ConditionSerializeVisitor;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verifier.Verifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VerifierTrajectoryTest {
    private StatementSerializeVisitor programSerializer;
    private ConditionSerializeVisitor conditionSerializer;
    private Statement validProgram;
    private Statement invalidProgram;
    private Verifier verifier;

    private String expectedSerializedProgram(boolean valid) {
        String expected = "if a == b\n";
        expected += "then\n";
        expected += valid ? "  trajectory := b\n" : "  trajectory := b + 1\n";
        expected += "else\n";
        expected += "    changeInTrajectory := ( a * -1 ) + b\n";
        expected += "    trajectory := b + changeInTrajectory";
        return expected;
    }

    private Statement createProgram(boolean valid) {

        // Condition
        VariableExpression a1 = new VariableExpression("a");
        VariableExpression b1 = new VariableExpression("b");
        Condition condition = new BinaryCondition(ConditionType.EQUAL, a1, b1);

        // Then Block
        VariableExpression trajectory1 = new VariableExpression("trajectory");
        Expression b2 = new VariableExpression("b");
        Expression oneS = new IntegerExpression(1);
        Expression bPlusOne = new BinaryExpression(ExpressionType.ADD, b2, oneS);
        Assignment thenBlock = valid ? new Assignment(trajectory1, b2) : new Assignment(trajectory1, bPlusOne);

        // Else Block
        // ElseBlockStatement1
        VariableExpression a2 = new VariableExpression("a");
        IntegerExpression negOne = new IntegerExpression(-1);
        Expression mul = new BinaryExpression(ExpressionType.MUL, a2, negOne);
        VariableExpression b3 = new VariableExpression("b");
        Expression add = new BinaryExpression(ExpressionType.ADD, mul, b3);
        VariableExpression changeInTrajectory1 = new VariableExpression("changeInTrajectory");
        Assignment changeInTrajectoryAssignment = new Assignment(changeInTrajectory1, add);

        // ElseBlockStatement2
        VariableExpression trajectory3 = new VariableExpression("trajectory");

        VariableExpression b4 = new VariableExpression("b");
        VariableExpression changeInTrajectory2 = new VariableExpression("changeInTrajectory");
        Expression add2 = new BinaryExpression(ExpressionType.ADD, b4, changeInTrajectory2);
        Assignment trajectoryAssignment = new Assignment(trajectory3, add2);

        Statement elseBlock = new Composition(changeInTrajectoryAssignment, trajectoryAssignment);

        // If Statement
        Statement program = new If(condition, thenBlock, elseBlock);

        // Verify Program Serialization
        program.accept(programSerializer);
        String result = programSerializer.result;
        String expected = expectedSerializedProgram(valid);

        assertEquals(expected, result);

        return program;
    }

    @BeforeEach
    public void setUp() {
        programSerializer = new StatementSerializeVisitor();
        conditionSerializer = new ConditionSerializeVisitor();
        this.validProgram = createProgram(true);
        this.invalidProgram = createProgram(false);
        verifier = new Verifier();
    }

    @Test
    void TrajectoryValidNoPrecondition() {
        // Postcondition: a == b ==> trajectory == a
        Condition aEqualB = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new VariableExpression("b"));
        Condition trajEqualA = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("trajectory"),
                new VariableExpression("a"));
        Condition postcondition1 = new BinaryConnective(ConnectiveType.IMPLIES, aEqualB, trajEqualA);

        // Postcondition: !(a == b) ==> trajectory == b + (b + (-1 * a))
        Condition innerc1Left = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new VariableExpression("b"));
        Condition postcondition1Left = new UnaryConnective(ConnectiveType.NOT, innerc1Left);

        Expression negationTerm = new BinaryExpression(ExpressionType.MUL,
                new IntegerExpression(-1),
                new VariableExpression("a"));
        Expression innerSum = new BinaryExpression(ExpressionType.ADD,
                new VariableExpression("b"),
                negationTerm);
        Expression outerSum = new BinaryExpression(ExpressionType.ADD,
                new VariableExpression("b"),
                innerSum);
        Condition postcondition1Right = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("trajectory"),
                outerSum);
        Condition postcondition2 = new BinaryConnective(ConnectiveType.IMPLIES, postcondition1Left, postcondition1Right);

        Condition postcondition = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);


        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "( ( a == b ) ==> ( trajectory == a ) ) AND ( ( NOT( a == b ) ) ==> ( trajectory == b + ( b + ( -1 * a ) ) ) )";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertTrue(verifier.verify(validProgram, postcondition));
    }

    @Test
    void TrajectoryValidWithPrecondition() {
        // Precondition: ensures a == b
        Condition precondition = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new VariableExpression("b"));

        // Postcondition: trajectory == a
        Condition postcondition = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("trajectory"),
                new VariableExpression("a"));

        precondition.accept(conditionSerializer);
        String expectedSerializedPre = "a == b";
        assertEquals(expectedSerializedPre, conditionSerializer.result);

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "trajectory == a";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertTrue(verifier.verify(validProgram, precondition, postcondition));
    }

    @Test
    void TrajectoryInvalidNoPrecondition() {
        // Postcondition: a == b ==> trajectory == a
        Condition aEqualB = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new VariableExpression("b"));
        Condition trajEqualA = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("trajectory"),
                new VariableExpression("a"));
        Condition postcondition1 = new BinaryConnective(ConnectiveType.IMPLIES, aEqualB, trajEqualA);

        // Postcondition: !(a == b) ==> trajectory == b + (-1 * a)
        Condition innerc1Left = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new VariableExpression("b"));
        Condition postcondition1Left = new UnaryConnective(ConnectiveType.NOT, innerc1Left);

        Expression negationTerm = new BinaryExpression(ExpressionType.MUL,
                new IntegerExpression(-1),
                new VariableExpression("a"));
        Expression postResult = new BinaryExpression(ExpressionType.ADD,
                new VariableExpression("b"),
                negationTerm);
        Condition postcondition1Right = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("trajectory"),
                postResult);
        Condition postcondition2 = new BinaryConnective(ConnectiveType.IMPLIES, postcondition1Left, postcondition1Right);

        Condition postcondition = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "( ( a == b ) ==> ( trajectory == a ) ) AND ( ( NOT( a == b ) ) ==> ( trajectory == b + ( -1 * a ) ) )";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertFalse(verifier.verify(validProgram, postcondition));

        /* Test counterexamples */
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertNotEquals(map.get("a"), map.get("b"));
    }


    @Test
    void TrajectoryInvalidWithPrecondition() {
        // Precondition: ensures a == b
        Condition precondition = new BinaryCondition(ConditionType.EQUAL,
        new VariableExpression("a"),
        new VariableExpression("b"));

        // Postcondition: !(trajectory == a)
        Condition trajectoryEqualsA = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("trajectory"),
                new VariableExpression("a"));
        Condition postcondition = new UnaryConnective(ConnectiveType.NOT, trajectoryEqualsA);

        precondition.accept(conditionSerializer);
        String expectedSerializedPre = "a == b";
        assertEquals(expectedSerializedPre, conditionSerializer.result);

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "NOT( trajectory == a )";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertFalse(verifier.verify(validProgram, precondition, postcondition));

        /* Test counterexamples */
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertEquals(map.get("a"), map.get("b"));
    }

    @Test
    void InvalidTrajectoryValidSpec() {
        // Postcondition: a == b ==> trajectory == a
        Condition aEqualB = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new VariableExpression("b"));
        Condition trajEqualA = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("trajectory"),
                new VariableExpression("a"));
        Condition postcondition1 = new BinaryConnective(ConnectiveType.IMPLIES, aEqualB, trajEqualA);

        // Postcondition: !(a == b) ==> trajectory == b + (b + (-1 * a))
        Condition innerc1Left = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("a"),
                new VariableExpression("b"));
        Condition postcondition1Left = new UnaryConnective(ConnectiveType.NOT, innerc1Left);

        Expression negationTerm = new BinaryExpression(ExpressionType.MUL,
                new IntegerExpression(-1),
                new VariableExpression("a"));
        Expression innerSum = new BinaryExpression(ExpressionType.ADD,
                new VariableExpression("b"),
                negationTerm);
        Expression outerSum = new BinaryExpression(ExpressionType.ADD,
                new VariableExpression("b"),
                innerSum);
        Condition postcondition1Right = new BinaryCondition(ConditionType.EQUAL,
                new VariableExpression("trajectory"),
                outerSum);
        Condition postcondition2 = new BinaryConnective(ConnectiveType.IMPLIES, postcondition1Left, postcondition1Right);

        Condition postcondition = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "( ( a == b ) ==> ( trajectory == a ) ) AND ( ( NOT( a == b ) ) ==> ( trajectory == b + ( b + ( -1 * a ) ) ) )";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertFalse(verifier.verify(invalidProgram, postcondition));

        /* Test counterexamples */
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertEquals(map.get("a"), map.get("b"));
    }
}