package imp.statement;

import imp.visitor.StatementVisitor;

public final class Composition extends Statement {
    public final Statement before;
    public final Statement after;

    public Composition(Statement before, Statement after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}