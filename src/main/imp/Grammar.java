package imp;

import imp.visitor.GrammarVisitor;

public abstract class Grammar {
    abstract public void accept(GrammarVisitor visitor);
}