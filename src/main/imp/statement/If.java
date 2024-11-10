package imp.statement;

import imp.condition.Conditional;
import imp.visitor.StatementVisitor;

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
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}
