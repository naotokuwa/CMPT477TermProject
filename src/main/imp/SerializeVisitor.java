package imp;

public final class SerializeVisitor extends GrammarVisitor {
    public String result;
    public int depth = 0;
    public final int INDENT = 2;

    private String addIndent(String s) {
        return " ".repeat(depth * INDENT) + s;
    }

    @Override
    void visit(Grammar grammar) {
        throw new UnsupportedOperationException("This should not be called");
    }

    @Override
    void visit(IntegerExpression e) {
        result = String.valueOf(e.integer);
    }

    @Override
    void visit(VariableExpression e) {
        result = e.symbol;
    }

    @Override
    void visit(AdditionExpression e) {
        e.left.accept(this);
        String left = result;

        e.right.accept(this);
        String right = result;

        result = left + " + " + right;
    }

    @Override
    void visit(MultiplyExpression e) {
        e.left.accept(this);
        String left = result;

        e.right.accept(this);
        String right = result;

        result = left + " * " + right;
    }

    @Override
    void visit(True t) {
        result = "true";
    }

    @Override
    void visit(False t) {
        result = "false";
    }

    @Override
    void visit(Equal c) {
        c.left.accept(this);
        String left = result;

        c.right.accept(this);
        String right = result;

        result = left + " == " + right;
    }

    @Override
    void visit(Le c) {
        c.left.accept(this);
        String left = result;

        c.right.accept(this);
        String right = result;

        result = left + " <= " + right;
    }

    @Override
    void visit(Assignment s) {
        s.v.accept(this);
        String left = result;

        s.e.accept(this);
        String right = result;

        result = addIndent(left + " := " + right);
    }

    @Override
    void visit(Composition s) {
        s.before.accept(this);
        String before = result;

        s.after.accept(this);
        String after = result;

        result = addIndent(before) + "\n" + addIndent(after);
    }

    @Override
    void visit(If s) {
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

