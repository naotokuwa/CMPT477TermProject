package imp;

abstract public class GrammarVisitor {
    abstract void visit(Grammar grammar);

    // Expression
    abstract void visit(IntegerExpression e);

    abstract void visit(VariableExpression e);

    abstract void visit(AdditionExpression e);

    abstract void visit(MultiplyExpression e);

    // Conditional
    abstract void visit(True t);

    abstract void visit(False t);

    abstract void visit(Equal t);

    abstract void visit(Le t);

    // Statement
    abstract void visit(Assignment s);

    abstract void visit(Composition s);

    abstract void visit(If s);
}
