package imp.expression;

import imp.visitor.ExpressionVisitor;

public final class VariableExpression extends Expression {
    public final String symbol;
    public VariableExpression(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
