package verifier.largestOfThree;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.ConditionSerializeVisitor;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verifier.Verifier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
public class VerifierLargestOfThree {
    private StatementSerializeVisitor programSerializer;
    private ConditionSerializeVisitor conditionSerializer;
    private Statement validProgram;
    private Statement invalidProgram;
    private Verifier verifier;

    private String expectedSerializedProgram(boolean valid) {
        String expected = "largest := x\n";
        expected += "if largest <= y\n";
        expected += "then\n";
        expected += "  largest := y\n";
        expected += "else\n";
        expected += "  tmp := 1\n";
        expected += "if largest <= z\n";
        expected += "then\n";
        expected += valid ? "  largest := z\n" 
                          : "  largest := x\n";
        expected += "else\n";
        expected += "  tmp := 1";
        return expected;
    }

    private Statement createMeaningLessStatement() {
        VariableExpression tmp = new VariableExpression("tmp");
        Expression one = new IntegerExpression(1);
        Assignment statement = new Assignment(tmp, one);
        return statement;
    }

    private Statement createProgram(boolean valid) {

        // -- statement 1 --
        VariableExpression largest1 = new VariableExpression("largest");
        VariableExpression x1 = new VariableExpression("x");
        Assignment statement1 = new Assignment(largest1, x1);

        // -- statement 2 --
        // statement2Condition
        VariableExpression largest2 = new VariableExpression("largest");
        VariableExpression y1 = new VariableExpression("y");
        Condition statement2Condition = new BinaryCondition(ConditionType.LE, largest2, y1);
        // statement2ThenBlock
        VariableExpression largest3 = new VariableExpression("largest");
        VariableExpression y2 = new VariableExpression("y");
        Assignment statement2ThenBlock = new Assignment(largest3, y2);
        // statement2ElseBlock
        Statement statement2ElseBlock = createMeaningLessStatement();
        // statement2IfStatement
        Statement statement2IfStatement = new If(statement2Condition, statement2ThenBlock, statement2ElseBlock);

        // -- statement 3 --
        // statement3Condition
        VariableExpression largest4 = new VariableExpression("largest");
        VariableExpression z1 = new VariableExpression("z");
        Condition statement3Condition = new BinaryCondition(ConditionType.LE, largest4, z1);
        // statement3ThenBlock
        VariableExpression largest5 = new VariableExpression("largest");
        VariableExpression z2 = valid ? new VariableExpression("z")
                                      : new VariableExpression("x");
        Assignment statement3ThenBlock = new Assignment(largest5, z2);
        // statement3ElseBlock
        Statement statement3ElseBlock = createMeaningLessStatement();
        // statement3IfStatement
        Statement statement3IfStatement = new If(statement3Condition, statement3ThenBlock, statement3ElseBlock);

        Statement comp1 = new Composition(statement1, statement2IfStatement);
        Statement comp2 = new Composition(comp1, statement3IfStatement);
        Statement program = comp2;

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
    void LargestOfThreeValidNoPrecondition() {
        // Postcondition: x <= largest && y <= largest && z <= largest
        Condition postcondition1 = new BinaryCondition(ConditionType.LE, new VariableExpression("x"), new VariableExpression("largest"));
        Condition postcondition2 = new BinaryCondition(ConditionType.LE, new VariableExpression("y"), new VariableExpression("largest"));
        Condition postcondition3 = new BinaryCondition(ConditionType.LE, new VariableExpression("z"), new VariableExpression("largest"));
        
        Condition combine1and2 = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);
        Condition combine12and3 = new BinaryConnective(ConnectiveType.AND, combine1and2, postcondition3);

        combine12and3.accept(conditionSerializer);
        String expectedSerializedPost = "( ( x <= largest ) AND ( y <= largest ) ) AND ( z <= largest )";
        
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertTrue(verifier.verify(validProgram, combine12and3));
    }

    @Test
    void LargestOfThreeValidWithPrecondition() {
        // Precondition: x <= y && y <= z
        Condition precondition1 = new BinaryCondition(ConditionType.LE, new VariableExpression("x"), new VariableExpression("y"));
        Condition precondition2 = new BinaryCondition(ConditionType.LE, new VariableExpression("y"), new VariableExpression("z"));
        
        Condition combinedPrecondition = new BinaryConnective(ConnectiveType.AND, precondition1, precondition2);
    
        // Postcondition: x <= largest && y <= largest && z == largest
        Condition postcondition1 = new BinaryCondition(ConditionType.LE, new VariableExpression("x"), new VariableExpression("largest"));
        Condition postcondition2 = new BinaryCondition(ConditionType.LE, new VariableExpression("y"), new VariableExpression("largest"));
        Condition postcondition3 = new BinaryCondition(ConditionType.EQUAL, new VariableExpression("z"), new VariableExpression("largest"));
    
        Condition combinedPostcondition1 = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);
        Condition combinedPostcondition = new BinaryConnective(ConnectiveType.AND, combinedPostcondition1, postcondition3);
    
        // Serialize the conditions
        combinedPrecondition.accept(conditionSerializer);
        String expectedSerializedPre = "( x <= y ) AND ( y <= z )";
        assertEquals(expectedSerializedPre, conditionSerializer.result);
    
        combinedPostcondition.accept(conditionSerializer);
        String expectedSerializedPost = "( ( x <= largest ) AND ( y <= largest ) ) AND ( z == largest )";
        assertEquals(expectedSerializedPost, conditionSerializer.result);
    
        // Verify with precondition and postcondition
        assertTrue(verifier.verify(validProgram, combinedPrecondition, combinedPostcondition));
    }

    @Test
    void LargestOfThreeInvalidWithPrecondition() {
        // Precondition: x == y
        Condition precondition = new BinaryCondition(ConditionType.EQUAL, new VariableExpression("x"), new VariableExpression("y"));

        // Postcondition: x == largest
        Condition postcondition = new BinaryCondition(ConditionType.EQUAL, new VariableExpression("x"), new VariableExpression("largest"));

        precondition.accept(conditionSerializer);
        String expectedSerializedPre = "x == y";
        assertEquals(expectedSerializedPre, conditionSerializer.result);

        postcondition.accept(conditionSerializer);
        String expectedSerializedPost = "x == largest";
        assertEquals(expectedSerializedPost, conditionSerializer.result);

        assertFalse(verifier.verify(validProgram, precondition, postcondition));

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertNotEquals(map.get("x"), map.get("largest")); // post condition not met
    }

    @Test
    void LargestOfThreeInvalidWithoutPre() {
        // Postcondition: y == largest
        Condition postcondition = new BinaryCondition(ConditionType.EQUAL, new VariableExpression("y"), new VariableExpression("largest"));

        postcondition.accept(conditionSerializer);
        String serializedPost = conditionSerializer.result;

        String expectedSerializedPost = "y == largest";

        assertEquals(expectedSerializedPost, serializedPost);

        assertFalse(verifier.verify(validProgram, postcondition));

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertNotEquals(map.get("y"), map.get("largest")); // post condition not met
    }

    @Test
    void LargestOfThreeWrong() {
        // Postcondition: x <= largest && y <= largest && z <= largest
        Condition postcondition1 = new BinaryCondition(ConditionType.LE, new VariableExpression("x"), new VariableExpression("largest"));
        Condition postcondition2 = new BinaryCondition(ConditionType.LE, new VariableExpression("y"), new VariableExpression("largest"));
        Condition postcondition3 = new BinaryCondition(ConditionType.LE, new VariableExpression("z"), new VariableExpression("largest"));

        Condition combine1and2 = new BinaryConnective(ConnectiveType.AND, postcondition1, postcondition2);
        Condition combine12and3 = new BinaryConnective(ConnectiveType.AND, combine1and2, postcondition3);

        combine12and3.accept(conditionSerializer);
        String serializedPost = conditionSerializer.result;

        String expectedSerializedPost = "( ( x <= largest ) AND ( y <= largest ) ) AND ( z <= largest )";

        assertEquals(expectedSerializedPost, serializedPost);

        assertFalse(verifier.verify(invalidProgram, combine12and3));

        /* Test counterexamples */

        // Testing strings is difficult since z3 can return different values
        String counterexampleString = verifier.getCounterexampleString();
        assertNotEquals("", counterexampleString);

        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.get("z") > map.get("x"));
        assertTrue(map.get("z") != map.get("largest")); // z is never largest.
    }
}
