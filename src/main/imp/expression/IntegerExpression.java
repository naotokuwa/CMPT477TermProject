package imp.expression;

import imp.visitor.ExpressionVisitor;

public final class IntegerExpression extends Expression {
    public final int integer;

    public IntegerExpression(int integer) {
        this.integer = integer;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
