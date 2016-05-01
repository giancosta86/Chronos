/*§
  ===========================================================================
  Chronos
  ===========================================================================
  Copyright (C) 2015-2016 Gianluca Costa
  ===========================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ===========================================================================
*/

grammar Chronos;

@header {
package info.gianlucacosta.chronos.parser;
}


// ------------------
// PROGRAM DEFINITION
// ------------------

program: globalStatement* (event | procedure)+ EOF;



// -----------------
// GLOBAL STATEMENTS
// -----------------

globalStatement: (createQueue | createMap | implicitGlobalAssignment | disableHeapCheck) ';';

createQueue: 'queue' IDENTIFIER (FIFO | LIFO | 'sorted' 'by' IDENTIFIER (ASC|DESC)?);

createMap: 'map' IDENTIFIER;

implicitGlobalAssignment: assignment;




// ----------------
// TOP-LEVEL BLOCKS
// ----------------


event: 'event' IDENTIFIER block;

procedure: 'procedure' IDENTIFIER ('(' param (',' param)* ')')? block;
param: IDENTIFIER;


block: '{' localStatement* '}';

disableHeapCheck: '$disableHeapCheck';



// ----------
// STATEMENTS
// ----------

localStatement: (basicLocalStatement ';') | ifStatement | whileStatement;


basicLocalStatement: createEntity
             | destroyEntity

             | schedule
             | cancel

             | localAssignment
             | globalAssignment

             | enqueue
             | remove

             | call
             | returnStatement

             | exit

             | read

             | print
             | println

             | assertion

             | setRandomSeed;



// --------------------
// CREATION/DESTRUCTION
// --------------------

createEntity: 'create' IDENTIFIER ('called' reference)?;
destroyEntity: 'destroy' reference;



// ----------------
// EVENT MANAGEMENT
// ----------------

schedule: 'schedule' IDENTIFIER ('called' reference)? (AT | AFTER) expression;
cancel: 'cancel' IDENTIFIER ('called' reference)?;



// ----------
// ASSIGNMENT
// ----------

localAssignment: assignment;
globalAssignment: 'global' assignment;


assignment: reference ':=' expression;
reference: IDENTIFIER ('(' expression ')')?;



// ----------------
// QUEUE MANAGEMENT
// ----------------

enqueue: 'insert' reference 'into' IDENTIFIER;
remove: 'remove' reference 'from' IDENTIFIER;



// ------------
// FLOW CONTROL
// ------------

call: 'call' IDENTIFIER ('(' argument ( ',' argument)* ')')?;
argument: expression;

returnStatement: 'return';

exit: 'exit';

ifStatement: 'if' condition thenPart (elsePart)?;
condition: expression;
thenPart: block;
elsePart: 'else' (block | ifStatement);

whileStatement: 'while' condition block;

assertion: 'assert' expression;



// -----------
// EXPRESSIONS
// -----------


expression: orOperation;

orOperation: andOperation (OP_OR andOperation)*;
andOperation: logicLiteral (OP_AND logicLiteral)*;
logicLiteral: OP_NOT? comparison;


comparison: algebraicSum (comparisonOperator algebraicSum)?;
comparisonOperator: '>' | '>=' | '<' | '<=' | '=' | '!=';


algebraicSum: firstAddend additionalAddend*;
firstAddend: (OP_PLUS|OP_MINUS)? algebraicProduct;
additionalAddend: (OP_PLUS|OP_MINUS) algebraicProduct;


algebraicProduct: firstFactor additionalFactor*;
firstFactor: term;
additionalFactor: additionalFactorOperator term;
additionalFactorOperator: OP_TIMES|OP_OVER;



term: ground
        | priorityExpression
        | referenceValue
        | dequeue
        | functionCall;


priorityExpression: '(' expression ')';

referenceValue: reference;

dequeue: 'get' 'first' 'from' IDENTIFIER;

ground: number
        | plusInf
        | minusInf
        | trueValue
        | falseValue
        | stringValue
        | now;


number: NUMBER;
plusInf: PLUS_INF;
minusInf: MINUS_INF;
trueValue: TRUE;
falseValue: FALSE;
stringValue: STRING;
now: NOW;



// --------------------
// PREDEFINED FUNCTIONS
// --------------------

functionCall: emptyCheck
        | uniformRandom
        | uniformIntRandom
        | exponentialRandom
        | cast
        | floor
        | ceil
        | read;


emptyCheck: 'isEmpty' '(' IDENTIFIER ')';

uniformRandom: 'uniformRandom' '(' expression ',' expression ')';
uniformIntRandom: 'uniformIntRandom' '(' expression ',' expression ')';
exponentialRandom: 'expRandom' '(' expression ')';


cast: castToDouble | castToInt | castToBoolean | castToString;

castToDouble: 'toDouble' '(' expression ')';
castToInt: 'toInt' '(' expression ')';

castToBoolean: 'toBool' '(' expression ')';
castToString: 'toString' '(' expression ')';


floor: 'floor' '(' expression ')';
ceil: 'ceil' '(' expression ')';


read: readDouble | readInt | readBoolean | readString;

readDouble: 'readDouble' '(' prompt ')';
readInt: 'readInt' '(' prompt ')';
readBoolean: 'readBool' '(' prompt ')';
readString: 'readString' '(' prompt ')';
prompt: expression;



// ------------
// INPUT/OUTPUT
// ------------

print: 'print' expression;
println: 'println' expression?;


// -------------
// MISCELLANEOUS
// -------------

setRandomSeed: 'setRandomSeed' expression;



// ------
// TOKENS
// ------


FIFO: 'fifo';
LIFO: 'lifo';
ASC: 'asc';
DESC: 'desc';

FIRST: 'first';


AT: 'at';
AFTER: 'after';


OP_OR: 'or';
OP_AND: 'and';
OP_NOT: 'not';
OP_PLUS: '+';
OP_MINUS: '-';
OP_TIMES: '*';
OP_OVER: '/';


NUMBER: ('0'|[1-9][0-9]*)('.'[0-9]+)?;
PLUS_INF: '+inf'|'inf';
MINUS_INF: '-inf';
TRUE: 'true';
FALSE: 'false';
STRING: '"' ~["]* '"';
NOW: 'now' | 'time.v';


IDENTIFIER: [A-Za-z_][A-Za-z0-9_]*;


WHITESPACE: [ \t\r\n]+ -> skip ;

SINGLE_LINE_COMMENT:  '//' ~( '\r' | '\n' )* -> skip;
MULTI_LINE_COMMENT: '/*' .*? '*/' -> skip;
