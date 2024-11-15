package cool.parser;

public class PrintVisitor implements ASTVisitor<Void>{

    int indent = 0;

    void printIndent(String str) {
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        System.out.print(str);
    }

    void printIndentedLine(String str) {
        printIndent(str);
        System.out.println();
    }

    @Override
    public Void visit(Program program) {
        printIndentedLine("program");
        indent += 2;
        program.classes.forEach(c -> c.accept(this));
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Classz classz) {
        printIndentedLine("class: " + classz.name.getText());
        indent += 2;
        classz.features.forEach(f -> f.accept(this));
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Attribute attribute) {
        printIndentedLine("attribute");
        indent += 2;
        attribute.var.accept(this);
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Method method) {
        printIndentedLine("method: " + method.name.getText());
        indent += 2;
        method.formals.forEach(f -> f.accept(this));
        method.body.accept(this);
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Variable variable) {
        printIndentedLine(variable.getToken().getText());
        return null;
    }

    @Override
    public Void visit(Literal literal) {
        printIndentedLine(literal.getToken().getText());
        return null;
    }

    @Override
    public Void visit(BinaryOperator binaryOperator) {
        printIndentedLine(binaryOperator.op.getText());
        indent += 2;
        binaryOperator.left.accept(this);
        binaryOperator.right.accept(this);
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(UnaryOperator unaryOperator) {
        printIndentedLine(unaryOperator.op.getText());
        indent += 2;
        unaryOperator.operand.accept(this);
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Assignment assignment) {
        printIndentedLine("<-");
        indent += 2;
        assignment.name.accept(this);
        assignment.value.accept(this);
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(ExplicitDispatch explicitDispatch) {
        printIndentedLine(".");
        indent += 2;
        printIndentedLine(explicitDispatch.name.getText());
        explicitDispatch.object.accept(this);
        explicitDispatch.args.forEach(arg -> arg.accept(this));
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(ImplicitDispatch implicitDispatch) {
        printIndentedLine("implicit dispatch: ");
        indent += 2;
        implicitDispatch.name.getText();
        implicitDispatch.args.forEach(arg -> arg.accept(this));
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(If anIf) {
        printIndentedLine("if");
        indent += 2;
        anIf.cond.accept(this);
        anIf.thenBranch.accept(this);
        anIf.elseBranch.accept(this);
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(While aWhile) {
        printIndentedLine("while");
        indent += 2;
        aWhile.cond.accept(this);
        aWhile.body.accept(this);
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Let let) {
        printIndentedLine("let");
        indent += 2;
        let.vars.forEach(var -> var.accept(this));
        let.body.accept(this);
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Local local) {
        printIndentedLine("local: ");
        indent += 2;
        printIndentedLine(local.getToken().getText());
        local.value.ifPresent(value -> value.accept(this));
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Case aCase) {
        printIndentedLine("case");
        indent += 2;
        aCase.cond.accept(this);
        aCase.branches.forEach(branch -> branch.accept(this));
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        printIndentedLine("CaseBranch");
        indent += 2;
        printIndentedLine(caseBranch.name.getText());
        printIndentedLine(caseBranch.type.getText());
        caseBranch.body.accept(this);
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Block block) {
        printIndentedLine("Block");
        indent += 2;
        block.stmts.forEach(expr -> expr.accept(this));
        indent -= 2;
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        printIndentedLine("formal");
        indent += 2;
        formal.name.accept(this);
        printIndentedLine(formal.type.getText());
        indent -= 2;
        return null;
    }
}
