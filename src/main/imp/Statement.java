package imp;

abstract public class Statement extends Grammar {}

final class Assignment extends Statement {
    public final VariableExpression v;
    public final Expression e;

    Assignment(VariableExpression v, Expression e) {
        this.v = v;
        this.e = e;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}

final class Composition extends Statement {
    public final Statement before;
    public final Statement after;

    Composition(Statement before, Statement after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}

final class If extends Statement {
    public final Conditional c;
    public final Statement thenStatement;
    public final Statement elseStatement;

    If(Conditional c, Statement thenStatement, Statement elseStatement) {
        this.c = c;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}