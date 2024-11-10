lexer grammar CoolLexer;

tokens { ERROR }

@header{
    package cool.lexer;
}

@members{
    private void raiseError(String msg) {
        setText(msg);
        setType(ERROR);
    }

    public void processString() {
        String text = getText();

        // Remove the surrounding double quotes
        text = text.substring(1, text.length() - 1);

        // Replace known escape sequences
        text = text.replace("\\n", "\n")
                   .replace("\\t", "\t")
                   .replace("\\b", "\b")
                   .replace("\\f", "\f");

        // Replace any other \c with just c
        text = text.replaceAll("\\\\(.)", "$1");

        // Set the processed text as the lexeme for this token
        setText(text);
    }
}

WS
    :   [ \n\f\r\t]+ -> skip
    ;

INVALID: '#'{
    raiseError("Invalid character: " + getText());
};

// KEYWORDS

CLASS      : [cC][lL][aA][sS][sS];
ELSE       : [eE][lL][sS][eE];
FI         : [fF][iI];
IF         : [iI][fF];
IN         : [iI][nN];
INHERITS   : [iI][nN][hH][eE][rR][iI][tT][sS];
ISVOID     : [iI][sS][vV][oO][iI][dD];
LET        : [lL][eE][tT];
LOOP       : [lL][oO][oO][pP];
POOL       : [pP][oO][oO][lL];
THEN       : [tT][hH][eE][nN];
WHILE      : [wW][hH][iI][lL][eE];
CASE       : [cC][aA][sS][eE];
ESAC       : [eE][sS][aA][cC];
NEW        : [nN][eE][wW];
OF         : [oO][fF];
NOT        : [nN][oO][tT];

TRUE       : 't' [rR][uU][eE];
FALSE      : 'f' [aA][lL][sS][eE];

SELF: 'self';
SELFTYPE: 'SELF_TYPE';

ML_COMM_END: '*)' {
    raiseError("Unmatched *)");
};

L_PAR: '(';
R_PAR: ')';
BLOCK_START: '{';
BLOCK_END: '}';
PLUS: '+';
MINUS: '-';
MUL: '*';
SLASH: '/';
NEG: '~';
ASSIGN: '<-';
CASE_ARROW: '=>';
LE: '<=';
LT: '<';
EQ: '=';
SEMICOLON: ';';
COLON: ':';
AT: '@';
DOT: '.';
COMMA: ',';
GE: '>=';
GT: '>';


// IDENTIFIERS
fragment LETTER : [a-zA-Z];
fragment ID_FRAG  :   (LETTER | '_' | DIGIT)*;
ID: [a-z]ID_FRAG;
TYPE: [A-Z]ID_FRAG;


fragment DIGIT  :   [0-9];
INT :   DIGIT+;

SL_COMM: '--' .*? ('\n' | EOF);
ML_COMM: '(*' .*? '*)';
ML_COMM_UNFINISHED: '(*' .*? EOF {
    if (getText().contains("\u001A") == true)
        raiseError("EOF in comment");
};


STRING: '"'(.)*?'"' {

    if (getText().length() > 1024)
        raiseError("String constant too long");
    if (getText().contains("\0") == true)
        raiseError("String contains null character");
    if (getText().contains("\n") == true)
        raiseError("Unterminated string constant");
    if (getText().contains("\u001A") == true)
        raiseError("EOF in string constant");
};

ERROR: .    ;