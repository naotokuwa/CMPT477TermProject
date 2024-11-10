package imp.expression;

import imp.visitor.ExpressionVisitor;

public abstract class Expression {
    public abstract void accept(ExpressionVisitor v);
}