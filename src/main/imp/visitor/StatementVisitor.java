package imp.visitor;

import imp.statement.Assignment;
import imp.statement.Composition;
import imp.statement.If;

abstract public class StatementVisitor {
    public abstract void visit(Assignment s);
    public abstract void visit(Composition s);
    public abstract void visit(If s);
}
