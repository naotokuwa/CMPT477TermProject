package imp.expression;

import imp.visitor.ExpressionVisitor;

public final class BinaryExpression extends Expression {
    public final ExpressionType type;
    public final Expression left;
    public final Expression right;

    public BinaryExpression(ExpressionType type, Expression left, Expression right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
