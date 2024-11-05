package imp.expression;

import imp.visitor.GrammarVisitor;

public final class IntegerExpression extends Expression {
    public final int integer;

    public IntegerExpression(int integer) {
        this.integer = integer;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}
