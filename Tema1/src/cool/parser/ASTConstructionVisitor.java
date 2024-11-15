package cool.parser;

import java.util.LinkedList;

public class ASTConstructionVisitor extends CoolParserBaseVisitor<ASTNode>{

    @Override
    public ASTNode visitProgram(CoolParser.ProgramContext ctx) {
        LinkedList<Classz> classes = new LinkedList<>();
        for (var child : ctx.classz()) {
            Classz classz = (Classz) visit(child);
            if (classz != null) {
                classz.debugStr = child.getText();
                classes.add(classz);
            }
        }
        return new Program(classes, ctx.getStart());
    }

    public ASTNode visitClassz(CoolParser.ClasszContext ctx) {
        LinkedList<Feature> features = new LinkedList<>();
        for (var child : ctx.features) {
            Feature feature = (Feature) visit(child);
            if (feature != null) {
                feature.debugStr = child.getText();
                features.add(feature);
            }
        }
        return new Classz(ctx.name, ctx.type, features, ctx.getStart());
    }

    public ASTNode visitMethod(CoolParser.MethodContext ctx) {
        LinkedList<Formal> formals = new LinkedList<>();
        for (var child : ctx.formals) {
            Formal formal = (Formal) visit(child);
            if (formal != null) {
                formal.debugStr = child.getText();
                formals.add(formal);
            }
        }
        return new Method(ctx.name, ctx.type, formals, (Expression) visit(ctx.body), ctx.getStart());
    }

    public ASTNode visitAttribute(CoolParser.AttributeContext ctx) {
        return new Attribute((Local) visit(ctx.localVar()), ctx.getStart());
    }

    public ASTNode visitFormal(CoolParser.FormalContext ctx) {
        return new Formal( new Variable(ctx.name), ctx.type, ctx.start);
    }

    public ASTNode visitLocalVar(CoolParser.LocalVarContext ctx) {
        return new Local(new Variable(ctx.name), ctx.type, (Expression) visit(ctx.value), ctx.start);
    }

    public ASTNode visitExpr(CoolParser.ExprContext ctx) {
        return visit(ctx.assignExpr());
    }

    public ASTNode visitAssignExpr(CoolParser.AssignExprContext ctx) {
        if (ctx.latterUnaryExpr() != null)
            return visit(ctx.latterUnaryExpr());
        return new Assignment(new Variable(ctx.name), (Expression) visit(ctx.value), ctx.getStart());
    }

    public ASTNode visitLatterUnaryExpr(CoolParser.LatterUnaryExprContext ctx) {
        if (ctx instanceof CoolParser.AllocationContext) {
            CoolParser.AllocationContext newCtx = (CoolParser.AllocationContext) ctx;
            return new UnaryOperator(new Variable(newCtx.operand), newCtx.start);
        } else if (ctx instanceof CoolParser.LogicalNegationContext) {
            CoolParser.LogicalNegationContext newCtx = (CoolParser.LogicalNegationContext) ctx;
            return new UnaryOperator((Expression) visit(newCtx.e), newCtx.start);
        }
        return visit( ((CoolParser.FormerExprContext)ctx).compExpr());
    }

    public ASTNode visitCompExpr(CoolParser.CompExprContext ctx) {
        if (ctx.addSubExpr() != null)
            return visit(ctx.addSubExpr());
        return new BinaryOperator((Expression) visit(ctx.left), ctx.op, (Expression) visit(ctx.right));
    }

    public ASTNode visitAddSubExpr(CoolParser.AddSubExprContext ctx) {
        if (ctx.mulDivExpr() != null)
            return visit(ctx.mulDivExpr());
        return new BinaryOperator((Expression) visit(ctx.left), ctx.op, (Expression) visit(ctx.right));
    }

    public ASTNode visitMulDivExpr(CoolParser.MulDivExprContext ctx) {
        if (ctx.unaryExpr() != null)
            return visit(ctx.unaryExpr());

        return new BinaryOperator((Expression) visit(ctx.left), ctx.op, (Expression) visit(ctx.right));
    }

    public ASTNode visitUnaryExpr(CoolParser.UnaryExprContext ctx) {
        if (ctx.accessExpr() != null)
            return visit(ctx.accessExpr());

        return new UnaryOperator((Expression) visit(ctx.operand), ctx.op);
    }

    public ASTNode visitAccessExpr(CoolParser.AccessExprContext ctx) {
        if (ctx instanceof CoolParser.ExplicitDispatchContext) {
            CoolParser.ExplicitDispatchContext newCtx = (CoolParser.ExplicitDispatchContext) ctx;
            LinkedList<Expression> args = new LinkedList<>();
            for (var child : newCtx.args) {
                Expression arg = (Expression) visit(child);
                if (arg != null) {
                    arg.debugStr = child.getText();
                    args.add(arg);
                }
            }

            return new ExplicitDispatch(newCtx.name, args, (Dispatch) visit(newCtx.object), newCtx.type, newCtx.start);
        }
        else if (ctx instanceof CoolParser.ImplicitDispatchContext) {
            CoolParser.ImplicitDispatchContext newCtx = (CoolParser.ImplicitDispatchContext) ctx;
            LinkedList<Expression> args = new LinkedList<>();
            for (var child : newCtx.args) {
                Expression arg = (Expression) visit(child);
                if (arg != null) {
                    arg.debugStr = child.getText();
                    args.add(arg);
                }
            }
            return new ImplicitDispatch(newCtx.name, args, newCtx.start);
        }
        return visit(((CoolParser.OtherExprContext) ctx).primaryExpr());
    }

    public ASTNode visitOtherExpr(CoolParser.OtherExprContext ctx) {
        return visit(ctx.primaryExpr());
    }

    public ASTNode visitParenExpr(CoolParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    public ASTNode visitIfExpr(CoolParser.IfExprContext ctx) {
        return new If((Expression) visit(ctx.cond), (Expression) visit(ctx.thenBranch), (Expression) visit(ctx.elseBranch), ctx.start);
    }

    public ASTNode visitWhileExpr(CoolParser.WhileExprContext ctx) {
        return new While((Expression) visit(ctx.cond), (Expression) visit(ctx.body), ctx.start);
    }

    public ASTNode visitBlockExpr(CoolParser.BlockExprContext ctx) {
        LinkedList<ASTNode> stmts = new LinkedList<>();
        for (var child : ctx.stmts) {
            ASTNode stmt = visit(child);
            if (stmt != null) {
                stmt.debugStr = child.getText();
                stmts.add(stmt);
            }
        }
        return new Block(stmts, ctx.start);
    }

    public ASTNode visitLetExpr(CoolParser.LetExprContext ctx) {
        LinkedList<Local> locals = new LinkedList<>();
        for (var variable: ctx.vars) {
            Local local = (Local) visit(variable);
            if (local != null) {
                local.debugStr = variable.getText();
                locals.add(local);
            }
        }
        return new Let(locals, (Expression) visit(ctx.body), ctx.start);
    }

    public ASTNode visitCaseExpr(CoolParser.CaseExprContext ctx) {
        LinkedList<CaseBranch> branches = new LinkedList<>();
        for (var child : ctx.branches) {
            CaseBranch branch = (CaseBranch) visit(child);
            if (branch != null) {
                branch.debugStr = child.getText();
                branches.add(branch);
            }
        }
        return new Case((Expression) visit(ctx.cond), branches, ctx.start);
    }

    public ASTNode visitCaseBranch(CoolParser.CaseBranchContext ctx) {
        return new CaseBranch(ctx.name, ctx.type, (Expression) visit(ctx.body), ctx.start);
    }

    public ASTNode visitStringExpr(CoolParser.StringExprContext ctx) {
        return new Literal(ctx.STRING().getSymbol());
    }

    public ASTNode visitIntExpr(CoolParser.IntExprContext ctx) {
        return new Literal(ctx.INT().getSymbol());
    }

    public ASTNode visitIDExpr(CoolParser.IdExprContext ctx) {
        return new Literal(ctx.ID().getSymbol());
    }

    public ASTNode visitTrueExpr(CoolParser.TrueExprContext ctx) {
        return new Literal(ctx.TRUE().getSymbol());
    }

    public ASTNode visitFalseExpr(CoolParser.FalseExprContext ctx) {
        return new Literal(ctx.FALSE().getSymbol());
    }

}
