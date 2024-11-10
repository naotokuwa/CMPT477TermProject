package imp.condition;

import imp.visitor.ConditionVisitor;

final public class BinaryConnective extends  Conditional {
    public Conditional left;
    public Conditional right;
    public ConnectiveType type;

    public BinaryConnective(ConnectiveType type, Conditional left, Conditional right){
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(ConditionVisitor visitor){
        visitor.visit(this);
    }
}
