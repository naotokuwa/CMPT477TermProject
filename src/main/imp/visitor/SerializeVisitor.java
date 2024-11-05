package imp.visitor;

import imp.*;
import imp.condition.BinaryCondition;
import imp.condition.ConditionType;
import imp.condition.Boolean;
import imp.expression.BinaryExpression;
import imp.expression.ExpressionType;
import imp.expression.IntegerExpression;
import imp.expression.VariableExpression;
import imp.statement.Assignment;
import imp.statement.Composition;
import imp.statement.If;

import java.util.Map;

public final class SerializeVisitor extends GrammarVisitor {
    public String result;
    private int depth = 0;
    private final int INDENT = 2;

    private String addIndent(String s) {
        return " ".repeat(depth * INDENT) + s;
    }

    @Override
    public void visit(Grammar grammar) {
        throw new UnsupportedOperationException("This should not be called");
    }

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
        e.left.accept(this);
        String left = result;

        e.right.accept(this);
        String right = result;

        result = left + " " + typeToString.get(e.type) + " " + right;
    }

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

        c.left.accept(this);
        String left = result;

        c.right.accept(this);
        String right = result;

        result = left + " " + typeToString.get(c.type) + " " + right;
    }

    @Override
    public void visit(Assignment s) {
        s.v.accept(this);
        String left = result;

        s.e.accept(this);
        String right = result;

        result = addIndent(left + " := " + right);
    }

    @Override
    public void visit(Composition s) {
        s.before.accept(this);
        String before = result;

        s.after.accept(this);
        String after = result;

        result = addIndent(before) + "\n" + addIndent(after);
    }

    @Override
    public void visit(If s) {
        s.c.accept(this);
        String c = result;

        // Increment depth
        depth++;

        s.thenStatement.accept(this);
        String thenStatement = result;

        s.elseStatement.accept(this);
        String elseStatement = result;

        // Make depth back
        depth--;

        result = addIndent("if " + c + "\n");
        result += addIndent("then\n");
        result += thenStatement + "\n";
        result += addIndent("else\n");
        result += elseStatement;
    }
}

