package imp.visitor.serialize;

import imp.expression.BinaryExpression;
import imp.expression.ExpressionType;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.visitor.ExpressionVisitor;

import java.util.Map;

final public class ExpressionSerializeVisitor extends ExpressionVisitor  {
    public String result;
    // Depth for nested BinaryCondition
    private int depth = 0;

    @Override
    public void visit(IntegerExpression e) {
        result = String.valueOf(e.integer);
    }

    @Override
    public void visit(VariableExpression e) {
        result = e.symbol;
    }

    @Override
    public void visit(BinaryExpression e) {
        Map<ExpressionType, String> typeToString = Map.of(
                ExpressionType.ADD, "+",
                ExpressionType.MUL, "*"
        );

        depth++;
        e.left.accept(this);
        String left = result;

        e.right.accept(this);
        String right = result;
        depth--;

        result = left + " " + typeToString.get(e.type) + " " + right;

        if (depth > 0){
            result  = "( " + result + " )";
        }
    }
}
