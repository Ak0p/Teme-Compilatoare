package cool.parser;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;
import java.util.Optional;

public abstract class ASTNode {
    // Reținem un token descriptiv al nodului, pentru a putea afișa ulterior
    // informații legate de linia și coloana eventualelor erori semantice.
    protected Token token;
    public String debugStr = null;		// used in codegen

    ASTNode(Token token) {
        this.token = token;
    }

    Token getToken() {
        return token;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}

abstract class Expression extends ASTNode {
    Expression(Token token) {
        super(token);
    }
}


class Program extends ASTNode {
    LinkedList<Classz> classes;

    Program(LinkedList<Classz> classes, Token start) {
        super(start);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Classz extends ASTNode {
    Token name;
    Optional<Token> type;
    LinkedList<Feature> features;

    Classz(Token name, Token type, LinkedList<Feature> features, Token start) {
        super(start);
        this.name = name;
        this.type = Optional.of(type);
        this.features = features;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Feature extends ASTNode {
    Feature(Token start) {
        super(start);
    }
}


class Attribute extends Feature {
    Local var;

    Attribute(Local var, Token start) {
        super(start);
        this.var = var;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Method extends Feature {
    Token name;
    Token type;
    LinkedList<Formal> formals;
    Expression body;

    Method(Token name, Token type, LinkedList<Formal> formals, Expression body, Token start) {
        super(start);
        this.name = name;
        this.type = type;
        this.formals = formals;
        this.body = body;
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Variable extends Expression {
    Variable(Token token) {
        super(token);
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Literal extends Expression {
    Literal(Token token) {
        super(token);
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class BinaryOperator extends Expression {
    Expression left;
    Expression right;

    BinaryOperator(Expression left, Token op, Expression right) {
        super(op);
        this.left = left;
        this.right = right;
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class UnaryOperator extends Expression {

    Expression operand;
    UnaryOperator(Expression operand, Token op) {
        super(op);
        this.operand = operand;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

}


class Assignment extends Expression {
    Variable name;
    Expression value;

    Assignment (Variable name, Expression value, Token start) {
        super(start);
        this.name = name;
        this.value = value;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

abstract class Dispatch extends ASTNode {
    Token name;
    LinkedList<Expression> args;
    Dispatch(Token name, LinkedList<Expression> args, Token start) {
        super(start);
        this.name = name;
        this.args = args;
    }
}

class ExplicitDispatch extends Dispatch {
    Dispatch object;
    Optional<Token> dispatchClass;
    ExplicitDispatch(Token name, LinkedList<Expression> args, Dispatch object, Token dispatchClass, Token start) {
        super(name, args, start);
        this.object = object;
        this.dispatchClass = Optional.of(dispatchClass);
        this.name = name;
        this.args = args;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class ImplicitDispatch extends Dispatch {

    ImplicitDispatch(Token name, LinkedList<Expression> args, Token start) {
        super(name, args, start);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class If extends Expression {
    // Sunt necesare trei câmpuri pentru cele trei componente ale expresiei.
    Expression cond;
    Expression thenBranch;
    Expression elseBranch;

    If(Expression cond,
       Expression thenBranch,
       Expression elseBranch,
       Token start) {
        super(start);
        this.cond = cond;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class While extends Expression {
    Expression cond;
    Expression body;
    While(Expression cond, Expression body, Token token) {
        super(token);
        this.cond = cond;
        this.body = body;
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Let extends Expression {
    LinkedList<Local> vars;
    Expression body;
    Let(LinkedList<Local> vars, Expression body, Token start) {
        super(start);
        this.vars = vars;
        this.body = body;
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Local extends ASTNode {
    Variable name;
    Token type;
    Optional<Expression> value;

    Local(Variable name, Token type, Expression value, Token start) {
        super(start);
        this.name = name;
        this.type = type;
        this.value = Optional.of(value);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Case extends Expression {
    Expression cond;
    LinkedList<CaseBranch> branches;

    Case(Expression cond, LinkedList<CaseBranch> branches, Token start) {
        super(start);
        this.cond = cond;
        this.branches = branches;
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class CaseBranch extends ASTNode {
    Token name;
    Token type;
    Expression body;

    CaseBranch(Token name, Token type, Expression body, Token start) {
        super(start);
        this.name = name;
        this.type = type;
    }
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Block extends Expression {
    LinkedList<ASTNode> stmts;

    Block(LinkedList<ASTNode> stmts, Token token) {
        super(token);
        this.stmts = stmts;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Formal extends ASTNode {
    Variable name;
    Token type;

    Formal(Variable name, Token type, Token token) {
        super(token);
        this.name = name;
        this.type = type;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

}










