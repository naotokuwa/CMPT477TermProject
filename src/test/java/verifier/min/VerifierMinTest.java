package verifier.min;

import imp.expression.*;
import imp.statement.*;
import imp.condition.*;
import imp.visitor.serialize.StatementSerializeVisitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifierMinTest {
    private StatementSerializeVisitor visitor;
    private Statement program;

    private String expectedSerializedProgram(){
        String expected = "minVal := x\n";
        expected += "if y <= minVal\n";
        expected += "then\n";
        expected += "  minVal := y\n";
        expected += "else\n";
        expected += "  tmp := 1";

        return expected;
    }

    private Statement createMeaningLessStatement(){
        VariableExpression tmp = new VariableExpression("tmp");
        Expression one  = new IntegerExpression(1);
        Assignment statement = new Assignment(tmp, one);
        return statement;
    }

    private Statement createProgram(){
        // First line
        VariableExpression minVal1 = new VariableExpression("minVal");
        Expression x1 = new VariableExpression("x");
        Statement firstLine = new Assignment(minVal1, x1);
        
        // If condition
        Expression y1  = new VariableExpression("y");
        Expression minVal2 = new VariableExpression("minVal");
        Condition condition = new BinaryCondition(ConditionType.LE, y1, minVal2);

        // Then block
        VariableExpression minVal3 = new VariableExpression("minVal");
        Expression y2  = new VariableExpression("y");
        Assignment thenBlock = new Assignment(minVal3, y2);

        // Else block
        // We don't need to use a else block as you can tell in Dafny implementation.
        // So, just make a meaningless program which does not affect the semantic
        // of program and put it to else branch
        Statement elseBlock = createMeaningLessStatement();

        // If statement
        Statement ifStatement = new If(condition, thenBlock, elseBlock);
        Statement program = new Composition(firstLine, ifStatement);

        // Verify program serialization
        program.accept(visitor);
        String result = visitor.result;
        String expected = expectedSerializedProgram();

        assertEquals(expected, result);

        return program;
    }

    @BeforeEach public void setUp() {
        visitor = new StatementSerializeVisitor();
        this.program = createProgram();
    }
    
    @Test
    void MinValid1(){
        System.out.println("TO BE IMPLEMENTED");
    }
}