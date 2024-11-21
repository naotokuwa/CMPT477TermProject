package verifier.equality;

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

public class VerifierEqualityTest {
    private StatementSerializeVisitor statementSerializeVisitor;
    private ConditionSerializeVisitor conditionSerializer;
    private Statement program;

	private String expectedSerializedProgram(){
        String expected = "if x <= y\n";
        expected += "then\n";
        expected += "  if y <= x\n";
        expected += "  then\n";
        expected += "    equal := 1\n";
        expected += "  else\n";
        expected += "    equal := 0\n";
        expected += "else\n";
        expected += "  equal := 0";
        return expected;
	}

    private String expectedSerializedWrongProgram(){
        String expected = "if x <= y\n";
        expected += "then\n";
        expected += "  if y <= x\n";
        expected += "  then\n";
        expected += "    equal := 1\n";
        expected += "  else\n";
        expected += "    equal := 0\n";
        expected += "else\n";
        expected += "  equal := 1";
        return expected;
	}
	
	private Statement createProgram(){

        // -- Outer If Statement --
        // Outer Condition
        Expression x1 = new VariableExpression("x");
        Expression y1 = new VariableExpression("y");
        Condition outerCondition = new BinaryCondition(ConditionType.LE, x1, y1);

        // -- Outer Then / Inner If Statement --
        // innerCondition
        VariableExpression y2 = new VariableExpression("y");
        VariableExpression x2 = new VariableExpression("x");
        Condition innerCondition = new BinaryCondition(ConditionType.LE, y2, x2);
        // innerThenBlock
        VariableExpression equal2 = new VariableExpression("equal");
        IntegerExpression one1 = new IntegerExpression(1);
        Assignment innerThenBlock = new Assignment(equal2, one1);
        // innerElseBlock
        VariableExpression equal3 = new VariableExpression("equal");
        IntegerExpression zero2 = new IntegerExpression(0);
        Assignment innerElseBlock = new Assignment(equal3, zero2);
    
        Statement innerIfStatement = new If(innerCondition, innerThenBlock, innerElseBlock);

        // Outer Else
        VariableExpression equal1 = new VariableExpression("equal");
        IntegerExpression zero1 = new IntegerExpression(0);
        Assignment outerElseBlock = new Assignment(equal1, zero1);

        Statement program = new If(outerCondition, innerIfStatement, outerElseBlock);

		// Verify program serialization
		program.accept(statementSerializeVisitor);
		String result = statementSerializeVisitor.result;
		String expected = expectedSerializedProgram();
		
		assertEquals(expected, result);
		
		return program;
	}

    @BeforeEach public void setUp() {
        statementSerializeVisitor = new StatementSerializeVisitor();
        conditionSerializer = new ConditionSerializeVisitor();
        program = createProgram();
    }

    private Condition createEqual(int result){
        Expression equalVar = new VariableExpression("equal");
        Expression one = new IntegerExpression(result);
        return new BinaryCondition(ConditionType.EQUAL, equalVar, one);
    }

    @Test
    void EqualityWithPre1(){
        // Precondition
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression zero1 = new IntegerExpression(0);
        Expression zero2 = new IntegerExpression(0);

        Condition equal1 = new BinaryCondition(ConditionType.EQUAL, x, zero1);
        Condition equal2 = new BinaryCondition(ConditionType.EQUAL, y, zero2);

        Condition preCondition = new BinaryConnective(ConnectiveType.AND, equal1, equal2);
        preCondition.accept(conditionSerializer);

        String expectedPre = "( x == 0 ) AND ( y == 0 )";
        String preConditionStr = conditionSerializer.result;

        assertEquals(preConditionStr, expectedPre);

        // Postcondition
        Condition postCondition = createEqual(1);
        postCondition.accept(conditionSerializer);

        String expectedPost = "equal == 1";
        String postConditionStr = conditionSerializer.result;

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, preCondition, postCondition));
    }

    @Test
    void EqualityWithPre2(){
        // Precondition
        Expression x = new VariableExpression("x");
        Expression y = new VariableExpression("y");
        Expression zero1 = new IntegerExpression(0);
        Expression zero2 = new IntegerExpression(0);

        Condition lessThanZero1 = new BinaryCondition(ConditionType.LE, x, zero1);
        Condition lessThanZero2 = new BinaryCondition(ConditionType.LE, y, zero2);
        Condition greaterThanZero = new UnaryConnective(ConnectiveType.NOT, lessThanZero2);

        Condition preCondition = new BinaryConnective(ConnectiveType.AND, lessThanZero1, greaterThanZero);
        preCondition.accept(conditionSerializer);

        String expectedPre = "( x <= 0 ) AND ( NOT( y <= 0 ) )";
        String preConditionStr = conditionSerializer.result;

        assertEquals(preConditionStr, expectedPre);

        // Postcondition
        Condition postCondition = createEqual(0);
        postCondition.accept(conditionSerializer);

        String expectedPost = "equal == 0";
        String postConditionStr = conditionSerializer.result;

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, preCondition, postCondition));
    }

    @Test
    void EqualityWithoutPre(){
        // Postcondition
        Expression x1 = new VariableExpression("x");
        Expression y1 = new VariableExpression("y");
        Condition xEqualY1 = new BinaryCondition(ConditionType.EQUAL, x1, y1);

        Expression x2 = new VariableExpression("x");
        Expression y2 = new VariableExpression("y");
        Condition xEqualY2 = new BinaryCondition(ConditionType.EQUAL, x2, y2);
        Condition xNotEqualY = new UnaryConnective(ConnectiveType.NOT, xEqualY2);

        Condition equalOne = createEqual(1);
        Condition equalZero = createEqual(0);

        Condition imply1 = new BinaryConnective(ConnectiveType.IMPLIES, xEqualY1, equalOne);
        Condition imply2 = new BinaryConnective(ConnectiveType.IMPLIES, xNotEqualY, equalZero);

        Condition postCondition = new BinaryConnective(ConnectiveType.AND, imply1, imply2);
        postCondition.accept(conditionSerializer);

        String expectedPost = "( ( x == y ) ==> ( equal == 1 ) ) AND ( ( NOT( x == y ) ) ==> ( equal == 0 ) )";
        String postConditionStr = conditionSerializer.result;

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, postCondition));
    }

    @Test
    void EqualityInvalidWithPre1(){
        // Precondition
        Expression x = new VariableExpression("x");
        Expression zero = new IntegerExpression(0);
        Condition preCondition = new BinaryCondition(ConditionType.EQUAL, x, zero);
        preCondition.accept(conditionSerializer);

        String expectedPre = "x == 0";
        String preConditionStr = conditionSerializer.result;

        assertEquals(preConditionStr, expectedPre);

        // Postcondition
        Condition postCondition = createEqual(1);
        postCondition.accept(conditionSerializer);

        String expectedPost = "equal == 1";
        String postConditionStr = conditionSerializer.result;

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, preCondition, postCondition));

        // Counter Example
        Map<String, Integer> counterExample = verifier.getCounterexampleMap();
        assertEquals(counterExample.get("x"), 0);
        assertNotEquals(counterExample.get("y"), 0);
    }

    @Test
    void EqualityInvalidWithPre2(){
        // Precondition
        Expression x = new VariableExpression("x");
        Expression y1 = new VariableExpression("y");
        Expression y2 = new VariableExpression("y");
        Expression addition = new BinaryExpression(ExpressionType.ADD, y1, y2);

        Condition preCondition = new BinaryCondition(ConditionType.EQUAL, x, addition);
        preCondition.accept(conditionSerializer);

        String expectedPre = "x == y + y";
        String preConditionStr = conditionSerializer.result;

        assertEquals(expectedPre, preConditionStr);

        // Postcondition
        Condition postCondition = createEqual(1);
        postCondition.accept(conditionSerializer);

        String expectedPost = "equal == 1";
        String postConditionStr = conditionSerializer.result;

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, preCondition, postCondition));

        // Counter Example
        Map<String, Integer> counterExample = verifier.getCounterexampleMap();
        assertEquals(counterExample.get("x"), counterExample.get("y") * 2);
    }

    @Test
    void WrongEqualityWithoutPre(){
        // Cast back the program to if statement
        If ifStatement = (If)program;
        VariableExpression equalVar = new VariableExpression("equal");
        Expression one = new IntegerExpression(1);
        Statement elseStatement = new Assignment(equalVar, one);
        program = new If(ifStatement.c, ifStatement.thenStatement, elseStatement);

        // Check the modified program
        program.accept(statementSerializeVisitor);
        String result = statementSerializeVisitor.result;
        assertEquals(result, expectedSerializedWrongProgram());

        // Postcondition
        Expression x1 = new VariableExpression("x");
        Expression y1 = new VariableExpression("y");
        Condition xEqualY1 = new BinaryCondition(ConditionType.EQUAL, x1, y1);

        Expression x2 = new VariableExpression("x");
        Expression y2 = new VariableExpression("y");
        Condition xEqualY2 = new BinaryCondition(ConditionType.EQUAL, x2, y2);
        Condition xNotEqualY = new UnaryConnective(ConnectiveType.NOT, xEqualY2);

        Condition equalOne = createEqual(1);
        Condition equalZero = createEqual(0);

        Condition imply1 = new BinaryConnective(ConnectiveType.IMPLIES, xEqualY1, equalOne);
        Condition imply2 = new BinaryConnective(ConnectiveType.IMPLIES, xNotEqualY, equalZero);

        Condition postCondition = new BinaryConnective(ConnectiveType.AND, imply1, imply2);
        postCondition.accept(conditionSerializer);

        String expectedPost = "( ( x == y ) ==> ( equal == 1 ) ) AND ( ( NOT( x == y ) ) ==> ( equal == 0 ) )";
        String postConditionStr = conditionSerializer.result;

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, postCondition));

        // Counter Example
        Map<String, Integer> counterExample = verifier.getCounterexampleMap();
        System.out.println(verifier.getCounterexampleString());
        assertTrue(counterExample.get("x") > counterExample.get("y"));
    }
}