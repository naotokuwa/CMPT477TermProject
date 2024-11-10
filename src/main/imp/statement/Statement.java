package imp.statement;

import imp.visitor.StatementVisitor;

public abstract class Statement {
    public abstract void accept(StatementVisitor visitor);
}