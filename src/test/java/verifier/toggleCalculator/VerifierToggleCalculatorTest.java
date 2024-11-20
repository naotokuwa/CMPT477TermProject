package verifier.toggleCalculator;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.ConditionSerializeVisitor;
import imp.visitor.serialize.StatementSerializeVisitor;
import imp.visitor.replacement.CondCopyVisitor;
import imp.visitor.replacement.ExprCopyVisitor;
import verifier.Verifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;



public class VerifierToggleCalculatorTest {
	private StatementSerializeVisitor visitor;
    private ConditionSerializeVisitor conditionSerializer;
	private Statement program;
    private BinaryExpression change_mul; // saves specific line so we can change it for a test


	private String expectedSerializedProgram()
    {
	    String expected = "if toggleAddMul == 1\n";
        expected += "then\n";
        expected += "  result := a + b\n";
        expected += "else\n";
        expected += "  result := a * b";
	    return expected;
	}

	private Statement createProgram()
    {
        // Condition
        VariableExpression toggleAddMul1 = new VariableExpression("toggleAddMul");
        IntegerExpression one1 = new IntegerExpression(1);
        BinaryCondition condition = new BinaryCondition(ConditionType.EQUAL, toggleAddMul1, one1);

        // Then Block
        VariableExpression result1 = new VariableExpression("result");

        VariableExpression a1 = new VariableExpression("a");
        VariableExpression b1 = new VariableExpression("b");
        BinaryExpression add = new BinaryExpression(ExpressionType.ADD, a1, b1);

        Assignment thenBlock = new Assignment(result1, add);

        // Else Block
        VariableExpression result2 = new VariableExpression("result");

        VariableExpression a2 = new VariableExpression("a");
        VariableExpression b2 = new VariableExpression("b");
        // save so we can change for ToggleCalculatorInvalidValidSpec
        change_mul = new BinaryExpression(ExpressionType.MUL, a2, b2);

        Assignment elseBlock = new Assignment(result2, change_mul);

        // If Statement
        Statement program = new If(condition, thenBlock, elseBlock);

		// Verify program serialization
		program.accept(visitor);
		String expected = expectedSerializedProgram();
		assertEquals(expected, visitor.result);

		return program;
	}

    @BeforeEach
    public void setUp()
    {
        visitor = new StatementSerializeVisitor();
        this.program = createProgram();
        conditionSerializer = new ConditionSerializeVisitor();
    }

    // helper funcs to reduce repetition
    private BinaryConnective make_q()
    {
        ExprCopyVisitor copier = new ExprCopyVisitor();

        // toggleAddMul == 1
        VariableExpression mul = new VariableExpression("toggleAddMul");
        IntegerExpression one = new IntegerExpression(1);
        BinaryCondition mul_eq_1 = new BinaryCondition(ConditionType.EQUAL, mul, one);
        // a + b
        VariableExpression a = new VariableExpression("a");
        VariableExpression b = new VariableExpression("b");
        BinaryExpression a_plus_b = new BinaryExpression(ExpressionType.ADD, a, b);
        // result == a + b
        VariableExpression res = new VariableExpression("result");
        BinaryCondition res_eq_add = new BinaryCondition(ConditionType.EQUAL, res, a_plus_b);
        // (toggleAddMul == 1) ==> (result == a + b)
        BinaryConnective q_1 = new BinaryConnective(ConnectiveType.IMPLIES, mul_eq_1, res_eq_add);

        // (toggleAddMul == 0) ==> (result == a * b)
        // toggleAddMul == 0
        mul.accept(copier);
        IntegerExpression zero = new IntegerExpression(0);
        BinaryCondition mul_eq_0 = new BinaryCondition(ConditionType.EQUAL, copier.result, zero);
        // a * b
        VariableExpression a1 = new VariableExpression("a");
        VariableExpression b1 = new VariableExpression("b");
        BinaryExpression ab = new BinaryExpression(ExpressionType.MUL, a1, b1);
        // result == a * b
        res.accept(copier);
        BinaryCondition res_eq_mult = new BinaryCondition(ConditionType.EQUAL, copier.result, ab);
        // (toggleAddMul == 0) ==> (result == a * b)
        BinaryConnective q_2 = new BinaryConnective(ConnectiveType.IMPLIES, mul_eq_0, res_eq_mult);

        // (toggleAddMul == 1) ==> (result == a + b) && (toggleAddMul == 0) ==> (result == a * b)
        BinaryConnective ret = new BinaryConnective(ConnectiveType.AND, q_1, q_2);
        return ret;
    }

    private void assert_expected(String expected, Condition cond)
    {
        cond.accept(conditionSerializer);
        assertEquals(conditionSerializer.result, expected);
    }


    // ======================================= valid methods =======================================
    @Test
    void ToggleCalculatorValidWithPre()
    {
        ExprCopyVisitor copier = new ExprCopyVisitor();

        // toggleAddMul == 1
        VariableExpression mul = new VariableExpression("toggleAddMul");
        IntegerExpression one = new IntegerExpression(1);
        BinaryCondition mul_eq_1 = new BinaryCondition(ConditionType.EQUAL, mul, one);
        // toggleAddMul == 0
        IntegerExpression zero = new IntegerExpression(0);
        mul.accept(copier);
        BinaryCondition mul_eq_0 = new BinaryCondition(ConditionType.EQUAL, copier.result, zero);
        // toggleAddMul == 1 || toggleAddMul == 0
        BinaryConnective pre = new BinaryConnective(ConnectiveType.OR, mul_eq_1, mul_eq_0);

        // (toggleAddMul == 1) ==> (result == a + b) && (toggleAddMul == 0) ==> (result == a * b)
        BinaryConnective post = make_q();


        // check pre and post created correctly
        assert_expected("( toggleAddMul == 1 ) OR ( toggleAddMul == 0 )", pre);
        assert_expected("( ( toggleAddMul == 1 ) ==> ( result == a + b ) )" + " AND " +
                        "( ( toggleAddMul == 0 ) ==> ( result == a * b ) )", post);


        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, pre, post));
    }
    @Test
    void ToggleCalculatorValidWithoutPre()
    {
        // (toggleAddMul == 1) ==> (result == a + b) && (toggleAddMul == 0) ==> (result == a * b)
        BinaryConnective post = make_q();


        // check pre and post created correctly
        assert_expected("( ( toggleAddMul == 1 ) ==> ( result == a + b ) )" + " AND " +
                        "( ( toggleAddMul == 0 ) ==> ( result == a * b ) )", post);


        Verifier verifier = new Verifier();
        assertTrue(verifier.verify(program, post));
    }


    // ====================================== invalid methods ======================================
    @Test
    void ToggleCalculatorInvalidWithPre()
    {
        CondCopyVisitor copier = new CondCopyVisitor();

        // toggleAddMul == -1
        VariableExpression mul = new VariableExpression("toggleAddMul");
        IntegerExpression min_1 = new IntegerExpression(-1);
        BinaryCondition pre = new BinaryCondition(ConditionType.EQUAL, mul, min_1);

        // (toggleAddMul == 1) ==> (result == a + b) && (toggleAddMul == 0) ==> (result == a * b)
        BinaryConnective post = make_q();
        // modify to (toggleAddMul != 1) && (toggleAddMul == 1) ==> (result == a + b) &&
        //           (toggleAddMul == 0) ==> (result == a * b)
        pre.accept(copier);
        UnaryConnective n_pre = new UnaryConnective(ConnectiveType.NOT, copier.result);
        post.accept(copier);
        post = new BinaryConnective(ConnectiveType.AND, n_pre, copier.result);


        // check pre and post created correctly
        assert_expected("toggleAddMul == -1", pre);
        assert_expected("( NOT( toggleAddMul == -1 ) ) AND " +
                        "( ( ( toggleAddMul == 1 ) ==> ( result == a + b ) )" + " AND " +
                        "( ( toggleAddMul == 0 ) ==> ( result == a * b ) ) )", post);


        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, pre, post));


        /* Test counterexamples */
        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertEquals(-1, map.get("toggleAddMul"));
    }
    @Test
    void ToggleCalculatorInvalidWithoutPre()
    {
        // a * b
        VariableExpression a = new VariableExpression("a");
        VariableExpression b = new VariableExpression("b");
        BinaryExpression ab = new BinaryExpression(ExpressionType.MUL, a, b);
        // result == a * b
        VariableExpression res = new VariableExpression("result");
        BinaryCondition post = new BinaryCondition(ConditionType.EQUAL, res, ab);


        // check pre and post created correctly
        assert_expected("result == a * b", post);


        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, post));


        /* Test counterexamples */
        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsKey("a"));
        assertEquals(1, map.get("toggleAddMul"));
    }


    // ======================================= wrong methods =======================================
    @Test
    void ToggleCalculatorInvalidValidSpec()
    {
        ExprCopyVisitor copier = new ExprCopyVisitor();

        // toggleAddMul == 2
        VariableExpression mul = new VariableExpression("toggleAddMul");
        IntegerExpression two = new IntegerExpression(2);
        BinaryCondition pre = new BinaryCondition(ConditionType.EQUAL, mul, two);
        // change result := a * b; to result := a * b + toggleAddMul;
        mul.accept(copier);
        VariableExpression b = new VariableExpression("b");
        BinaryExpression add_b_mul = new BinaryExpression(ExpressionType.ADD, b, copier.result);
        change_mul.right = add_b_mul;

        // check changed properly
		String expected = expectedSerializedProgram();
        expected = expected.replace("result := a * b", "result := a * ( b + toggleAddMul )");
		program.accept(visitor);
		assertEquals(expected, visitor.result);


        // a * b
        VariableExpression a = new VariableExpression("a");
        b.accept(copier);
        BinaryExpression ab = new BinaryExpression(ExpressionType.MUL, a, copier.result);
        // (result == a * b)
        VariableExpression res = new VariableExpression("result");
        BinaryCondition first = new BinaryCondition(ConditionType.EQUAL, res, ab);
        // a + b
        VariableExpression a1 = new VariableExpression("a");
        b.accept(copier);
        BinaryExpression a_plus_b = new BinaryExpression(ExpressionType.ADD, a1, copier.result);
        // (result == a + b)
        res.accept(copier);
        BinaryCondition second = new BinaryCondition(ConditionType.EQUAL, copier.result, a_plus_b);
        // (result == a * b) || (result == a + b)
        BinaryConnective post = new BinaryConnective(ConnectiveType.OR, first, second);


        // check pre and post created correctly
        assert_expected("toggleAddMul == 2", pre);
        assert_expected("( result == a * b ) OR ( result == a + b )", post);


        Verifier verifier = new Verifier();
        assertFalse(verifier.verify(program, pre, post));


        /* Test counterexamples */
        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsKey("a"));
        assertEquals(2, map.get("toggleAddMul"));
    }
}
