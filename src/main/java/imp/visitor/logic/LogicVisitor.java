package imp.visitor.logic;

import imp.condition.BinaryConnective;
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
    public Condition q;
    public Condition result;


    public LogicVisitor(Condition q) { this.q = q; }

    @Override
    public void visit(Assignment s)
    {
        // save original q bc replacer mutates it
        Condition orig = q;
        CondCopyVisitor copier = new CondCopyVisitor();
        q.accept(copier);
        q = copier.result;

        ConditionReplacementVisitor replacer = new ConditionReplacementVisitor(s.v.symbol, s.e);
        q.accept(replacer);

        q = orig;
        result = replacer.result;
    }

    @Override
    public void visit(Composition s)
    {
        s.after.accept(this);
        Condition wp2 = result;

        // save q and set q = wp2
        Condition orig = q;
        CondCopyVisitor copier = new CondCopyVisitor();
        wp2.accept(copier);
        q = copier.result;

        s.before.accept(this);

        // restore q
        q = orig;
    }

    @Override
    public void visit(If s)
    {
        CondCopyVisitor cond_copier = new CondCopyVisitor();

        // true branch C -> wp(s1, Q)
        s.c.accept(cond_copier);
        Condition cond = cond_copier.result;

        s.thenStatement.accept(this);
        Condition true_wp = result;
        BinaryConnective true_b = new BinaryConnective(ConnectiveType.IMPLIES, cond, true_wp);


        // false branch !C -> wp(s2, Q)
        s.c.accept(cond_copier);
        Condition not_cond = new UnaryConnective(ConnectiveType.NOT, cond_copier.result);

        s.elseStatement.accept(this);
        Condition false_wp = result;
        BinaryConnective false_b = new BinaryConnective(ConnectiveType.IMPLIES, not_cond, false_wp);


        // C -> wp(s1, Q) && !C -> wp(s2, Q)
        result = new BinaryConnective(ConnectiveType.AND, true_b, false_b);
    }
}
