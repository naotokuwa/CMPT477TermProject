package imp;

abstract class Expression extends Grammar {}


final class IntegerExpression extends Expression {
    public final int integer;

    IntegerExpression(int integer) {
        this.integer = integer;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}

final class VariableExpression extends Expression {
    public final String symbol;

    VariableExpression(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}

final class AdditionExpression extends Expression {
    public final Expression left;
    public final Expression right;

    AdditionExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}

final class MultiplyExpression extends Expression {
    public final Expression left;
    public final Expression right;

    MultiplyExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}