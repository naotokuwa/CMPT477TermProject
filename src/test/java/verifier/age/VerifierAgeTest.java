package verifier.age;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;
import imp.visitor.serialize.ConditionSerializeVisitor;
import verifier.Verifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;


public class VerifierAgeTest 
{
    private ConditionSerializeVisitor conditionSerializer;
	private StatementSerializeVisitor visitor;
	private Statement program;
    private BinaryExpression calculateAge; // save so we can modify line of program for a test


	private String expectedSerializedProgram()
    {
        String expected = "year := 2024\n";
        expected += "age := 2024 + ( -1 * birthyear )";
        return expected;
	}

	private Statement createProgram()
    {
        // Statement 1
        VariableExpression year = new VariableExpression("year");
        IntegerExpression currentyear1 = new IntegerExpression(2024);
        Assignment statement1 = new Assignment(year, currentyear1);

        // Statement 2
        IntegerExpression negativeOne = new IntegerExpression(-1);
        VariableExpression birthyear = new VariableExpression("birthyear");
        BinaryExpression negativeBirthyear = new BinaryExpression(ExpressionType.MUL, negativeOne, birthyear);
        IntegerExpression currentyear2 = new IntegerExpression(2024);
        calculateAge = new BinaryExpression(ExpressionType.ADD, currentyear2, negativeBirthyear);
        VariableExpression age = new VariableExpression("age");
        Assignment statement2 = new Assignment(age, calculateAge);

		Statement program = new Composition(statement1, statement2);

		// Verify program serialization
		program.accept(visitor);
		String expected = expectedSerializedProgram();
		assertEquals(expected, visitor.result);

		return program;
	}

    @BeforeEach public void setUp() 
    {
        visitor = new StatementSerializeVisitor();
        this.program = createProgram();
        conditionSerializer = new ConditionSerializeVisitor();
    }

    private void assert_expected(String expected, Condition cond)
    {
        cond.accept(conditionSerializer);
        assertEquals(conditionSerializer.result, expected);
    }

    private BinaryCondition make_q()
    {
        IntegerExpression min = new IntegerExpression(-1);
        // - birthyear
        VariableExpression birthyear = new VariableExpression("birthyear");
        BinaryExpression min_by = new BinaryExpression(ExpressionType.MUL, min, birthyear);
        // 2024 - birthyear
        IntegerExpression ttf = new IntegerExpression(2024);
        BinaryExpression add = new BinaryExpression(ExpressionType.ADD, ttf, min_by);
        // age == 2024 - birthyear
        VariableExpression age = new VariableExpression("age");
        BinaryCondition post = new BinaryCondition(ConditionType.EQUAL, age, add);

        return post;
    }

    
    // ======================================= valid methods =======================================
    @Test
    void AgeValidWithPre()
    {
        // birthyear <= 2024
        VariableExpression by = new VariableExpression("birthyear");
        IntegerExpression ttf = new IntegerExpression(2024);
        BinaryCondition pre = new BinaryCondition(ConditionType.LE, by, ttf);

        BinaryCondition post = make_q();


        // check pre and post created correctly
        assert_expected("birthyear <= 2024", pre);
        assert_expected("age == 2024 + ( -1 * birthyear )", post);


        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, pre, post));
    }

    @Test
    void AgeValidWithoutPre()
    {
        BinaryCondition post = make_q();


        // check pre and post created correctly
        assert_expected("age == 2024 + ( -1 * birthyear )", post);


        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, post));
    }


    // ====================================== invalid methods ======================================
    @Test
    void AgeInvalidWithPre()
    {
        // birthyear <= 2024
        VariableExpression by = new VariableExpression("birthyear");
        IntegerExpression ttf = new IntegerExpression(2024);
        BinaryCondition pre = new BinaryCondition(ConditionType.LE, by, ttf);

        VariableExpression birthyear = new VariableExpression("birthyear");
        // 2024 + birthyear
        IntegerExpression ttf2 = new IntegerExpression(2024);
        BinaryExpression add = new BinaryExpression(ExpressionType.ADD, ttf2, birthyear);
        // age == 2024 + birthyear
        VariableExpression age = new VariableExpression("age");
        BinaryCondition post = new BinaryCondition(ConditionType.EQUAL, age, add);


        // check pre and post created correctly
        assert_expected("birthyear <= 2024", pre);
        assert_expected("age == 2024 + birthyear", post);


        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, pre, post));


        /* Test counterexamples */
        Map<String, Integer> map = verifier.getCounterexampleMap();
        System.out.println(map);
        assertTrue(map.get("birthyear") <= 2024);
    }

    @Test
    void AgeInvalidWithoutPre()
    {
        VariableExpression birthyear = new VariableExpression("birthyear");
        // 2024 + birthyear
        IntegerExpression ttf2 = new IntegerExpression(2024);
        BinaryExpression add = new BinaryExpression(ExpressionType.ADD, ttf2, birthyear);
        // age == 2024 + birthyear
        VariableExpression age = new VariableExpression("age");
        BinaryCondition post = new BinaryCondition(ConditionType.EQUAL, age, add);


        // check pre and post created correctly
        assert_expected("age == 2024 + birthyear", post);


        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, post));


        /* Test counterexamples */
        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.get("birthyear") <= 2024);
    }


    // ======================================= wrong methods =======================================
    @Test
    void AgeInvalidValidSpec()
    {
       // modify age := 2024 + ( -1 * birthyear ); to age := 2024 + birthyear;
       calculateAge.right = new VariableExpression("birthyear");

       // check changed properly
       String expected = expectedSerializedProgram();
       expected = expected.replace("age := 2024 + ( -1 * birthyear )", "age := 2024 + birthyear");
       program.accept(visitor);
       assertEquals(expected, visitor.result);


        BinaryCondition post = make_q();
        assert_expected("age == 2024 + ( -1 * birthyear )", post);


        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, post));


        /* Test counterexamples */
        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.containsKey("birthyear"));
    }
}