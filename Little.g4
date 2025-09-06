lexer grammar Little;

WS : [ \t\n\r]+ -> skip;
COMMENT: '--' ~[\r\n]* '\n' -> skip;
KEYWORD:
'PROGRAM' |
'BEGIN' |
'END' |
'FUNCTION' |
'READ' |
'WRITE' |
'IF' |
'ELSE' |
'ENDIF' |
'WHILE' |
'ENDWHILE' |
'CONTINUE' |
'BREAK' |
'RETURN' |
'INT' |
'VOID' |
'STRING' |
'FLOAT';
OPERATOR: ':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' |
'(' | ')' | ';' | ',' | '<=' | '>=';
IDENTIFIER: [a-zA-Z]([0-9a-zA-Z])*;
INTLITERAL : [0-9]+;
FLOATLITERAL: [0-9]*'.'[0-9]+;
STRINGLITERAL: '"'~["]*'"';

