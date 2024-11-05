package imp.expression;

import imp.visitor.GrammarVisitor;

public final class VariableExpression extends Expression {
    public final String symbol;

    public VariableExpression(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}
