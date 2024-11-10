package imp.condition;

import imp.visitor.ConditionVisitor;

public final class Boolean extends Conditional {
    public boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    @Override
    public void accept(ConditionVisitor visitor) {
        visitor.visit(this);
    }
}
