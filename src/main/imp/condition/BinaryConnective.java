package imp.condition;

import imp.visitor.ConditionVisitor;

final public class BinaryConnective extends Condition {
    public final ConnectiveType type;
    public Condition left;
    public Condition right;

    public BinaryConnective(ConnectiveType type, Condition left, Condition right){
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(ConditionVisitor visitor){
        visitor.visit(this);
    }
}
