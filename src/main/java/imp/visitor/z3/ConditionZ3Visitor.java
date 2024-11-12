package imp.visitor.z3;

import com.microsoft.z3.*;
import imp.condition.*;
import imp.condition.Boolean;
import imp.visitor.ConditionVisitor;

/**
 * Creates a z3 object representing a Condition.
 */
public class ConditionZ3Visitor extends ConditionVisitor {
    private final Context ctx;
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

    @Override
    public void visit(Boolean c) {
        result = ctx.mkBool(c.value);
    }

    @Override
    public void visit(BinaryCondition c) {
        ExpressionZ3Visitor visitor = new ExpressionZ3Visitor(ctx);
        c.left.accept(visitor);
        ArithExpr left = visitor.result;
        c.right.accept(visitor);
        ArithExpr right = visitor.result;

        switch (c.type) {
            case EQUAL -> result = ctx.mkEq(left, right);
            case LE -> result = ctx.mkLe(left, right);
            default -> throw new UnsupportedOperationException("Binary condition type not implemented");
        }
    }

    @Override
    public void visit(BinaryConnective c) {
        c.left.accept(this);
        BoolExpr left = result;
        c.right.accept(this);
        BoolExpr right = result;

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

    /**
     * @return The validity of the last visited Condition
     */
    public boolean checkValidity() {
        return isValid(result);
    }

    private boolean isValid(BoolExpr formula) {
        Solver solver = ctx.mkSolver();
        BoolExpr negation = ctx.mkNot(formula);
        solver.add(negation);
        return solver.check() == Status.UNSATISFIABLE;
    }
}
