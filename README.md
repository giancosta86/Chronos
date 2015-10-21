# Chronos

*Programming language for discrete event simulation*


## Introduction

Discrete event simulation (DES) is a branch of Operations Research applied to a wide range of domains, which is why several valuable programming languages have been developed over the years to create executable models according to this set of techniques.

Chronos is a didactic, interpreted, context-free language for DES, inspired by [SIMSCRIPT](http://www.simscript.com/), an important language described by Professor Silvano Martello in [his lectures](http://www.or.deis.unibo.it/staff_pages/martello/Slides_LM_new.html) and [his book](http://www.editrice-esculapio.com/martello-ricerca-operativa-per-la-laurea-magistrale/); a few ideas and elements were borrowed from Python, JavaScript and Scala as well.

Chronos is designed to help students foster their acquaintance with DES and its concepts, which can be easily applied to other languages; it can also be a valid tool for research workers.

Last but not least, Chronos is my very first context-free language - I hope you'll enjoy it! ^\_\_^


## Features

* **Intepreted language**: variables require no type declaration, and values of different types can be assigned to the same variable at different times.

* **Rich type system**: variables can be of type *Double*, *Int*, *Bool*, *String*, *Infinity*, *Entity* or *Map*.

* **Infinity algebra**: **inf** and **-inf** are constants that can be assigned to variables and employed in numerical operations and comparisons.

* **Entities**: entities are objects whose attributes can be set at runtime - for example, you can create a *Book* entity and assign it *title*, *author* and further attributes.

* **Procedures**: parameterized lines of code can be grouped into a procedure, just like traditional scripting languages. The user *cannot* define functions in the typical sense, but a procedure can actually return one or more values by modifying attributes of passed entities.

* **Global scope**: identifiers not found in the current scope will be looked for in the global scope. Setting an identifier in the global scope requires the **global** keyword, to prevent namespace pollution.

* **Queues**: entities can be stored into global containers called *queues*: queues support just a few basic operations:

  * Insert an element
  * Get and remove the first element
  * Remove an element (whatever its position)
  * Is the queue empty?

  Furthermore, each queue is ruled by an *insertion strategy*, which defines how elements should be ordered:

  * **FIFO**: First-In, First-Out
  * **LIFO**: Last-In, First-Out
  * **SORTED**: according to an attribute of the entities (for example: *title*). Sort order can be either *ascending* or *descending*.


* **Events**: the most important aspect of Chronos. An event is a named block of code - similar to a procedure - but it takes no parameters. On the other hand, events can be *reified* into a special kind of entity, called *event notice*, which can be *scheduled* - that is, the related event code will be executed when the internal clock reaches its appointed instant.
Multiple events can be scheduled for the same moment, in FIFO order. Actually, the engine does not really employ a clock, but an optimized timeline.

* **Flow control**: **if/else** and **while** statements are provided. There is no **for** loop, because Chronos tries to adhere as much as possible to block diagrams.

* **Advanced random functions**: language constructs can generate values employing uniform double and integer distributions, as well as exponential distributions. It is also possible to set the random seed.

* **Fine-grained user input**: the user can input doubles, integers, booleans and strings - as required by the program.

* **Conversion functions**: all types can be cast to each other whenever possible. The *floor()* and *ceil()* functions can be applied to *Double* values to obtain the corresponding *Int* value.

* **Assertions**: the **assert** statement introduces design-by-contract.

* **Deallocation check**: to support students learning languages that do not provide garbage collection, the Chronos Virtual Machine reports all the entities and event notices that are still in memory when the program ends; this features can also be disabled via a dedicated global directive.

* **Chronos IDE**: programming in Chronos can be fun! ^\_\_^! [Chronos IDE](https://github.com/giancosta86/Chronos-IDE) is a JavaFX application for writing and testing programs in an Integrated Development Environment (IDE).


## Requirements

Chronos requires Java 8u51 or later.

The **JAVA_HOME** environment variable must also be set. To know more about this, please refer to [this guide](http://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/index.html).


## Running Chronos


The recommended way of running Chronos is [Chronos IDE](https://github.com/giancosta86/Chronos-IDE), its JavaFX development environment.

However, Chronos can also be used in command-line scripts (its command-line interpreter supports I/O redirection) - just employ the scripts in the *bin* directory of the [binary distribution](https://github.com/giancosta86/Chronos/releases/latest).

The exit code of the interpreter is 0 if the program successfully ends, 1 otherwise.


## Language reference

To fully understand the rationale behind Chronos, it is strongly recommended to study Professor Martello's papers on the subject.

Chronos is based on an [ANTLR 4 grammar](https://github.com/giancosta86/Chronos/blob/master/src/main/antlr/Chronos.g4) - reading the grammar is fairly easy and usually provides more detailed information.

What follows is a brief introduction to the syntax and semantics of the language.


### Comments

Comments are like in Java or Scala:

* Single-line comments

  ```
  // Single-line comment
  ```

* Multi-line comments

  ```
  /*
  Multi-line
  comment
  */
  ```

### Program structure

The program structure can be outlined as follows:

```
<global statements>

<one or more event/procedure definition, in any order>

event start {
  <startup code>
}
```

where *global statements* can be, in any order:

* assignments - in this case, the **global** keyword is forbidden - this is the only case where a basic assignment creates a global variable

* queue creation statements (see below)

* map creation statements (see below)

* the **$disableHeapCheck** directive, to turn off deallocation checks at the end of a program (see below)


The *start* event is automatically called when the program starts, like the *main* function in Java or Scala: it must schedule other events, which, in turn, can schedule other events.

The program terminates when:

* there are no more events scheduled
* the user does not provide required input
* a wrong operation is performed (for example, an invalid type conversion)


### Hello, world!

A minimal example is as simple as:

```
event start {
    println "Hello, world! ^__^";
}
```

The **println** statement does not require parentheses: this is a common trait in Chronos, but you can always add parentheses, if you wish.
Statements must *always* end with a semicolon, ";", unless they end with a "}" (for example, the **if** and **while** blocks).

The *start* event is always executed at time 0: the current time is represented by a double, read-only value which can be accessed via the keyword expression **now** (or **time.v**). For example:

```
println now + 5;
```


### Variables, basic data type and scopes

To declare and initialize a variable in Chronos, just assign it a value. For example:

```
myVariable := 5;
```

When assigning a variable, it is visible, starting from that line, within the current event/procedure (the *local scope*).

Identifiers must begin with a letter (uppercase or lowercase) or an underscore ("\_"), and can also include digits starting from the second character.

The *primitive data types* are immutable:

  * *Double*: for example, 9.4. Maps to *double* on the JVM
  * *Int*: for example, 7. Maps to *int* on the JVM
  * *Bool*: **true** and **false**. Maps to *boolean* on the JVM.
  * *String*: for example, "Hello world" - with double quotation marks. String currently do not support escape sequences. Maps to *String* on the JVM
  * *[+/-]inf*: infinity values. They can be mixed with numbers in expressions.


Variables can be reassigned, even to different value types:

    ```
    X := "Hello"; //Now X refers to a String
    X := 80; //Now an Int
    X := -inf; //Now an infinity
    ```

When a binary operation relates an *Int* and a *Double*, the result is *Double*.

If both operands are *Int*, the result is *Int* - especially in *a / b*. For example:

  ```
  println 2 / 3; //Prints 0
  println 2.0 / 3; //Prints 0.6666666666666666
  ```

In *a + b*, if either operand is a *String*, the result will always be a *String*. You can alter this behaviour by using type conversions. For example:

  ```
  println 7 + "10";  //Prints 710
  println 7 + toInt("10"); //Prints 17
  ```

A global assignment can be achieved in 2 ways:

  * Via a normal assignment *before* any procedure/event declaration (that is, in the *global statements* section of the program)

  * By employing the **global** keyword before the assignment within the event/procedure block:

    ```
    global alpha := 9; //Assigns global var Y, creating it if needed
    ```

When a variable is employed in an expression, it will be looked for in 2 different scopes:

  1. the current local scope - which also includes:
    * in the case of a procedure, the parameter names
    * in the case of an event, the event name itself (see below)

  2. the global scope

Therefore, a local variable can *shadow* a global variable - by design, the only way to avoid the issue is to
choose a different name for the local variable.


### Type conversions

A few predefined functions provide checked, meaningful type conversions:

* **toDouble**(*expression*): converts *expression* to *Double*, if possible
* **toInt**(*expression*): converts *expression* to *Int*, if possible
* **toBool**(*expression*): converts *expression* to *Bool*, if possible
* **toString**(*expression*): converts *expression* to *String* (always possible)

A failed conversion will crash the program - therefore, it is important to ensure that input data is correctly typed - which is why the language provides dedicated input statements.


### Input functions

Input is performed by functions:

* **readDouble**(*prompt*)
* **readInt**(*prompt*)
* **readBool**(*prompt*)
* **readString**(*prompt*)

where *prompt* is an expression that will be converted to *String* and shown to the user. It is usually a string literal.

The actual implementation of these functions depends on the underlying environment, but it's reasonable to assume, for example, that **readInt** will not return until the user has typed an integer value, and that it will terminate the program in case of interrupted input.



### Logic expressions

Boolean expressions can be mixed via the usual *logic operators*, which in Chronos are
called **and**, **or** and **not**:

  ```
  alpha := true;
  beta := false;
  gamma := alpha and (beta or not (7 < 5));
  ```

Logic operators have the same priority as in Java - but it is always best to employ parentheses for clarity.


### Comparison operators

*Comparison operators* are **=** (equality, whereas assignment is **:=**), **!=**, **>**, **>=**, **<**, **<=**:

  * *Double*, *Int* and infinities are comparable

  * *Strings* are comparable

  * **true** > **false**

  * *Entities* and *event notices* (which are a kind of entity) are comparable, but the comparison is performed on their memory address: consequently, only **=** and **!=** are meaningful, to understand if two entity variables refer to the very same object

Comparison operators have more priority than logical operators, but, as usual, it is best to use parentheses.

### Output

Chronos provides 2 simple statements:

* **print** *expression*: prints out the given expression

* **println** *expression*: prints out the given expression, followed by a newline character. Without expression, **println** prints out a blank line


### If statement

The general form of the **if** statement is defined as:

```
if conditionA {
  //Statements to be executed if conditionA is true
} else if conditionB {
  //Statements to be executed if conditionB is true
} else {
  //Statements to be executed if the above conditions are all false
}
```
Where:

* There can be as many **else if** branches as needed - even zero

* The **else** part is optional as well

Please, note that *condition* does not require parentheses - but it *must* be followed by a block between braces, to improve readability.

Conditional blocks do *not* define a scope: variables defined in conditional blocks get defined in the scope of the current event/procedure.



### While statement

The **while** statement is defined as:

```
while condition {
  //Statements to be executed
}
```

Please, note that *condition* does not require parentheses - but it *must* be followed by a block between braces, to improve readability.

The loop body does *not* define a scope: variables defined within such block get defined in the scope of the current event/procedure.


### Assertions

The **assert** statement enables a basic but effective form of [design by contract](https://en.wikipedia.org/wiki/Design_by_contract):

```
assert condition;
```

Whenever such a statement is encountered, *condition* is evaluated and, if is **false**, the program crashes, reporting such line.

Assertions are very useful to state program constraints, therefore ensuring consistency.


### Creating entities

To create an entity and assign it to a local variable, you need to *allocate* it with the **create** statement, which comes in 2 forms:

  * the basic form is, for example:
    ```
    create book;
    ```

    This creates an entity of type *book* and also assigns it to the local variable named *book*.


  * the extended form supports the declaration of both an entity type and a local variable, and it's preferable in order to declare 2 entities of the same type in the same scope:

    ```
    create book called exampleBook;
    create book called anotherBook;
    ```

    Now the local context contains 2 variables - *exampleBook* and *anotherBook*, both of type *book*.

Strictly speaking, the entity type does not affect the internal structure of entities. It has just 2 main purposes:

 * in a domain-driven approach, it participates in the description of the entity - for example, printing an entity shows its type and its attributes

 * if the entity type coincides with an event name, an entity notice is created instead; we'll return on this concept later.

After creating an entity, you can assign it to other local/global variables. Please, keep in mind that only the *reference* to the entity will be copied, therefore all the variables will point to the very same entity in memory.


### Entity attributes

Entities behave like key/value pairs and are initially empty.

Further key/value pairs are called *attributes* (or *properties*) and can be added or set using this syntax:

  ```
  author(book) := "Alan Turing";
  ```

Any object can be assigned to an attribute - including other entities, recursively!

  ```
  create person called greatScientist;
  name(greatScientist) := "Alan Turing";
  println greatScientist;

  create book;
  author(book) := greatScientist;
  ```


### Deallocating entities

Entities *should* be deallocated, via the following statement:

```
destroy localOrGlobalVariable; //Must reference an entity
```

Deallocating the same entity twice results in a failure.

If the program successfully terminates, Chronos will report all the entities that were not deallocated: this check has actually no effect, but it is due to the didactic nature of Chronos.

To optimize performances and memory requirements, this check can be turned off via the **$disableHeapCheck** global statement at the beginning of the program:

```
$disableHeapCheck;
```


### Queues

Entity queues are paramount in simulations, and Chronos supports them extensively.

Queues belong to the global scope, but in their own namespace, accessible via the queue manipulation instructions.

To create a queue, use one of the following global statements before any event or procedure:

  * **queue** queueName **fifo**; *//creates a FIFO queue*
  * **queue** queueName **lifo**; *//creates a LIFO queue*
  * **queue** queueName **sorted by** propertyName **asc**; *//sorted in ascending order. The default if the sort order is omitted*
  * **queue** queueName **sorted by** propertyName **desc**; *//sorted in descending order*

The last 2 statements both define a **sorted** queue - that is, a queue whose elements are sorted according to a given attribute (for example, the property *title* in a queue of *book* entities)


Given any type of queue, the related operations are fairly straightforward:

```
//The type of the queue (in this case, "fifo") does not alter the syntax of queue operations
queue myQueue fifo;

event start {
  create book;
  title(book) := "Chronos - Reference guide";

  create anotherBook;
  title(anotherBook) := "Chronos IDE - User guide";

  insert book into myQueue;
  insert anotherBook into myQueue;

  assert not isEmpty(myQueue);

  firstBookFound := get first from myQueue;
  println firstBookFound;

  secondBookFound := get first from myQueue;
  println secondBookFound;

  assert isEmpty(myQueue);

  insert book into myQueue;

  //Removes the entity from the book, whatever its position
  remove book from myQueue;

  if isEmpty(myQueue) {
    println "The queue is now empty!";
  }

  destroy book;
  destroy anotherBook;
}
```

There are 2 important aspects to take into account when dealing with sorted queues:

  * queues are *dynamically typed*: when you add an entity to a sorted queue, it is only required that such entity contains the *property* on which the queue is based - for example, if a queue is sorted by *title*, you could add both *book* and *film* objects, provided they both have a *title* property.

  * For performance reasons, items are sorted *when added to the queue*: consequently, you should *not* alter the property on which the queue is based, at least as long as the entity is in the queue - otherwise, the correct order cannot be ensured.


### Procedures

Procedures are top-level, named blocks of statements.

A procedure can define zero or more parameters and can contain any number of statements.

For example:
```
procedure procedureWithNoParams {
  //Statements here
}

procedure updateStats(book, quantity) {
  //Statements here
}
```

Procedures cannot be nested.

To call a procedure, employ the **call** statement:

```
call procedureWithNoParams;

call updateStats(myReferenceGuide, 5);
```

User-defined functions are, by design, *not* supported, but entities provide a way to simulate them:

```
event start {
  create result;
  call sum(6, 8, result);
  println "The sum is: " + value(result);

  destroy result;
}

procedure sum(x, y, result) {
  value(result) := x + y;
}
```

Incidentally, the example above shows that events and procedures can be declared in any order.

To return from a procedure, just employ the **return** statement.

```
return;
```

### Events

Events are the cornerstone of a simulation system, which is why Chronos is very flexible in this regard.

An event is a named, global, non-nestable block defining a local scope, exactly like procedure. But, unlike procedures, events cannot be called via the **call** statement and, consequently, they do not support parameters.

First of all, every program requires a *start* event, which is its entry point - a sort of *main* function, found in many traditional programming languages. The duty of the *start* event is to *schedule event notices*.

An *event notice* is simply *an entity whose type matches the name of an event*; being an entity, an event notice can be assigned properties.

For example:

```
event start {
  /*
  Creates an event notice bound to additionalEvent and assigns it
  to a local variable named "additionalEvent"
  */
  create additionalEvent;

  /*
  Creates an event notice bound to additionalEvent, assigning it
  to the local variable "myEventNotice"
  */
  create additionalEvent called myEventNotice;
}


event additionalEvent {

}
```

The first form is lighter, the second form is preferred to create multiple event notices referencing the same event within the same local context.

An event notice can also be *scheduled* - that is, the code of its related event will be executed at a given instant.

To schedule an event notice there are 4 variants of the **schedule** statement:

* **schedule** *eventName* **at** *instant*
* **schedule** *eventName* **called** *eventNotice* **at** *instant*

* **schedule** *eventName* **after** *interval*
* **schedule** *eventName* **called** *eventNotice* **after** *interval*

The first 2 forms schedule event notices by instant, the other 2 forms schedule by interval from the current instant. For example:

```
schedule additionalEvent at 90;
schedule additionalEvent called myEventNotice after 10;
```


To pass data to events, apart from (fairly inelegant) global variables, one can use *attributes of the triggering event notice*: in other words, every event has a local variable named like the event itself and referencing *the event notice created to schedule the event* - this means that it's possible to set attributes of the event notice while scheduling it, and retrieve such attributes within the event body. For example:

```
event start {
  //This creates an event notice referring customEvent
  //and assigned to the customEvent local variable
  create customEvent;
  magicNumber(customEvent) := 42;

  //This creates an event notice referring customEvent
  //and assigned to the myNotice local variable
  create customEvent called myNotice;
  magicNumber(myNotice) := magicNumber(customEvent) + 1;

  schedule customEvent at 10;
  schedule customEvent called myNotice at 15.5;
}


event customEvent {
  println magicNumber(customEvent);

  //Always destroy the event notice if you do not reschedule it
  destroy customEvent;
}
```

In the event block, the event notice can be rescheduled, or it can be destroyed, just like other entities; anyway, event notices *must* be destroyed at some point - unless the deallocation check is disabled via global directive, of course.

In both events and procedures, the special constants *now* and *time.v* both return the double value of the current simulation time - which is 0 in the *start* event and is automatically increased by the engine as the events flow.

The **return** keyword stops the current event, making the execution proceed to the next event in the schedule, if available.


### Maps

Setting properties of entities is effective, but it presents 2 major drawbacks:

* Property names must be valid identifiers - in particular, they cannot be numbers
* Property names cannot be *dynamic* - to access the *title* property of a *book* entity, we need to write *title(book)*, which is hardwired in the code.

To overcome this limitation, at the beginning of the program it is possible to define special global variables called *maps*:

```
map mapName;
```

A map is a dictionary of properties, like an entity, but:

* Its keys can be *any primitive*, immutable value type (especially numbers)
* The key can be any expression, unknown until the very moment when it's accessed

For example:

```
map myMap;

event start {
  myMap("title") := "Chronos reference guide";

  titleProperty := "title";

  //We are accessing the map via an expression!
  println myMap(titleProperty);
}
```

Arrays are a very useful application of maps:

```
map bookArray;
bookCount := 10;

event start {
  i := 1;
  while (i <= bookCount) {
    create book;
    title(book) := "Reference Guide - Part " + i;
    bookArray(i) := book;
    i := i + 1;
  }

  i := 1;
  while (i <= bookCount) {
    println title(bookArray(i));
    i := i + 1;
  }

  i := 1;
  while (i <= bookCount) {
    destroy bookArray(i);
    i := i + 1;
  }
}
```

In this example, we didn't need to declare the "array size" - because it is actually a map: such feature is especially useful to define *sparse arrays*.

**Note**: unlike queues, maps belong to the *global namespace*, so they might be shadowed by local variables. Also, it is perfectly licit (but rather obscure and not recommendable) to overwrite them via *global assignments*.


### Random numbers

Chronos supports random numbers via 3 predefined functions:

* *uniformRandom(min, max)*: returns a random *Double* value with uniform distribution in the range [min; max). Please note that *max* is *excluded*

* *uniformIntRandom(min, max)*: returns a random *Int* value with uniform distribution in the range [min; max]. In this case, *max* is *included*

* *expRandom(averageValue)*: return a random *Double* value with exponential distribution having the given average value

Whenever a program starts, a new random seed is chosen; you can set the random seed at any time by using the **setRandomSeed** statement:

```
setRandomSeed numericExpression;
```


### Terminating a program

Programs naturally end their execution when there are no more events in the schedule.

To force immediate (but still successful) program termination, you can use the **exit** statement:

```
exit;
```

To force a failure, you could use, for example:

```
assert false;
```

## Experimenting

Chronos is an agile, interpreted language - feel free to try it yourself, especially in its [user-friendly IDE](https://github.com/giancosta86/Chronos-IDE)! ^\_\_^

As a plus, the test packages contain a suite of test programs, crafted to verify the language features: you might want to check them out as well.


## The Chronos Virtual Machine

Chronos is a programming language based on an multi-layered architecture:

* The *parser* is in Java, generated via ANTLR.

* The concrete tree visitor, creating the AST, is written in Scala. In particular, the methods of the *BasicAstBuilder* object generate the AST from the program source code.

* The virtual machine is written in Scala as well, and it's pluggable in other applications: by implementing the traits *Input* and *Output*, and passing them to an instance of the *Interpreter* class, other software can integrate the Chronos engine.

* [Chronos-IDE](https://github.com/giancosta86/Chronos-IDE) is an application mixing the Chronos Virtual Machine into [OmniEditor](https://github.com/giancosta86/OmniEditor), an easy-to-use JavaFX library for creating custom IDEs.


## Special thanks


* [Professor Silvano Martello](http://www.or.deis.unibo.it/staff_pages/martello/cvitae.html), for his valuable advice and teaching in the field of Operations Research

* [Professor Enrico Denti](http://enricodenti.disi.unibo.it/index.shtml), for his valuable advice and teaching in the field of Languages and Computational Models

* [Scala](http://www.scala-lang.org/) - an extremely elegant language that has quickly become one of my favourite languages

* [SIMSCRIPT](http://www.simscript.com/) - the language that mainly inspired Chronos

* [Python](https://www.python.org/) - another language that contributed to inspiring Chronos

* [ANTLR](http://www.antlr.org/) - a great parser generator, and much more
