package imp;

abstract public class Conditional extends Grammar {}

final class True extends Conditional {
    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}

final class False extends Conditional {
    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}

final class Equal extends Conditional {
    public final Expression left;
    public final Expression right;

    Equal(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}

final class Le extends Conditional {
    public final Expression left;
    public final Expression right;

    Le(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}