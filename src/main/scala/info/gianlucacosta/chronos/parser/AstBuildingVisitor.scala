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

package info.gianlucacosta.chronos.parser

import info.gianlucacosta.chronos.ast._
import info.gianlucacosta.chronos.ast.expressions._
import info.gianlucacosta.chronos.ast.expressions.ground._
import info.gianlucacosta.chronos.ast.statements._
import info.gianlucacosta.chronos.parser.ChronosParser._
import org.antlr.v4.runtime.tree.{ParseTree, TerminalNode}

import scala.collection.JavaConversions._

/**
 * Low-level visitor, generating an AST from the concrete parsing tree.
 *
 * You can employ it at the end of a custom pipeline of customized ANTLR classes;
 * for more basic usage, please consider BasicAstBuilder.
 */
class AstBuildingVisitor extends ChronosBaseVisitor[Node] {

  override def visitProgram(ctx: ProgramContext): Program = {
    val globalStatements = Block(
      ctx.globalStatement.map(visitGlobalStatement)
    )

    val events = ctx.event().map(visitEvent)

    val procedures = ctx.procedure().map(visitProcedure)

    Program(globalStatements, events, procedures)
  }


  override def visitGlobalStatement(ctx: GlobalStatementContext): Statement = {
    visitContextAs[Statement](ctx.getChild(0), classOf[Statement])
  }


  private def visitContextAs[T](parseTree: ParseTree, resultClass: Class[T]): T = {
    val visitResult = parseTree.accept(this)

    if (!resultClass.isInstance(visitResult)) {
      throw new IllegalArgumentException(s"Parsing interval: '${parseTree.getText}' The visit returned: '${visitResult}', not '${resultClass.getSimpleName}'")
    }

    visitResult.asInstanceOf[T]
  }


  override def visitEvent(ctx: EventContext): Event = {
    val eventName = ctx.IDENTIFIER().getText
    val block = visitBlock(ctx.block())

    Event(eventName, block)
  }


  override def visitProcedure(ctx: ProcedureContext): Procedure = {
    val procedureName = ctx.IDENTIFIER().getText
    val parameters = ctx.param().map(_.IDENTIFIER().getText)
    val block = visitBlock(ctx.block())

    Procedure(procedureName, parameters, block)
  }


  /*
   * GLOBAL DIRECTIVES
   */

  override def visitDisableHeapCheck(ctx: DisableHeapCheckContext): DisableHeapCheck = {
    DisableHeapCheck(ctx.getStart.getLine)
  }


  /*
   * STATEMENT PARSING
   */


  override def visitLocalStatement(ctx: LocalStatementContext): Statement = {
    visitContextAs[Statement](ctx.getChild(0), classOf[Statement])
  }


  override def visitPrint(ctx: PrintContext): Print = {
    val expression = visitExpression(ctx.expression())

    Print(expression, ctx.getStart.getLine)
  }


  override def visitPrintln(ctx: PrintlnContext): Println = {
    val expression = visitExpression(ctx.expression())

    Println(expression, ctx.getStart.getLine)
  }


  override def visitLocalAssignment(ctx: LocalAssignmentContext): AssignLocalReference = {
    visitAssignment(ctx.assignment())
  }


  override def visitImplicitGlobalAssignment(ctx: ImplicitGlobalAssignmentContext): AssignLocalReference = {
    visitAssignment(ctx.assignment())
  }


  override def visitAssignment(assignmentContext: AssignmentContext): AssignLocalReference = {
    val reference = visitReference(assignmentContext.reference())
    val valueExpression = visitExpression(assignmentContext.expression())

    AssignLocalReference(
      reference,
      valueExpression,
      assignmentContext.getStart.getLine)
  }


  override def visitGlobalAssignment(ctx: GlobalAssignmentContext): AssignGlobalReference = {
    val assignmentContext = ctx.assignment()

    val reference = visitReference(ctx.assignment().reference())
    val valueExpression = visitExpression(assignmentContext.expression())

    AssignGlobalReference(
      reference,
      valueExpression,
      ctx.getStart.getLine)
  }


  override def visitAssertion(ctx: AssertionContext): Assert = {
    val expression = visitExpression(ctx.expression())

    Assert(expression, ctx.getStart.getLine)
  }


  override def visitCreateMap(ctx: CreateMapContext): CreateMap = {
    val mapName = ctx.IDENTIFIER().getText

    CreateMap(mapName, ctx.getStart.getLine)
  }


  override def visitCreateEntity(ctx: CreateEntityContext): CreateEntity = {
    val entityType = ctx.IDENTIFIER().getText

    val reference = if (ctx.reference() != null) {
      Some(visitReference(ctx.reference()))
    } else {
      None
    }

    CreateEntity(entityType, reference, ctx.getStart.getLine)
  }


  override def visitDestroyEntity(ctx: DestroyEntityContext): DestroyEntity = {
    val reference = visitReference(ctx.reference())

    DestroyEntity(reference, ctx.getStart.getLine)
  }


  override def visitReturnStatement(ctx: ReturnStatementContext): Return = {
    Return(ctx.getStart.getLine)
  }


  override def visitExit(ctx: ExitContext): Exit = {
    Exit(ctx.getStart.getLine)
  }


  override def visitReadDouble(ctx: ReadDoubleContext): ReadDouble = {
    val prompt = visitExpression(ctx.prompt().expression());
    val reference = visitReference(ctx.reference())
    ReadDouble(prompt, reference, ctx.getStart.getLine)
  }

  override def visitReadInt(ctx: ReadIntContext): ReadInt = {
    val prompt = visitExpression(ctx.prompt().expression());
    val reference = visitReference(ctx.reference())
    ReadInt(prompt, reference, ctx.getStart.getLine)
  }

  override def visitReadBoolean(ctx: ReadBooleanContext): ReadBoolean = {
    val prompt = visitExpression(ctx.prompt().expression());
    val reference = visitReference(ctx.reference())
    ReadBoolean(prompt, reference, ctx.getStart.getLine)
  }

  override def visitReadString(ctx: ReadStringContext): ReadString = {
    val prompt = visitExpression(ctx.prompt().expression());
    val reference = visitReference(ctx.reference())
    ReadString(prompt, reference, ctx.getStart.getLine)
  }


  override def visitCreateQueue(ctx: CreateQueueContext): Statement = {
    val queueName = ctx.IDENTIFIER(0).getText

    if (ctx.FIFO() != null) {
      CreateFifoQueue(queueName, ctx.getStart.getLine)
    } else if (ctx.LIFO() != null) {
      CreateLifoQueue(queueName, ctx.getStart.getLine)
    } else {
      val propertyName = ctx.IDENTIFIER(1).getText

      val sortOrder =
        if (ctx.DESC() != null)
          SortOrder.Desc
        else
          SortOrder.Asc

      CreateSortedQueue(queueName, propertyName, sortOrder, ctx.getStart.getLine)
    }
  }


  override def visitBlock(ctx: BlockContext): Block = {
    val statements = ctx.localStatement().map(visitLocalStatement)
    Block(statements)
  }


  override def visitIfStatement(ctx: IfStatementContext): If = {
    val condition = visitCondition(ctx.condition())

    val thenPart = visitBlock(ctx.thenPart().block())
    val elsePart =
      if (ctx.elsePart() != null)
        Some(visitElsePart(ctx.elsePart()))
      else
        None


    If(condition, thenPart, elsePart, ctx.getStart.getLine)
  }


  override def visitCondition(ctx: ConditionContext): Condition = {
    val expression = visitExpression(ctx.expression())

    Condition(expression)
  }

  override def visitElsePart(ctx: ElsePartContext): Node = {
    if (ctx.block() != null) {
      visitBlock(ctx.block())
    } else if (ctx.ifStatement() != null) {
      visitIfStatement(ctx.ifStatement())
    } else {
      throw new IllegalStateException("Unknown else part")
    }
  }


  override def visitWhileStatement(ctx: WhileStatementContext): While = {
    val condition = visitCondition(ctx.condition())

    val block = visitBlock(ctx.block())

    While(condition, block, ctx.getStart.getLine)
  }


  override def visitCall(ctx: CallContext): Call = {
    val procedureName = ctx.IDENTIFIER().getText

    val arguments = ctx.argument().map(argument => visitExpression(argument.expression()))

    Call(procedureName, arguments, ctx.getStart.getLine)
  }


  override def visitEnqueue(ctx: EnqueueContext): Enqueue = {
    val queueName = ctx.IDENTIFIER().getText

    val reference = visitReference(ctx.reference())

    Enqueue(queueName, reference, ctx.getStart.getLine)
  }


  override def visitRemove(ctx: RemoveContext): Remove = {
    val queueName = ctx.IDENTIFIER().getText
    val reference = visitReference(ctx.reference())

    Remove(queueName, reference, ctx.getStart.getLine)
  }


  override def visitSchedule(ctx: ScheduleContext): Statement = {
    val eventName = ctx.IDENTIFIER().getText

    val eventNoticeReference = if (ctx.reference() != null) {
      Some(visitReference(ctx.reference()))
    } else {
      None
    }

    val timeExpression = visitExpression(ctx.expression())

    if (ctx.AT() != null) {
      ScheduleAt(eventName, eventNoticeReference, timeExpression, ctx.getStart.getLine)
    } else if (ctx.AFTER() != null) {
      ScheduleAfter(eventName, eventNoticeReference, timeExpression, ctx.getStart.getLine)
    } else {
      throw new IllegalStateException
    }
  }

  override def visitCancel(ctx: CancelContext): Cancel = {
    val eventName = ctx.IDENTIFIER().getText

    val eventNoticeReference = if (ctx.reference() != null) {
      Some(visitReference(ctx.reference()))
    } else {
      None
    }

    Cancel(eventName, eventNoticeReference, ctx.getStart.getLine)
  }


  override def visitSetRandomSeed(ctx: SetRandomSeedContext): SetRandomSeed = {
    val expression = visitExpression(ctx.expression())

    SetRandomSeed(expression, ctx.getStart.getLine)
  }


  /*
   * EXPRESSION PARSING
   */


  override def visitExpression(ctx: ExpressionContext): Expression =
    visitContextAs(ctx.getChild(0), classOf[Expression])


  override def visitOrOperation(ctx: OrOperationContext): Expression = {
    val operands = ctx.andOperation().map(visitAndOperation)

    if (operands.size >= 2) {
      Or(operands)
    } else {
      operands.head
    }
  }


  override def visitAndOperation(ctx: AndOperationContext): Expression = {
    val operands = ctx.logicLiteral().map(visitLogicLiteral)

    if (operands.size >= 2) {
      And(operands)
    } else {
      operands.head
    }
  }


  override def visitLogicLiteral(ctx: LogicLiteralContext): Expression = {
    val target = visitComparison(ctx.comparison())

    if (ctx.OP_NOT() != null) {
      Not(target)
    } else {
      target
    }
  }


  override def visitComparison(ctx: ComparisonContext): Expression = {
    val leftExpression = visitAlgebraicSum(ctx.algebraicSum(0))

    if (ctx.comparisonOperator() != null) {
      val rightExpression = visitAlgebraicSum(ctx.algebraicSum(1))

      val comparisonOperator = ctx.comparisonOperator().getText match {
        case "=" => ComparisonOperator.Equal
        case "!=" => ComparisonOperator.NotEqual
        case ">" => ComparisonOperator.Greater
        case ">=" => ComparisonOperator.GreaterEqual
        case "<" => ComparisonOperator.Less
        case "<=" => ComparisonOperator.LessEqual
      }

      Comparison(leftExpression, comparisonOperator, rightExpression)
    } else {
      leftExpression
    }
  }


  override def visitAlgebraicSum(ctx: AlgebraicSumContext): Expression = {
    val firstAddendModulus = visitAlgebraicProduct(ctx.firstAddend().algebraicProduct())
    val firstAddend = if (ctx.firstAddend().OP_MINUS() != null) UnaryMinus(firstAddendModulus) else firstAddendModulus

    val addends = ctx.additionalAddend().map { addendContext =>
      val rightModulus = visitAlgebraicProduct(addendContext.algebraicProduct())

      if (addendContext.OP_MINUS() != null) {
        UnaryMinus(rightModulus)
      } else {
        rightModulus
      }
    }

    if (addends.nonEmpty) {
      addends.insert(0, firstAddend)
      AlgebraicSum(addends)
    } else {
      firstAddend
    }
  }


  override def visitAlgebraicProduct(ctx: AlgebraicProductContext): Expression = {
    val firstTerm: Expression = visitTerm(ctx.firstFactor().term())

    (firstTerm /: ctx.additionalFactor()) { (leftTerm, rightContext) =>
      val rightTerm = visitTerm(rightContext.term())
      val operator = rightContext.additionalFactorOperator().getText

      operator match {
        case "*" => Multiplication(leftTerm, rightTerm)
        case "/" => Division(leftTerm, rightTerm)
      }
    }
  }


  override def visitTerm(ctx: TermContext): Expression =
    visitContextAs[Expression](ctx.getChild(0), classOf[Expression])


  override def visitPriorityExpression(ctx: PriorityExpressionContext): Expression = {
    visitExpression(ctx.expression())
  }


  override def visitReference(ctx: ReferenceContext): Reference = {
    val identifier = ctx.IDENTIFIER().getText

    val parameterExpression = if (ctx.expression() != null) {
      Some(visitExpression(ctx.expression()))
    } else {
      None
    }

    Reference(identifier, parameterExpression)
  }


  override def visitReferenceValue(ctx: ReferenceValueContext): ReferenceValue = {
    ReferenceValue(visitReference(ctx.reference()))
  }


  override def visitDequeue(ctx: DequeueContext): Dequeue = {
    Dequeue(ctx.IDENTIFIER().getText)
  }


  override def visitFloor(ctx: FloorContext): Floor = {
    Floor(visitExpression(ctx.expression()))
  }


  override def visitCeil(ctx: CeilContext): Ceil = {
    Ceil(visitExpression(ctx.expression()))
  }

  override def visitEmptyCheck(ctx: EmptyCheckContext): IsEmpty = {

    IsEmpty(ctx.IDENTIFIER().getText)
  }

  override def visitUniformRandom(ctx: UniformRandomContext): UniformRandom = {
    val minValue = visitExpression(ctx.expression(0))
    val maxValue = visitExpression(ctx.expression(1))


    UniformRandom(minValue, maxValue)
  }


  override def visitUniformIntRandom(ctx: UniformIntRandomContext): Node = {
    val minValue = visitExpression(ctx.expression(0))
    val maxValue = visitExpression(ctx.expression(1))

    UniformIntRandom(minValue, maxValue)
  }

  override def visitExponentialRandom(ctx: ExponentialRandomContext): ExponentialRandom = {
    val averageValue = visitExpression(ctx.expression())

    ExponentialRandom(averageValue)
  }


  override def visitCastToDouble(ctx: CastToDoubleContext): CastToDouble = {
    CastToDouble(visitExpression(ctx.expression()))
  }


  override def visitCastToInt(ctx: CastToIntContext): CastToInt = {
    CastToInt(visitExpression(ctx.expression()))
  }


  override def visitCastToBoolean(ctx: CastToBooleanContext): CastToBoolean = {
    CastToBoolean(visitExpression(ctx.expression()))
  }


  override def visitCastToString(ctx: CastToStringContext): CastToString = {
    CastToString(visitExpression(ctx.expression()))
  }


  /*
   * GROUND TERMS
   */

  override def visitNumber(ctx: NumberContext): Expression = {
    val numberText = ctx.NUMBER().getText

    if (numberText.contains(".")) {
      DoubleTerm(numberText.toDouble)
    } else {
      IntTerm(numberText.toInt)
    }
  }


  override def visitPlusInf(ctx: PlusInfContext): PlusInfTerm = {
    PlusInfTerm
  }


  override def visitMinusInf(ctx: MinusInfContext): MinusInfTerm = {
    MinusInfTerm
  }


  override def visitTrueValue(ctx: TrueValueContext): BooleanTerm = {
    BooleanTerm(true)
  }


  override def visitFalseValue(ctx: FalseValueContext): BooleanTerm = {
    BooleanTerm(false)
  }


  override def visitStringValue(ctx: StringValueContext): StringTerm = {
    val stringValue = parseStringValue(ctx.STRING())

    new StringTerm(stringValue)
  }


  private def parseStringValue(stringNode: TerminalNode): String = {
    val stringWithDelimiters = stringNode.getText

    stringWithDelimiters.substring(1, stringWithDelimiters.length - 1)
  }

  override def visitNow(ctx: NowContext): Now = {
    Now
  }
}
