package imp.visitor.serialize;

import imp.condition.*;
import imp.condition.Boolean;
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

    @Override
    public void visit(BinaryConnective c) {
        Map<ConnectiveType, String> typeToString = Map.of(
                ConnectiveType.OR, "OR",
                ConnectiveType.AND, "AND",
                ConnectiveType.IMPLIES, "==>"
        );
        c.left.accept(this);
        String left = result;

        c.right.accept(this);
        String right = result;

        result = "( "+ left + " ) " + typeToString.get(c.type) + " ( " + right + " )";
    }

    @Override
    public void visit(UnaryConnective c) {
        Map<ConnectiveType, String> typeToString = Map.of(
                ConnectiveType.NOT, "NOT"
        );
        c.condition.accept(this);
        result = typeToString.get(c.type) + "( " + result + " )";
    }
}
