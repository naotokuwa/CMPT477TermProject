package imp.visitor;

import imp.expression.BinaryExpression;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;

abstract public class ExpressionVisitor{
    public abstract void visit(IntegerExpression e);
    public abstract void visit(VariableExpression e);
    public abstract void visit(BinaryExpression e);
}
