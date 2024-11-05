package imp.condition;

import imp.visitor.GrammarVisitor;

public final class Boolean extends Conditional {
    public boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    @Override
    public void accept(GrammarVisitor visitor) {
        visitor.visit(this);
    }
}
