package imp.visitor;


import imp.condition.BinaryCondition;
import imp.condition.Boolean;

abstract public class ConditionVisitor {
    public abstract void visit(Boolean c);
    public abstract void visit(BinaryCondition c);
}
