package imp.condition;

import imp.visitor.ConditionVisitor;

public class UnaryConnective extends Conditional {
    public ConnectiveType type;
    public Conditional condition;

    public UnaryConnective(ConnectiveType type, Conditional condition){
        this.type = type;
        this.condition = condition;
    }

    @Override
    public void accept(ConditionVisitor visitor){
        visitor.visit(this);
    }
}
