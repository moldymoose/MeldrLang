grammar MeldrLang; //similar to Little Grammar!

@header {
package org.meldr.compiler;
}

scene: SCENE IDENTIFIER level? obj_decl+ END SCENE;

level: LEVEL INT;

obj_decl: OBJECT IDENTIFIER objectProperty* END OBJECT;

objectProperty: model_decl | color_decl | location_decl | dynamic_decl | size_decl;

model_decl: MODEL '=' IDENTIFIER;
color_decl: COLOR '=' colorValue;
location_decl: LOCATION '=' vector;
dynamic_decl: DYNAMIC '=' booleanValue;
size_decl: SIZE '=' number;

// accepted format coud be as simple as (1,2,-3) or (X=1,Y=2,Z=-3)
vector: '(' x_number ',' y_number ',' z_number ')';

// color can be specified with hex color or text identifier
colorValue: hexColor | rgb | IDENTIFIER;
hexColor: '#' HEXVALUE;
rgb: '(' r_percent ',' g_percent ',' b_percent ')';

booleanValue: TRUE | FALSE;

x_number: X_DEC? number;
y_number: Y_DEC? number;
z_number: Z_DEC? number;
number: INT | FLOAT;

r_percent: R_DEC? percent;
g_percent: G_DEC? percent;
b_percent: B_DEC? percent;
percent: (INT | FLOAT) '%'?;

//Lexer Rules: Keyword sensitivity removed
SCENE     : [Ss][Cc][Ee][Nn][Ee];
OBJECT    : [Oo][Bb][Jj][Ee][Cc][Tt];
END       : [Ee][Nn][Dd];
LEVEL     : [Ll][Ee][Vv][Ee][Ll];
MODEL     : [Mm][Oo][Dd][Ee][Ll];
LOCATION  : [Ll][Oo][Cc][Aa][Tt][Ii][Oo][Nn];
COLOR     : [Cc][Oo][Ll][Oo][Rr];
DYNAMIC   : [Dd][Yy][Nn][Aa][Mm][Ii][Cc];
SIZE      : [Ss][Ii][Zz][Ee];
TRUE      : [Tt][Rr][Uu][Ee];
FALSE     : [Ff][Aa][Ll][Ss][Ee];


X_DEC: ('X' | 'x') (':' | '=');
Y_DEC: ('Y' | 'y') (':' | '=');
Z_DEC: ('Z' | 'z') (':' | '=');

R_DEC: ('R' | 'r') (':' | '=');
G_DEC: ('G' | 'g') (':' | '=');
B_DEC: ('B' | 'b') (':' | '=');

HEXVALUE: [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F];

IDENTIFIER: [a-zA-Z]([0-9a-zA-Z_])*;
INT: '-'? [0-9]+;
FLOAT: '-'? [0-9]+ '.' [0-9]+;

WS : [ \t\n\r]+ -> skip;
COMMENT: '--' ~[\r\n]* '\n' -> skip;