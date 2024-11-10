package imp.condition;

import imp.visitor.ConditionVisitor;

public abstract class Conditional {
    public abstract void accept(ConditionVisitor visitor);
}