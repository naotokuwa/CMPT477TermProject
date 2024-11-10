package imp.condition;

import imp.expression.Expression;
import imp.visitor.ConditionVisitor;

public final class BinaryCondition extends Condition {
    public final ConditionType type;
    public Expression left;
    public Expression right;

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
