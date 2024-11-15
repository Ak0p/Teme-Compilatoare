parser grammar CoolParser;

options {
    tokenVocab = CoolLexer;
}

@header{
    package cool.parser;
}

program
    :   (classes+=classz SEMICOLON)+ EOF
    ;

classz
    :   CLASS name=TYPE (INHERITS type=TYPE)? BLOCK_START (features+=feature SEMICOLON)* BLOCK_END
    ;

feature
    :   name=ID L_PAR (formals+=formal (COMMA formals+=formal)*)? R_PAR COLON type=TYPE BLOCK_START body=expr BLOCK_END   # method
    |   var=localVar                                                                  # attribute
    ;

formal
    :   name=ID COLON type=TYPE
    ;

localVar
    :   name=ID COLON type=TYPE (ASSIGN value=expr)?
    ;


expr
    :   assignExpr
    ;

assignExpr
    :   <assoc=right> name=ID ASSIGN  value=expr
    |   latterUnaryExpr
    ;

latterUnaryExpr
    : NEW operand=TYPE   # allocation
    | NOT e=primaryExpr  # logicalNegation
    | compExpr              # formerExpr
    ;
compExpr
    :   left=compExpr op=LT right=addSubExpr
    |   left=compExpr op=LE right=addSubExpr
    |   left=compExpr op=EQ right=addSubExpr
    |   left=compExpr op=GT right=addSubExpr
    |   left=compExpr op=GE right=addSubExpr
    |   addSubExpr
    ;

addSubExpr
    :   left=addSubExpr op=PLUS right=mulDivExpr
    |   left=addSubExpr op=MINUS right=mulDivExpr
    |   mulDivExpr
    ;

mulDivExpr
    :   left=mulDivExpr op=MUL right=unaryExpr
    |   left=mulDivExpr op=SLASH right=unaryExpr
    |   unaryExpr
    ;

unaryExpr
    :   op=NEG operand=unaryExpr
    |   op=ISVOID operand=unaryExpr
    |   accessExpr
    ;

accessExpr
    : object=accessExpr (AT type=TYPE)? DOT name=ID L_PAR (args+=expr (COMMA args+=expr)*)? R_PAR          # explicitDispatch
    | name=ID (args+=expr (COMMA args+=expr)*)? R_PAR                                                #   implicitDispatch
    | primaryExpr                                                                        # otherExpr
    ;

primaryExpr
    : L_PAR expr R_PAR                                                      # parenExpr
    | IF cond=expr THEN thenBranch=expr ELSE elseBranch=expr FI             # ifExpr
    | WHILE cond=expr LOOP body=expr POOL                                   # whileExpr
    | BLOCK_START (stmts+=expr SEMICOLON)+ BLOCK_END                        # blockExpr
    | LET vars+=localVar (COMMA vars+=localVar)* IN body=expr                     # letExpr
    | CASE cond=expr OF branches+=caseBranch+ ESAC                          # caseExpr
    | ID                                                                    # idExpr
    | INT                                                                   # intExpr
    | STRING                                                                # stringExpr
    | TRUE                                                                  # trueExpr
    | FALSE                                                                 # falseExpr
    ;

caseBranch
    : name=ID COLON type=TYPE CASE_ARROW body=expr
    ;



