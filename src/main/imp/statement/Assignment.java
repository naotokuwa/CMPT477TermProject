package imp.statement;

import imp.expression.Expression;
import imp.expression.VariableExpression;
import imp.visitor.GrammarVisitor;

public final class Assignment extends Statement {
    public final VariableExpression v;
    public final Expression e;

    public Assignment(VariableExpression v, Expression e) {
        this.v = v;
        this.e = e;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}
