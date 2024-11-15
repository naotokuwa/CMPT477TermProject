package imp.visitor.replacement;

import imp.expression.BinaryExpression;
import imp.expression.Expression;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.visitor.ExpressionVisitor;

public class ExpressionReplacementVisitor extends ExpressionVisitor {
    private final String targetSymbol;
    private final Expression toReplace;
    public Expression result;

    public ExpressionReplacementVisitor(String targetSymbol, Expression toReplace){
        this.targetSymbol = targetSymbol;
        this.toReplace = toReplace;
    }

    @Override
    public void visit(IntegerExpression e) {
        result = e;
    }

    @Override
    public void visit(VariableExpression e) {
        result = e;

        if (targetSymbol.equals(e.symbol)){
            ExprCopyVisitor copier = new ExprCopyVisitor();
            toReplace.accept(copier);
            result = copier.result;
        }
    }

    @Override
    public void visit(BinaryExpression e) {
        e.left.accept(this);
        e.left = result;

        e.right.accept(this);
        e.right = result;

        result = e;
    }
}
