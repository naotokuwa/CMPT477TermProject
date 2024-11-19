package verifier;

import imp.condition.*;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.statement.Assignment;
import imp.statement.Statement;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VerifierTest {
    @Test
    public void CounterexampleBeforeVerifyThrows() {
        Verifier verifier = new Verifier();

        // Program: x := 0
        Statement program = new Assignment(new VariableExpression("x"), new IntegerExpression(0));

        // Postcondition: NOT( x == 0 )
        Condition postcondition = new UnaryConnective(ConnectiveType.NOT,
                                      new BinaryCondition(ConditionType.EQUAL,
                                          new VariableExpression("x"),
                                          new IntegerExpression(0)));

        // Getting a counterexample before calling verify() should throw an exception
        assertThrows(CounterexampleCallOrderException.class, verifier::getCounterexampleMap);
        assertThrows(CounterexampleCallOrderException.class, verifier::getCounterexampleString);
        assertThrows(CounterexampleCallOrderException.class, verifier::getCounterexampleRaw);

        boolean isValid = verifier.verify(program, postcondition);
        assertFalse(isValid);

        // Getting a counterexample after calling verify() should not throw
        Map<String, Integer> map = verifier.getCounterexampleMap();
        assertEquals(new HashMap<>(), map);
        String basicString = verifier.getCounterexampleString();
        assertEquals("", basicString);
        String z3String = verifier.getCounterexampleRaw();
        assertEquals("", z3String);
    }
}
