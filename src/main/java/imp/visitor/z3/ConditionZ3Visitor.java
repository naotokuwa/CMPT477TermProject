package imp.visitor.z3;

import com.microsoft.z3.*;
import imp.condition.*;
import imp.condition.Boolean;
import imp.visitor.ConditionVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates a z3 object representing a Condition.
 */
public class ConditionZ3Visitor extends ConditionVisitor {
    private final Context ctx;
    private Condition oldCondition;
    private String cachedModel;
    public BoolExpr result;

    /**
     * @param context z3 context to use for generating objects
     */
    public ConditionZ3Visitor(Context context) {
        ctx = context;
    }

    /**
     * A simple convenience method for visiting a condition and returning the result.
     * @param c Condition to visit.
     * @return Resulting z3 object.
     */
    public BoolExpr getResult(Condition c) {
        c.accept(this);
        return result;
    }

    /**
     * @param condition The condition to check
     * @return The validity of the given condition
     */
    public boolean checkValidity(Condition condition) {
        condition.accept(this);
        return isValid(result);
    }

    /**
     * @param condition The condition used to generate the counterexample
     * @return A string representation of a counterexample if the condition
     * is invalid, otherwise an empty string
     */
    public String getCounterexampleAsString(Condition condition) {
        String z3Output = getCounterexampleRaw(condition);

        if (z3Output.equals("")) {
            return "";
        }

        String regex = "\\(define-fun\\s+(\\w+).*\n\\s+[(]?([\\w\\s-]+)[)]?\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(z3Output);
        StringBuilder counterExample = new StringBuilder("Counterexample:\n");
        while (matcher.find()) {
            String symbol = matcher.group(1);
            String value = matcher.group(2).replaceAll("\\s", "");
            counterExample.append(symbol).append(" = ").append(value).append("\n");
        }

        return counterExample.toString();
    }

    /**
     * @param condition The condition used to generate the counterexample
     * @return A map representation of a counterexample if the condition
     * is invalid, otherwise an empty map
     */
    public Map<String, Integer> getCounterexampleAsMap(Condition condition) {
        String z3Output = getCounterexampleAsString(condition);
        if (z3Output.equals("")) {
            return new HashMap<>();
        }
        return z3StringToMap(z3Output);
    }

    public String getCounterexampleRaw(Condition condition) {
        // Cache to avoid having to repeatedly invoke z3 and the visitor upon
        // repeated calls to getCounterexample methods
        if (condition == oldCondition) {
            return cachedModel;
        } else {
            oldCondition = condition;
        }

        Solver solver = ctx.mkSolver();
        condition.accept(this);
        BoolExpr negation = ctx.mkNot(result);
        solver.add(negation);
        Status status = solver.check();

        if (status == Status.UNSATISFIABLE) {
            return "";
        }

        cachedModel = solver.getModel().toString();
        return cachedModel;
    }

    @Override
    public void visit(Boolean c) {
        result = ctx.mkBool(c.value);
    }

    @Override
    public void visit(BinaryCondition c) {
        ExpressionZ3Visitor visitor = new ExpressionZ3Visitor(ctx);
        ArithExpr left = visitor.getResult(c.left);
        ArithExpr right = visitor.getResult(c.right);

        switch (c.type) {
            case EQUAL -> result = ctx.mkEq(left, right);
            case LE -> result = ctx.mkLe(left, right);
            default -> throw new UnsupportedOperationException("Binary condition type not implemented");
        }
    }

    @Override
    public void visit(BinaryConnective c) {
        BoolExpr left = getResult(c.left);
        BoolExpr right = getResult(c.right);

        switch (c.type) {
            case AND -> result = ctx.mkAnd(left, right);
            case OR -> result = ctx.mkOr(left, right);
            case IMPLIES -> result = ctx.mkImplies(left, right);
            default -> throw new UnsupportedOperationException("Binary connective type not implemented");
        }
    }

    @Override
    public void visit(UnaryConnective c) {
        c.condition.accept(this);

        switch (c.type) {
            case NOT -> result = ctx.mkNot(result);
            default -> throw new UnsupportedOperationException("Unary connective type not implemented");
        }
    }

    private boolean isValid(BoolExpr formula) {
        Solver solver = ctx.mkSolver();
        BoolExpr negation = ctx.mkNot(formula);
        solver.add(negation);
        return solver.check() == Status.UNSATISFIABLE;
    }

    /**
     * @param input The string representation given by getCounterexampleAsString()
     * @return A map from symbols to values
     */
    private Map<String, Integer> z3StringToMap(String input) {
        Map<String, Integer> symbolToVal = new HashMap<>();
        Scanner scanner = new Scanner(input);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split("=");
            if (tokens.length == 2) {
                String symbol = tokens[0].trim();
                int val = Integer.parseInt(tokens[1].trim());
                symbolToVal.put(symbol, val);
            }
        }
        return symbolToVal;
    }
}
