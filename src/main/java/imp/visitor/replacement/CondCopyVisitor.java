package imp.visitor.replacement;


import imp.condition.Boolean;
import imp.condition.BinaryCondition;
import imp.condition.BinaryConnective;
import imp.condition.Condition;
import imp.condition.UnaryConnective;
import imp.expression.Expression;
import imp.visitor.ConditionVisitor;


public class CondCopyVisitor extends ConditionVisitor
{
    public Condition result;


    @Override
    public void visit(Boolean c)
    { result = new Boolean(c.value); }

    @Override
    public void visit(BinaryCondition c)
    {
        ExprCopyVisitor copier = new ExprCopyVisitor();
        c.left.accept(copier);
        Expression new_left = copier.result;
        c.right.accept(copier);
        Expression new_right = copier.result;
        result = new BinaryCondition(c.type, new_left, new_right);
    }

    @Override
    public void visit(BinaryConnective c)
    {
        c.left.accept(this);
        Condition new_left = result;
        c.right.accept(this);
        Condition new_right = result;
        result = new BinaryConnective(c.type, new_left, new_right);
    }

    @Override
    public void visit(UnaryConnective c)
    {
        c.condition.accept(this);
        Condition new_cond = result;
        result = new UnaryConnective(c.type, new_cond);
    }
}
