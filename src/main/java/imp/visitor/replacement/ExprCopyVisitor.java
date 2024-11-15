package imp.visitor.replacement;

import imp.expression.BinaryExpression;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.expression.Expression;
import imp.visitor.ExpressionVisitor;


public class ExprCopyVisitor extends ExpressionVisitor
{
    public Expression result;

    @Override
    public void visit(IntegerExpression e)
    { result = new IntegerExpression(e.integer); }

    @Override
    public void visit(VariableExpression e)
    { result = new VariableExpression(e.symbol); }

    @Override
    public void visit(BinaryExpression e)
    {
        // deep copy
        e.left.accept(this);
        Expression left_res = result;
        e.right.accept(this);
        Expression right_res = result;
        result = new BinaryExpression(e.type, left_res, right_res);
    }
}
