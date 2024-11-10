package imp.condition;

import imp.visitor.ConditionVisitor;

public abstract class Condition {
    public abstract void accept(ConditionVisitor visitor);
}