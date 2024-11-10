package imp.condition;

import imp.visitor.ConditionVisitor;

public class UnaryConnective extends Condition {
    public ConnectiveType type;
    public Condition condition;

    public UnaryConnective(ConnectiveType type, Condition condition){
        this.type = type;
        this.condition = condition;
    }

    @Override
    public void accept(ConditionVisitor visitor){
        visitor.visit(this);
    }
}
