package imp.condition;

import imp.expression.Expression;
import imp.visitor.ConditionVisitor;

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
    public void accept(ConditionVisitor visitor) {
        visitor.visit(this);
    }
}
