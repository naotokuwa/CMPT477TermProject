# CMPT477TermProject
A formal program verification tool for the simplified (loop-free) IMP (Imperative Programming)
language leveraging Z3 for SMT formula validation.

### Authors:
- Kuwayama, Naoto
- Lucas, Joel
- Ly, Raymond
- Wang, Andy

## IMP Grammar
Our verifier accepts loop-free IMP program according to the following grammar:
```
Expression E ::= Z | V | E + E | E × E
Condition C ::= true | false | E = E | E ≤ E
Statement S ::= V := E | S; S | if C then S else S
Integers Z
Variables V
```

## Logic Grammar
Pre/post conditions follow the grammar:
```
Expression E ::= Z | V | E + E | E x E
Condition C ::= true | false | E = E | E <= E | C AND C | C OR C | C IMPLIES C | NOT C
Integers Z
Variables V
```


## Basic Example
Given the following program with no precondition the the postcondition `0 <= y`:
```
{true}
if x <= 0 y := x * -1 else y := x
{0 <= y}
```
The program can be verified through the `Verifier` class:
```
// Create a Verifier
Verifier = new Verifier();

// Assuming we have the root of an AST representing the program to verify. NOTE: Unfortunately since
there is no parser, the AST must be constructed manually. See accompanying tests for examples of how
to construct an AST by hand.
Statement program = someManuallyCreatedAST();

// Pre/post conditions must be created manually as well
// Postcondition: 0 <= y
Condition postcondition = new BinaryCondition(ConditionType.LE,
                          new IntegerExpression(0),
                          new VariableExpression("y"));

// Verify the program according to the postcondition
verifier.verify(program, postcondition);    // returns true

// Preconditions are optional
// Precondition: NOT( x == x )
Condition precondition = new UnaryConnective(ConnectiveType.NOT,
                              new BinaryCondition(ConditionType.LE,
                                  new IntegerExpression(0),
                                  new VariableExpression("y")
                              ));

verifier.verify(program, precondition, postcondition);    // returns false

// After verifying a program, a counterexample can be generated for invalid programs

// Get a counterexample as a simple string
String simpleString = verifier.getCounterexampleString()    // Prints:
                                                            //   Counterexample:
                                                            //   x = 1
                                      
// Get a counterexample as a z3-style string
String z3String = verifier.getCounterexampleRaw()

// Get a counterexample as a map
Map<String, Integer> = verifier.getCounterexampleMap()
```

# Detailed Usage

## Creating a Program
To create an IMP program, an AST must be manually created as there is no parser implementation.
The nodes of the AST for the program should follow the [IMP Grammar](#imp-grammar). A concrete
example can be found in `VerifierAbsTest::createProgram()`.

### Expressions
**Variables**: `var x = new IntegerExpression("x")`   
**Integers**: `var zero = new VariableExpression(0)`  

### Conditions
**True**: `var t = new Boolean(true)`  
**False**: `var f = new Boolean(false)`  
**Add**: `var add = new BinaryExpression(ExpressionType.ADD, x, y)`  
**Multiply**: `var mul = new BinaryExpression(ExpressionType.MUL, x, y)`  

### Statements
**Assignment**: `var assign = new Assignment(x, zero)`  
**Composition**: `var composition = new Composition(statement, statement)`  
**IfElse**: `var ifElse = new If(condition, statement, statement)`

## Creating pre- / post-conditions
To create pre- / post-conditions, create an AST following the [Logic Grammar](#logic-grammar). Same
nodes as [Creating a Program](#creating-a-program), but with additional logical condition operators:  

### Conditions (only for use in pre- / post-conditions)
... All conditions from IMP     
**And**: `var and = new BinaryConnective(ConnectiveType.AND, condition, condition)`  
**Or**: `var or = new BinaryConnective(ConnectiveType.OR, condition, condition)`  
**Implies**: `var implies = new BinaryConnective(ConnectiveType.IMPLIES, condition, condition)`  
**Not**: `var not = new UnaryConnective(ConnectiveType.NOT, condition)`

## Verifying a program
See [Basic Example](#basic-example) for general usage of `Verifier`. The primary method is
`Verifier.verify()`, which takes either 2 or 3 arguments. The `program` should be a top-level
[Statement](#statements). `precondition` and `postcondition` must be top-level conditions.

`verify(program, precondition, postcondition)`: Returns `true` if program with pre- and post-                                                              conditions is valid  
`verify(program, postcondition)`: Same as above but with no precondition (precondition = true)

## Getting a counterexample
See the end of [Basic Example](#basic-example) for how to obtain counterexamples (inputs which
would make the program incorrect according to the given conditions). The most important thing is
to call `verify()` before calling `getCounterexample_()`, otherwise an exception will be thrown.
`getCounterexample_()` will always return a counterexample for the last verified program. If the
program is valid, an empty string or map will be returned.