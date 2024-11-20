package verifier.signOfInteger;

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

public class VerifierSignOfIntegerTest {
    private StatementSerializeVisitor statementSerializeVisitor;
    private ConditionSerializeVisitor conditionSerializer;
    private Statement program;

    private String expectedSerializedProgram(){
        String expected = "if x == 0\n";
        expected += "then\n";
        expected += "  y := 0\n";
        expected += "else\n";
        expected += "  if x <= 0\n";
        expected += "  then\n";
        expected += "    y := -1\n";
        expected += "  else\n";
        expected += "    y := 1";
        return expected;
    }

    private String expectedSerializedWrongProgram(){
        String expected = "if x <= 0\n";
        expected += "then\n";
        expected += "  y := -1\n";
        expected += "else\n";
        expected += "  y := 1";
        return expected;
    }

    private Statement createProgram(){
        // -- Inner If --
        // Condition
        Expression x1 = new VariableExpression("x");
        IntegerExpression zero1 = new IntegerExpression(0);
        Condition innerIfCondition = new BinaryCondition(ConditionType.LE, x1, zero1);
        // Then Block
        VariableExpression y1 = new VariableExpression("y");
        IntegerExpression negativeOne = new IntegerExpression(-1);
        Assignment innerIfThenBlock = new Assignment(y1, negativeOne);
        // Else Block
        VariableExpression y2 = new VariableExpression("y");
        IntegerExpression one = new IntegerExpression(1);
        Assignment innerIfElseBlock = new Assignment(y2, one);
        // Inner If Statement
        Statement innerIfStatement = new If(innerIfCondition, innerIfThenBlock, innerIfElseBlock);


        // -- Outer If --
        // Condition
        Expression x2 = new VariableExpression("x");
        IntegerExpression zero2 = new IntegerExpression(0);
        Condition outerIfCondition = new BinaryCondition(ConditionType.EQUAL, x2, zero2);
        // Then Block
        VariableExpression y3 = new VariableExpression("y");
        IntegerExpression zero3 = new IntegerExpression(0);
        Assignment outerIfThenBlock = new Assignment(y3, zero3);
        // Else Block
        // See Inner If Statement
        // Outer If Statement
        Statement outerIfStatement = new If(outerIfCondition, outerIfThenBlock, innerIfStatement);

		Statement program = outerIfStatement;

		// Verify program serialization
		program.accept(statementSerializeVisitor);
		String result = statementSerializeVisitor.result;
		String expected = expectedSerializedProgram();

		assertEquals(result, expected);

		return program;
	}

    private Statement createWrongProgram(){
        // Condition
        Expression x1 = new VariableExpression("x");
        IntegerExpression zero1 = new IntegerExpression(0);
        Condition innerIfCondition = new BinaryCondition(ConditionType.LE, x1, zero1);

        // Then Block
        VariableExpression y1 = new VariableExpression("y");
        IntegerExpression negativeOne = new IntegerExpression(-1);
        Assignment innerIfThenBlock = new Assignment(y1, negativeOne);

        // Else Block
        VariableExpression y2 = new VariableExpression("y");
        IntegerExpression one = new IntegerExpression(1);
        Assignment innerIfElseBlock = new Assignment(y2, one);

        // If Statement
        Statement toReturn = new If(innerIfCondition, innerIfThenBlock, innerIfElseBlock);

		// Verify program serialization
		toReturn.accept(statementSerializeVisitor);
		String result = statementSerializeVisitor.result;
		String expected = expectedSerializedWrongProgram();

		assertEquals(result, expected);

		return toReturn;
	}

    @BeforeEach public void setUp() {
        statementSerializeVisitor = new StatementSerializeVisitor();
        conditionSerializer = new ConditionSerializeVisitor();
        program = createProgram();
    }

    private Condition createXIsZero(){
        Expression x = new VariableExpression("x");
        Expression zero = new IntegerExpression(0);
        return new BinaryCondition(ConditionType.EQUAL, x, zero);
    }

    private Condition createXLessThanZero(){
        // Create (x <= 0)
        Expression x1 = new VariableExpression("x");
        Expression zero1 = new IntegerExpression(0);
        Condition condition = new BinaryCondition(ConditionType.LE, x1, zero1);

        // Create NOT(x == 0)
        Condition xIsZero = createXIsZero();
        Condition xIsNotZero = new UnaryConnective(ConnectiveType.NOT, xIsZero);

        return new BinaryConnective(ConnectiveType.AND, condition, xIsNotZero);
    }

    private Condition createXGreaterThanZero(){
        // Create !(x <= 0)
        Expression x = new VariableExpression("x");
        Expression zero = new IntegerExpression(0);
        Condition condition = new BinaryCondition(ConditionType.LE, x, zero);
        return new UnaryConnective(ConnectiveType.NOT, condition);
    }

    private Condition createYIsResult(int expectedResult){
        Expression y = new VariableExpression("y");
        Expression result = new IntegerExpression(expectedResult);
        return new BinaryCondition(ConditionType.EQUAL, y, result);
    }

    @Test
    void SignOfIntegerValidWithPre1(){
        // Precondition
        Condition preCondition = createXGreaterThanZero();
        preCondition.accept(conditionSerializer);

        String expectedPre = "NOT( x <= 0 )";
        String preConditionStr = conditionSerializer.result;

        assertEquals(preConditionStr, expectedPre);

        // Postcondition
        Condition postCondition = createYIsResult(1);
        postCondition.accept(conditionSerializer);

        String postConditionStr = conditionSerializer.result;
        String expectedPost = "y == 1";

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, preCondition, postCondition));
    }

    @Test
    void SignOfIntegerValidWithPre2(){
        // Precondition
        Condition preCondition = createXIsZero();
        preCondition.accept(conditionSerializer);

        String expectedPre = "x == 0";
        String preConditionStr = conditionSerializer.result;

        assertEquals(preConditionStr, expectedPre);

        // Postcondition
        Condition postCondition = createYIsResult(0);
        postCondition.accept(conditionSerializer);

        String expectedPost = "y == 0";
        String postConditionStr = conditionSerializer.result;

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, preCondition, postCondition));
    }

    @Test
    void SignOfIntegerValidWithPre3(){
        // Precondition
        Condition preCondition = createXLessThanZero();
        preCondition.accept(conditionSerializer);

        String expectedPre = "( x <= 0 ) AND ( NOT( x == 0 ) )";
        String preConditionStr = conditionSerializer.result;

        assertEquals(preConditionStr, expectedPre);

        // Postcondition
        Condition postCondition = createYIsResult(-1);
        postCondition.accept(conditionSerializer);

        String expectedPost = "y == -1";
        String postConditionStr = conditionSerializer.result;

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, preCondition, postCondition));
    }

    @Test
    void SignOfIntegerValidWithOutPre(){
        // Postcondition
        Condition xGreaterThanZero = createXGreaterThanZero();
        Condition xIsZero = createXIsZero();
        Condition xLessThanZero = createXLessThanZero();

        Condition y1 = createYIsResult(1);
        Condition y2 = createYIsResult(0);
        Condition y3 = createYIsResult(-1);

        Condition imply1 = new BinaryConnective(ConnectiveType.IMPLIES, xGreaterThanZero, y1);
        Condition imply2 = new BinaryConnective(ConnectiveType.IMPLIES, xIsZero, y2);
        Condition imply3 = new BinaryConnective(ConnectiveType.IMPLIES, xLessThanZero, y3);

        Condition postCondition = new BinaryConnective(ConnectiveType.AND, imply1, imply2);
        postCondition = new BinaryConnective(ConnectiveType.AND, postCondition, imply3);

        postCondition.accept(conditionSerializer);
        String postConditionStr = conditionSerializer.result;

        // Expected string result
        String firstImplyStrExpected = "( NOT( x <= 0 ) ) ==> ( y == 1 )";
        String secondImplyStrExpected = "( x == 0 ) ==> ( y == 0 )";
        String thirdImplyStrExpected = "( ( x <= 0 ) AND ( NOT( x == 0 ) ) ) ==> ( y == -1 )";
        String firstAnd = String.format("( %s ) AND ( %s )", firstImplyStrExpected, secondImplyStrExpected);
        String expectedPost = String.format("( %s ) AND ( %s )", firstAnd, thirdImplyStrExpected);

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, postCondition));
    }

    @Test
    void SignOfIntegerInvalidWithoutPre(){
        // Postcondition
        Condition postCondition = createYIsResult(1);
        postCondition.accept(conditionSerializer);

        String postConditionStr = conditionSerializer.result;
        String expectedPost = "y == 1";

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, postCondition));

        // Counter example check
        Map<String, Integer> counterExample = verifier.getCounterexampleMap();
        assertTrue(counterExample.get("x") <= 0);
    }

    @Test
    void SignOfIntegerInvalidWithPre(){
        // Precondition
        Condition preCondition = createXIsZero();
        preCondition.accept(conditionSerializer);

        String expectedPre = "x == 0";
        String preConditionStr = conditionSerializer.result;

        assertEquals(preConditionStr, expectedPre);

        // Postcondition
        Condition postCondition = createYIsResult(-1);
        postCondition.accept(conditionSerializer);

        String expectedPost = "y == -1";
        String postConditionStr = conditionSerializer.result;

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, postCondition));

        // Counter example check
        Map<String, Integer> counterExample = verifier.getCounterexampleMap();
        assertEquals(counterExample.get("x"), 0);
    }

    @Test
    void WrongSignOfIntegerValidWithoutPre(){
        Statement wrongProgram = createWrongProgram();

        // Postcondition
        Condition xGreaterThanZero = createXGreaterThanZero();
        Condition xIsZero = createXIsZero();
        Condition xLessThanZero = createXLessThanZero();

        Condition y1 = createYIsResult(1);
        Condition y2 = createYIsResult(0);
        Condition y3 = createYIsResult(-1);

        Condition imply1 = new BinaryConnective(ConnectiveType.IMPLIES, xGreaterThanZero, y1);
        Condition imply2 = new BinaryConnective(ConnectiveType.IMPLIES, xIsZero, y2);
        Condition imply3 = new BinaryConnective(ConnectiveType.IMPLIES, xLessThanZero, y3);

        Condition postCondition = new BinaryConnective(ConnectiveType.AND, imply1, imply2);
        postCondition = new BinaryConnective(ConnectiveType.AND, postCondition, imply3);


        postCondition.accept(conditionSerializer);
        String postConditionStr = conditionSerializer.result;

        // Expected string result
        String firstImplyStrExpected = "( NOT( x <= 0 ) ) ==> ( y == 1 )";
        String secondImplyStrExpected = "( x == 0 ) ==> ( y == 0 )";
        String thirdImplyStrExpected = "( ( x <= 0 ) AND ( NOT( x == 0 ) ) ) ==> ( y == -1 )";
        String firstAnd = String.format("( %s ) AND ( %s )", firstImplyStrExpected, secondImplyStrExpected);
        String expectedPost = String.format("( %s ) AND ( %s )", firstAnd, thirdImplyStrExpected);

        assertEquals(postConditionStr, expectedPost);

        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(wrongProgram, postCondition));

        // Counter example check
        Map<String, Integer> counterExample = verifier.getCounterexampleMap();
        assertEquals(counterExample.get("x") , 0);
    }

}