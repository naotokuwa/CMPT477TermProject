package verifier;

import com.microsoft.z3.Context;
import imp.condition.BinaryConnective;
import imp.condition.Boolean;
import imp.condition.Condition;
import imp.condition.ConnectiveType;
import imp.statement.Statement;
import imp.visitor.logic.LogicVisitor;
import imp.visitor.z3.ConditionZ3Visitor;

import java.util.Map;

public class Verifier {
    private Condition validityCondition;
    private final ConditionZ3Visitor z3Visitor;

    public Verifier() {
        Context ctx = new Context();
        z3Visitor = new ConditionZ3Visitor(ctx);
    }

    /**
     * Verifies a program with a precondition and postcondition
     * @param program the program to verify
     * @param precondition a precondition
     * @param postcondition a postcondition
     * @return true if the program is valid
     */
    public boolean verify(Statement program, Condition precondition, Condition postcondition) {
        LogicVisitor visitor = new LogicVisitor(postcondition);
        program.accept(visitor);
        Condition weakestPre = visitor.result;
        validityCondition = new BinaryConnective(ConnectiveType.IMPLIES, precondition, weakestPre);

        return z3Visitor.checkValidity(validityCondition);
    }

    /**
     * Verifies a program with no precondition
     * @param program the program to verify
     * @param postcondition a postcondition
     * @return true if the program is valid
     */
    public boolean verify(Statement program, Condition postcondition) {
        Condition precondition = new Boolean(true);
        return verify(program, precondition, postcondition);
    }

    /**
     * @return a counterexample as a map from symbols to values
     */
    public Map<String, Integer> getCounterexampleMap() {
        return z3Visitor.getCounterexampleAsMap(validityCondition);
    }

    /**
     * @return a counterexample as a simple string
     * ex. x = 0
     *     y = -1
     */
    public String getCounterexampleString() {
        return z3Visitor.getCounterexampleAsString(validityCondition);
    }

    /**
     * @return a counterexample as a z3 string
     *  ex. (define-fun x () Int
     *       0)
     *      (define-fun y () Int
     *       (- 1))
     */
    public String getCounterexampleRaw() {
        return z3Visitor.getCounterexampleRaw(validityCondition);
    }
}
