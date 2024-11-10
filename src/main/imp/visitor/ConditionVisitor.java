package imp.visitor;


import imp.condition.BinaryCondition;
import imp.condition.BinaryConnective;
import imp.condition.Boolean;
import imp.condition.UnaryConnective;

abstract public class ConditionVisitor {
    public abstract void visit(Boolean c);
    public abstract void visit(BinaryCondition c);
    public abstract  void visit(BinaryConnective c);
    public abstract  void visit(UnaryConnective c);
}
