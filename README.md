# Chronos

*Programming language for discrete event simulation*


## Introduction

Discrete event simulation (DES) is a branch of Operations Research applied to a wide range of domains, which is why several valuable programming languages have been developed over the years to create executable models according to this set of techniques.

Chronos is a didactic, interpreted, context-free language for DES, inspired by the introduction to [SIMSCRIPT](http://www.simscript.com/) described by Professor Silvano Martello, at University of Bologna; a few ideas and elements were borrowed from Python, JavaScript and Scala as well.

Chronos is designed to help students foster their acquaintance with DES and its concepts, which can be easily applied to other languages; it can also be a valid tool for researchers.

The Chronos Virtual Machine is entirely written in the [Scala programming language](http://scala-lang.org/).


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

The **JAVA_HOME** environment variable must also be set. For further reference, please consult [this guide](http://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/index.html).


## Reference

Chronos is described in a friendly [wiki](https://github.com/giancosta86/Chronos/wiki)! You are all invited to visit it to learn the language!


## Using Chronos

Chronos is an agile, interpreted language - feel free to try it yourself, especially in [Chronos IDE](https://github.com/giancosta86/Chronos-IDE), its visual development environment!

[![Chronos IDE - Screenshot](https://github.com/giancosta86/Chronos-IDE/blob/master/Screenshot.png)](https://github.com/giancosta86/Chronos-IDE)

As a plus, the source code contains a full suite of [test programs](https://github.com/giancosta86/Chronos/tree/master/src/test/resources/info/gianlucacosta/chronos/interpreter/programs), crafted to verify the language features: you might want to check them out as well.
