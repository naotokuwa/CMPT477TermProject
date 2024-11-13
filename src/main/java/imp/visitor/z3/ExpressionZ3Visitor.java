package imp.visitor.z3;

import com.microsoft.z3.*;
import imp.expression.BinaryExpression;
import imp.expression.Expression;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.visitor.ExpressionVisitor;

/**
 * Creates a z3 object representing an Expression.
 */
public class ExpressionZ3Visitor extends ExpressionVisitor {
    private final Context ctx;
    public ArithExpr result;

    /**
     * @param context z3 context to use for generating objects
     */
    public ExpressionZ3Visitor(Context context) {
        ctx = context;
    }

    /**
     * A simple convenience method for visiting an expression and returning the result.
     * @param e Expression to visit.
     * @return Resulting z3 object.
     */
    public ArithExpr getResult(Expression e) {
        e.accept(this);
        return result;
    }

    public void visit(IntegerExpression e) {
        result = ctx.mkInt(e.integer);
    }

    public void visit(VariableExpression e) {
        result = ctx.mkIntConst(e.symbol);
    }

    public void visit(BinaryExpression e) {
        ArithExpr left = this.getResult(e.left);
        ArithExpr right = this.getResult(e.right);

        switch (e.type) {
            case ADD -> result = ctx.mkAdd(left, right);
            case MUL -> result = ctx.mkMul(left, right);
            default -> throw new UnsupportedOperationException("Binary expression type not implemented");
        }
    }
}
