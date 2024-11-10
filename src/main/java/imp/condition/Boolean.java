package imp.condition;

import imp.visitor.ConditionVisitor;

public final class Boolean extends Condition {
    public final boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    @Override
    public void accept(ConditionVisitor visitor) {
        visitor.visit(this);
    }
}
