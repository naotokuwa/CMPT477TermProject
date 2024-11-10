package imp.visitor.serialize;

import imp.statement.Assignment;
import imp.statement.Composition;
import imp.statement.If;
import imp.visitor.StatementVisitor;

final public class StatementSerializeVisitor extends StatementVisitor {
    public String result;
    private int depth = 0;
    private final int INDENT = 2;

    private String addIndent(String s) {
        return " ".repeat(depth * INDENT) + s;
    }

    @Override
    public void visit(Assignment s) {
        ExpressionSerializeVisitor expressionSerializeVisitor = new ExpressionSerializeVisitor();
        s.v.accept(expressionSerializeVisitor);
        String left = expressionSerializeVisitor.result;

        s.e.accept(expressionSerializeVisitor);
        String right = expressionSerializeVisitor.result;

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
        ConditionSerializeVisitor conditionSerializeVisitor = new ConditionSerializeVisitor();
        s.c.accept(conditionSerializeVisitor);
        String c = conditionSerializeVisitor.result;

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
