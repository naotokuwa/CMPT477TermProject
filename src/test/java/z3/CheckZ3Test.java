package z3;

import com.microsoft.z3.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckZ3Test {
    @Test
    public void testCounterExampleNotGiven(){
        Context ctx = new Context();

        // {(true -> false -> (x == x + 1) AND true -> (x + 2 == x + 1)) AND (false -> (x + 1 == x + 1) AND true -> (x + 1 + 2 == x + 1))}
        ArithExpr x = ctx.mkIntConst("x");

        // Construct the first part: (false -> x == x + 1)
        BoolExpr falseImpliesXEqXPlus1 = ctx.mkImplies(ctx.mkFalse(), ctx.mkEq(x, ctx.mkAdd(x, ctx.mkInt(1))));

        // (true -> x + 2 == x + 1)
        BoolExpr trueImpliesXPlus2EqXPlus1 = ctx.mkImplies(ctx.mkTrue(), ctx.mkEq(ctx.mkAdd(x, ctx.mkInt(2)), ctx.mkAdd(x, ctx.mkInt(1))));

        // Combine the first set with AND
        BoolExpr firstAnd = ctx.mkAnd(falseImpliesXEqXPlus1, trueImpliesXPlus2EqXPlus1);

        // (false -> x + 1 == x + 1)
        BoolExpr falseImpliesXPlus1EqXPlus1 = ctx.mkImplies(ctx.mkFalse(), ctx.mkEq(ctx.mkAdd(x, ctx.mkInt(1)), ctx.mkAdd(x, ctx.mkInt(1))));

        // (true -> x + 1 + 2 == x + 1)
        BoolExpr trueImpliesXPlus1Plus2EqXPlus1 = ctx.mkImplies(
                ctx.mkTrue(),
                ctx.mkEq(
                        ctx.mkAdd(x, ctx.mkInt(1), ctx.mkInt(2)),
                        ctx.mkAdd(x, ctx.mkInt(1))
                )
        );

        // Combine the second set with AND
        BoolExpr secondAnd = ctx.mkAnd(falseImpliesXPlus1EqXPlus1, trueImpliesXPlus1Plus2EqXPlus1);

        // Combine both main parts with AND
        BoolExpr wp = ctx.mkAnd(firstAnd, secondAnd);
        BoolExpr condition = ctx.mkImplies(ctx.mkTrue(), wp);

        BoolExpr toProve = ctx.mkNot(condition);
        Solver solver = ctx.mkSolver();
        solver.add(toProve);
        assertEquals(solver.check(), Status.SATISFIABLE);
        Model model = solver.getModel();
        System.out.println(model.toString());
    }
}
