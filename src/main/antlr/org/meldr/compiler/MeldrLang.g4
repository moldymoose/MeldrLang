grammar MeldrLang; //similar to Little Grammar!

@header {
package org.meldr.compiler;
}

//Parser Rules:
scene: 'SCENE' id 'BEGIN' scene_bdy 'END';
id: IDENTIFIER;
scene_bdy: obj_decl_list | ; //empty scripts should be accepted!
obj_decl_list: obj_decl obj_decl_list | ;
obj_decl: 'OBJECT' id '('param_decl_list')';
param_decl_list: param_decl param_decl_tail | ;
param_decl: model_decl | color_decl | location_decl | dynamic_decl;
param_decl_tail: ',' param_decl param_decl_tail | ;
model_decl: 'MODEL' '=' model_ty;
model_ty: MODEL_TYPE;
color_decl: 'COLOR' '=' '{' rgb_assign ',' rgb_assign ',' rgb_assign '}';
rgb_assign: RGB ':' (FLOATLITERAL | INTLITERAL) '%';
location_decl: 'LOCATION' '=' '('X=INTLITERAL ',' Y=INTLITERAL ',' Z=INTLITERAL')'; //Labels are used to easily retrieve coordinate vals in vistor/listener class implementation!
dynamic_decl: 'DYNAMIC' '=' ('TRUE' | 'FALSE');


//Lexer Rules:
WS : [ \t\n\r]+ -> skip;
COMMENT: '--' ~[\r\n]* '\n' -> skip;
PARAMETERTYPE: 'MODEL' | 'COLOR' | 'LOCATION' | 'DYNAMIC';
RGB: 'RED' | 'GREEN' | 'BLUE';
MODEL_TYPE: 'SPHERE' | 'CUBE';
INTLITERAL : '-'?[0-9]+;
FLOATLITERAL: [0-9]*'.'[0-9]+;
IDENTIFIER: [a-zA-Z]([0-9a-zA-Z_])*;
