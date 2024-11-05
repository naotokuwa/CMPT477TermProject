package imp.statement;

import imp.condition.Conditional;
import imp.visitor.GrammarVisitor;

public final class If extends Statement {
    public final Conditional c;
    public final Statement thenStatement;
    public final Statement elseStatement;

    public If(Conditional c, Statement thenStatement, Statement elseStatement) {
        this.c = c;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}
