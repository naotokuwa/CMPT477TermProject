package imp.visitor.logic;

import imp.condition.BinaryConnective;
import imp.condition.Boolean;
import imp.condition.Condition;
import imp.condition.ConnectiveType;
import imp.condition.UnaryConnective;
import imp.statement.Assignment;
import imp.statement.Composition;
import imp.statement.If;
import imp.visitor.StatementVisitor;
import imp.visitor.replacement.CondCopyVisitor;
import imp.visitor.replacement.ConditionReplacementVisitor;


public class LogicVisitor extends StatementVisitor
{
    public Condition result;


    // saves copy of q to avoid mutating original obj
    public LogicVisitor(Condition q)
    {
        CondCopyVisitor copier = new CondCopyVisitor();
        q.accept(copier);
        this.result = copier.result;
    }
    public LogicVisitor() { }


    @Override
    public void visit(Assignment s)
    {
        if(result == null)
        {
            result = new Boolean(true);
            return;
        }

        ConditionReplacementVisitor replacer = new ConditionReplacementVisitor(s.v.symbol, s.e);
        result.accept(replacer); // mutates result
    }

    @Override
    public void visit(Composition s)
    {
        if(result == null)
        {
            result = new Boolean(true);
            return;
        }

        s.after.accept(this);
        // q = wp2
        s.before.accept(this);
    }

    @Override
    public void visit(If s)
    {
        if(result == null)
        {
            result = new Boolean(true);
            return;
        }

        // save q
        Condition orig;
        CondCopyVisitor copier = new CondCopyVisitor();
        result.accept(copier);
        orig = copier.result;

        CondCopyVisitor cond_copier = new CondCopyVisitor();

        // true branch C -> wp(s1, Q)
        s.c.accept(cond_copier);
        Condition cond = cond_copier.result;

        s.thenStatement.accept(this);
        result.accept(copier);
        Condition true_wp = copier.result;
        BinaryConnective true_b = new BinaryConnective(ConnectiveType.IMPLIES, cond, true_wp);

        result = orig;

        // false branch !C -> wp(s2, Q)
        s.c.accept(cond_copier);
        Condition not_cond = new UnaryConnective(ConnectiveType.NOT, cond_copier.result);

        s.elseStatement.accept(this);
        result.accept(copier);
        Condition false_wp = copier.result;
        BinaryConnective false_b = new BinaryConnective(ConnectiveType.IMPLIES, not_cond, false_wp);


        // C -> wp(s1, Q) && !C -> wp(s2, Q)
        result = new BinaryConnective(ConnectiveType.AND, true_b, false_b);
    }
}
