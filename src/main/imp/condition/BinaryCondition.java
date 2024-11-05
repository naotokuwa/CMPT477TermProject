package imp.condition;

import imp.expression.Expression;
import imp.visitor.GrammarVisitor;

public final class BinaryCondition extends Conditional {
    public final ConditionType type;
    public final Expression left;
    public final Expression right;

    public BinaryCondition(ConditionType type, Expression left, Expression right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}
