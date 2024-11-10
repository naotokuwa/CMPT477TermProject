package imp.visitor.replacement;

import imp.condition.*;
import imp.condition.Boolean;
import imp.expression.Expression;
import imp.visitor.ConditionVisitor;

public class ConditionReplacementVisitor extends ConditionVisitor {
    public Condition result;
    private final String targetSymbol;
    private final Expression toReplace;

    public ConditionReplacementVisitor(String targetSymbol, Expression toReplace){
        this.targetSymbol = targetSymbol;
        this.toReplace = toReplace;
    }

    @Override
    public void visit(Boolean c) {
        result = c;
    }

    @Override
    public void visit(BinaryCondition c) {
        ExpressionReplacementVisitor visitor = new ExpressionReplacementVisitor(targetSymbol, toReplace);
        c.left.accept(visitor);
        c.left = visitor.result;

        c.right.accept(visitor);
        c.right = visitor.result;

        result = c;
    }

    @Override
    public void visit(BinaryConnective c) {
        c.left.accept(this);
        c.left = result;

        c.right.accept(this);
        c.right = result;

        result = c;
    }

    @Override
    public void visit(UnaryConnective c) {
        c.condition.accept(this);
        c.condition = result;
        result = c;
    }
}
