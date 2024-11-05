package imp.visitor;

import imp.*;
import imp.condition.BinaryCondition;
import imp.condition.Boolean;
import imp.expression.BinaryExpression;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.statement.Assignment;
import imp.statement.Composition;
import imp.statement.If;

abstract public class GrammarVisitor {
    public abstract void visit(Grammar grammar);

    // Expression
    public abstract void visit(IntegerExpression e);

    public abstract void visit(VariableExpression e);

    public abstract void visit(BinaryExpression e);

    // Conditional
    public abstract void visit(Boolean c);

    public abstract void visit(BinaryCondition c);

    // Statement
    public abstract void visit(Assignment s);

    public abstract void visit(Composition s);

    public abstract void visit(If s);
}
