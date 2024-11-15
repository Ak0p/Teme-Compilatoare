package cool.parser;

public interface ASTVisitor<T> {
    T visit(Program program);

    T visit(Classz classz);

    T visit(Attribute attribute);

    T visit(Method method);

    T visit(Variable variable);

    T visit(Literal literal);

    T visit(BinaryOperator binaryOperator);

    T visit(UnaryOperator unaryOperator);

    T visit(Assignment assignment);

    T visit(ExplicitDispatch explicitDispatch);

    T visit(ImplicitDispatch implicitDispatch);

    T visit(If anIf);

    T visit(While aWhile);

    T visit(Let let);

    T visit(Local local);

    T visit(Case aCase);

    T visit(CaseBranch caseBranch);

    T visit(Block block);

    T visit(Formal formal);

    ;
}
