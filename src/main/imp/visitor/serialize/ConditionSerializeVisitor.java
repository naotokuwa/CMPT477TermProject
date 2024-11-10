package imp.visitor.serialize;

import imp.condition.BinaryCondition;
import imp.condition.Boolean;
import imp.condition.ConditionType;
import imp.visitor.ConditionVisitor;

import java.util.Map;

final public class ConditionSerializeVisitor extends ConditionVisitor {
    public String result;

    @Override
    public void visit(Boolean c) {
        result = String.valueOf(c.value);
    }

    @Override
    public void visit(BinaryCondition c) {
        Map<ConditionType, String> typeToString = Map.of(
                ConditionType.EQUAL, "==",
                ConditionType.LE, "<="
        );

        ExpressionSerializeVisitor expressionSerializeVisitor = new ExpressionSerializeVisitor();

        c.left.accept(expressionSerializeVisitor);
        String left = expressionSerializeVisitor.result;

        c.right.accept(expressionSerializeVisitor);
        String right = expressionSerializeVisitor.result;

        result = left + " " + typeToString.get(c.type) + " " + right;
    }
}
