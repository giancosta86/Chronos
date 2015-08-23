/*§
  ===========================================================================
  Chronos
  ===========================================================================
  Copyright (C) 2015 Gianluca Costa
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

package info.gianlucacosta.chronos.interpreter

import info.gianlucacosta.chronos.ast._
import info.gianlucacosta.chronos.ast.expressions._
import info.gianlucacosta.chronos.ast.expressions.ground._
import info.gianlucacosta.chronos.ast.statements._
import info.gianlucacosta.chronos.interpreter.atoms._
import info.gianlucacosta.chronos.interpreter.exceptions._
import info.gianlucacosta.chronos.interpreter.queues.{FifoQueue, LifoQueue, SortedQueue}


private class ExecutingVisitor(
                                val simulationContext: SimulationContext,
                                val input: Input,
                                val output: Output
                                ) extends AstVisitor[Any] {

  override def visit(node: Node) =
    node.accept(this)


  @throws[InterruptedException]
  override def visit(node: Program): Unit = {
    setupSimulation(node)

    mainLoop()

    simulationContext.onSimulationEnded()
  }


  private def setupSimulation(programNode: Program): Unit = {
    visit(programNode.globalStatements)

    programNode.events.foreach(visit)
    programNode.procedures.foreach(visit)


    val startEvent = programNode.startEvent

    val startEventNotice =
      startEvent.map(event => new EventNotice(event.name, event))
        .getOrElse(throw new ExecutionException("Cannot find the program's start event"))

    executeEventNotice(startEventNotice)
  }


  private def mainLoop(): Unit = {
    while (simulationContext.hasEventNotices) {
      val eventNotices = simulationContext.fetchNextEventNotices()

      eventNotices.foreach(executeEventNotice)
    }
  }


  override def visit(node: DisableHeapCheck): Unit = {
    simulationContext.disableHeapCheck()
  }


  override def visit(node: Event): Unit = {
    simulationContext.registerEvent(node)
  }


  override def visit(node: Procedure): Unit = {
    simulationContext.registerProcedure(node)
  }


  private def executeEventNotice(eventNotice: EventNotice): Unit = {
    val event = eventNotice.event

    simulationContext.pushNewFrame()
    simulationContext.setReference(new RuntimeReference(event.name), eventNotice)

    visit(event.block)

    simulationContext.popCurrentFrame()
  }


  override def visit(node: Block): Unit = {
    node.statements.takeWhile(statement => {
      visit(statement)

      simulationContext.canRunProgram && simulationContext.canRunCurrentFrame
    })
  }


  override def visit(statement: Statement): Unit = {
    if (Thread.interrupted()) {
      throw new InterruptedException
    }

    try {
      statement.accept(this)
    } catch {
      case e: InterruptedException => throw e
      case e: Exception => throw new StatementFailedException(statement, e)
    }
  }


  override def visit(node: Return): Unit = {
    simulationContext.terminateCurrentFrame()
  }


  override def visit(node: Exit): Unit = {
    simulationContext.terminateProgram()
  }


  override def visit(node: Print): Unit = {
    val outputAtom = visit(node.expression)

    output.print(outputAtom)
  }


  override def visit(node: Println): Unit = {
    val outputAtom = visit(node.expression)

    output.println(outputAtom)
  }


  override def visit(node: AssignLocalReference): Unit = {
    val identifier = node.reference.identifier
    val parameterAtom = node.reference.parameter.map(visit)
    val valueAtom = visit(node.value)

    val reference = new RuntimeReference(identifier, parameterAtom)

    simulationContext.setReference(reference, valueAtom)
  }


  override def visit(node: AssignGlobalReference): Unit = {
    val identifier = node.reference.identifier
    val parameterAtom = node.reference.parameter.map(visit)
    val valueAtom = visit(node.value)

    val reference = new RuntimeReference(identifier, parameterAtom)


    simulationContext.setGlobalReference(reference, valueAtom)
  }


  override def visit(node: Call): Unit = {
    val procedure = simulationContext.getProcedure(node.procedureName)
    val arguments = node.arguments

    if (arguments.size != procedure.params.size) {
      throw new IllegalCallException(procedure, arguments)
    }

    val boundParams = procedure.params zip arguments.map(visit)


    simulationContext.pushNewFrame()

    boundParams.foreach(boundParam => {
      val paramName = boundParam._1
      val argumentValue = boundParam._2
      simulationContext.setReference(new RuntimeReference(paramName), argumentValue)
    })

    visit(procedure.block)

    simulationContext.popCurrentFrame()
  }


  override def visit(node: Assert): Unit = {
    val assertedAtom = visit(node.expression)

    assertedAtom.assert()
  }


  override def visit(node: CreateMap): Unit = {
    val identifier = node.identifier

    val atomMap = new AtomMap

    simulationContext.setGlobalReference(
      new RuntimeReference(identifier),
      atomMap
    )
  }


  override def visit(node: CreateEntity): Unit = {
    simulationContext.createEntity(
      node.entityType,
      node.reference.map(visit)
    )
  }


  override def visit(node: DestroyEntity): Unit = {
    simulationContext.destroyEntity(
      visit(node.reference)
    )
  }


  override def visit(node: ReadDouble): Unit = {
    val prompt = visit(node.prompt).toStringAtom.value
    val inputValue = input.readDouble(prompt)

    val reference = visit(node.reference)

    simulationContext.setReference(reference, inputValue)
  }


  override def visit(node: ReadInt): Unit = {
    val prompt = visit(node.prompt).toStringAtom.value
    val inputValue = input.readInt(prompt)

    val reference = visit(node.reference)

    simulationContext.setReference(reference, inputValue)
  }


  override def visit(node: ReadBoolean): Unit = {
    val prompt = visit(node.prompt).toStringAtom.value
    val inputValue = input.readBoolean(prompt)

    val reference = visit(node.reference)

    simulationContext.setReference(reference, inputValue)
  }


  override def visit(node: ReadString): Unit = {
    val prompt = visit(node.prompt).toStringAtom.value
    val inputValue = input.readString(prompt)

    val reference = visit(node.reference)

    simulationContext.setReference(reference, inputValue)
  }


  override def visit(node: CreateFifoQueue): Unit = {
    simulationContext.addQueue(
      node.queueName,
      new FifoQueue
    )
  }


  override def visit(node: CreateLifoQueue): Unit = {
    simulationContext.addQueue(
      node.queueName,
      new LifoQueue
    )
  }


  override def visit(node: CreateSortedQueue): Unit = {
    simulationContext.addQueue(
      node.queueName,
      new SortedQueue(node.propertyName, node.sortOrder)
    )
  }


  override def visit(node: Enqueue): Unit = {
    val queue = simulationContext.getQueue(node.queueName)
    val reference = visit(node.reference)
    val resolvedAtom = simulationContext.resolveReference(reference)


    resolvedAtom match {
      case entity: Entity => queue.enqueue(entity)

      case _ => throw new QueueException(s"Only entities can be inserted into queues, not '${resolvedAtom}'")
    }
  }


  override def visit(node: Remove): Unit = {
    val queue = simulationContext.getQueue(node.queueName)
    val reference = visit(node.reference)
    val resolvedAtom = simulationContext.resolveReference(reference)

    resolvedAtom match {
      case entity: Entity => queue.remove(entity)

      case _ => throw new QueueException(s"Only entities can be removed from queues, not '${resolvedAtom}'")
    }
  }


  override def visit(node: ScheduleAt): Unit = {
    val eventName = node.eventName
    val instantAtom = visit(node.instant)


    val eventNoticeReference = node.eventNoticeReference
      .map(visit)
      .getOrElse(new RuntimeReference(eventName))

    simulationContext.scheduleAt(eventName, eventNoticeReference, instantAtom)
  }


  override def visit(node: ScheduleAfter): Unit = {
    val eventName = node.eventName
    val delayAtom = visit(node.delay)
    val instantAtom = delayAtom + DoubleAtom(simulationContext.currentTime)



    val eventNoticeReference = node.eventNoticeReference
      .map(visit)
      .getOrElse(new RuntimeReference(eventName))

    simulationContext.scheduleAt(eventName, eventNoticeReference, instantAtom)
  }


  override def visit(node: Cancel): Unit = {
    val eventName = node.eventName

    val eventNoticeReference = node.eventNoticeReference
      .map(visit)
      .getOrElse(new RuntimeReference(eventName))

    simulationContext.cancel(eventName, eventNoticeReference)
  }


  override def visit(node: If): Unit = {
    val conditionAtom = visit(node.condition).toBooleanAtom

    if (conditionAtom.value) {
      visit(node.thenPart)
    } else {
      node.elsePart.foreach(visit)
    }
  }


  override def visit(node: While): Unit = {
    var conditionAtom = visit(node.condition).toBooleanAtom

    while (conditionAtom.value) {
      if (conditionAtom.value) {
        visit(node.block)
        conditionAtom = visit(node.condition).toBooleanAtom
      }
    }
  }


  override def visit(node: SetRandomSeed): Any = {
    val randomSeed = visit(node.randomSeed)

    randomSeed match {
      case DoubleAtom(doubleSeed) => simulationContext.setRandomSeed(doubleSeed)

      case IntAtom(intSeed) => simulationContext.setRandomSeed(intSeed)

      case _ => throw new ExecutionException(s"Invalid random seed: '${randomSeed}'")
    }
  }


  /*
   * EXPRESSIONS
   */

  override def visit(expression: Expression): Atom = {
    expression.accept(this).asInstanceOf[Atom]
  }

  override def visit(expression: Or): Atom = {
    val operands = expression.operands.map(visit)

    (operands.head /: operands.tail)(_ || _)
  }


  override def visit(expression: And): Atom = {
    val operands = expression.operands.map(visit)

    (operands.head /: operands.tail)(_ && _)
  }


  override def visit(expression: Not): Atom = {
    val operand = visit(expression.operand)

    !operand
  }


  override def visit(expression: Comparison): Atom = {
    val leftAtom = visit(expression.left)
    val rightAtom = visit(expression.right)

    val result = expression.operator match {
      case ComparisonOperator.Equal =>
        (leftAtom compare rightAtom) == 0

      case ComparisonOperator.NotEqual =>
        (leftAtom compare rightAtom) != 0

      case ComparisonOperator.Greater =>
        leftAtom > rightAtom

      case ComparisonOperator.GreaterEqual =>
        leftAtom >= rightAtom


      case ComparisonOperator.Less =>
        leftAtom < rightAtom


      case ComparisonOperator.LessEqual =>
        leftAtom <= rightAtom
    }

    BooleanAtom(result)
  }


  override def visit(expression: AlgebraicSum): Atom = {
    val addendAtoms = expression.addends.map(visit)

    (addendAtoms.head /: addendAtoms.tail)(_ + _)
  }


  override def visit(expression: UnaryMinus): Atom = {
    val originalExpressionAtom = visit(expression.operand)

    -originalExpressionAtom
  }


  override def visit(expression: Multiplication): Atom = {
    val leftAtom = visit(expression.left)
    val rightAtom = visit(expression.right)

    leftAtom * rightAtom
  }


  override def visit(expression: Division): Atom = {
    val leftAtom = visit(expression.left)
    val rightAtom = visit(expression.right)

    leftAtom / rightAtom
  }


  override def visit(expression: ReferenceValue): Atom = {
    val reference = visit(expression.reference)

    simulationContext.resolveReference(reference)
  }


  override def visit(expression: Dequeue): Entity = {
    val queueName = expression.queueName
    val queue = simulationContext.getQueue(queueName)

    if (queue.isEmpty) {
      throw new QueueException(s"Queue '${queueName}' is empty")
    }

    queue.dequeue()
  }


  override def visit(expression: CastToDouble): DoubleAtom = {
    val sourceAtom = visit(expression.expression)

    sourceAtom.toDoubleAtom
  }


  override def visit(expression: CastToInt): IntAtom = {
    val sourceAtom = visit(expression.expression)

    sourceAtom.toIntAtom
  }


  override def visit(expression: CastToBoolean): BooleanAtom = {
    val sourceAtom = visit(expression.expression)

    sourceAtom.toBooleanAtom
  }


  override def visit(expression: CastToString): StringAtom = {
    val sourceAtom = visit(expression.expression)

    sourceAtom.toStringAtom
  }


  override def visit(expression: Floor): IntAtom = {
    val sourceAtom = visit(expression.expression)

    sourceAtom.getFloor
  }


  override def visit(expression: Ceil): IntAtom = {
    val sourceAtom = visit(expression.expression)

    sourceAtom.getCeil
  }


  override def visit(expression: IsEmpty): BooleanAtom = {
    val queue = simulationContext.getQueue(expression.queueName)

    BooleanAtom(queue.isEmpty)
  }


  override def visit(expression: UniformRandom): DoubleAtom = {
    val minValue = visit(expression.minValue).toDoubleAtom.value
    val maxValue = visit(expression.maxValue).toDoubleAtom.value

    val randomBase = simulationContext.nextRandomDouble()

    DoubleAtom(minValue + randomBase * (maxValue - minValue))
  }


  override def visit(expression: UniformIntRandom): IntAtom = {
    val minValue = visit(expression.minValue).toIntAtom.value
    val maxValue = visit(expression.maxValue).toIntAtom.value

    val randomBase = simulationContext.nextRandomDouble()

    IntAtom(math.floor(minValue + randomBase * (maxValue - minValue + 1)).toInt)
  }

  override def visit(expression: ExponentialRandom): DoubleAtom = {
    val averageValue = visit(expression.averageValue).toDoubleAtom.value

    val randomSalt = simulationContext.nextRandomDouble()
    DoubleAtom(-averageValue * math.log(1 - randomSalt))
  }


  override def visit(expression: DoubleTerm): DoubleAtom = {
    DoubleAtom(expression.value)
  }


  override def visit(expression: IntTerm): IntAtom = {
    IntAtom(expression.value)
  }


  override def visit(expression: BooleanTerm): BooleanAtom = {
    BooleanAtom(expression.value)
  }

  override def visit(expression: StringTerm): StringAtom = {
    StringAtom(expression.value)
  }

  override def visit(expression: PlusInfTerm): InfinityAtom = {
    PlusInfAtom
  }


  override def visit(expression: MinusInfTerm): InfinityAtom = {
    MinusInfAtom
  }

  override def visit(expression: Now): DoubleAtom = {
    DoubleAtom(simulationContext.currentTime)
  }


  override def visit(expression: Condition): Atom = {
    if (Thread.interrupted()) {
      throw new InterruptedException
    }

    visit(expression.expression)
  }


  override def visit(reference: Reference): RuntimeReference = {
    val identifier = reference.identifier
    val parameter = reference.parameter.map(visit)

    new RuntimeReference(identifier, parameter)
  }
}
