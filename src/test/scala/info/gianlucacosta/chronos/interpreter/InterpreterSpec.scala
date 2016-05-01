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

package info.gianlucacosta.chronos.interpreter

import info.gianlucacosta.chronos.ast.exceptions.{DuplicateEventException, DuplicateParameterException, DuplicateProcedureException}
import info.gianlucacosta.chronos.ast.{Block, Event}
import info.gianlucacosta.chronos.interpreter.atoms.{Entity, EventNotice}
import info.gianlucacosta.chronos.interpreter.exceptions._
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class InterpreterSpec extends FlatSpec with Matchers {


  "HelloWorld" should "run" in {
    runProgram("hello.chronos", Seq(
      "Hello, world! ^__^"
    ))
  }


  "Arithmetic expressions" should "be correctly evaluated" in {
    runProgram("arithmeticExpressions.chronos", Seq(
      "9",
      "6",
      "12",
      "4",
      "43",

      "4",
      "4.5",

      "22.9",

      "1.7",
      "0.2"
    ))
  }


  "Infinity values" should "work as expected" in {
    runProgram("infOperations.chronos")
  }


  "Boolean expressions" should "be correctly evaluated" in {
    runProgram("booleanExpressions.chronos", Seq(
      "true",
      "false",

      "true",
      "true",
      "false",

      "true",
      "false",


      "true",
      "false",
      "false",

      "true",
      "false",

      "true",
      "true",
      "false",

      "true",
      "true"
    ))
  }


  "Assertion" should "crash the program on failure" in {
    runProgramWithFailingStatement[AssertionFailedException]("assertion.chronos")
  }


  "Variables" should "be correctly set and read" in {
    runProgram("variables.chronos", Seq(
      "90",

      "11.3",

      "true",

      "The result is: 286"
    ))
  }


  "Maps" should "work" in {
    runProgram("maps.chronos")
  }


  "Entities" should "work" in {
    runProgram("entities.chronos")
  }

  "Destroying an entity twice" should "fail" in {
    runProgramWithFailingStatement[IdentifierNotFoundException]("destroyingTwice.chronos")
  }


  "Lingering entities" should "cause an exception" in {
    val exception = intercept[MissedDeallocationException] {
      runProgram("missedDeallocation.chronos")
    }

    val entityInfoSet = exception.heapAtoms.map(heapAtom => {
      val heapEntity = heapAtom.asInstanceOf[Entity]
      (heapEntity.name, heapEntity.entityType)
    }).toSet

    entityInfoSet should be(Set(
      ("car", "car"),
      ("x", "book")
    ))
  }


  "Lingering entities" should "NOT raise an exception when the heap check is disabled" in {
    runProgram("disableHeapCheck.chronos")
  }


  "Procedures" should "be correctly called" in {
    runProgram("procedures.chronos", Seq(
      "Your result is: 17"
    ))
  }


  "Return and exit" should "work" in {
    runProgram("returnExit.chronos", Seq(
      "Alpha",
      "Beta",
      "Gamma"
    ))
  }


  "Read" should "work" in {
    runProgram(
      "userInput.chronos",

      new TestInput(
        Seq(9.8),
        Seq(11),
        Seq(true),
        Seq("Just a test")
      ),

      Seq(
        "The result is: 20.8",
        "true",
        "Just a test"
      ))
  }



  "Casts" should "work" in {
    runProgram("casts.chronos")
  }


  "If-then-else" should "work" in {
    runProgram("if.chronos", Seq(
      "Omicron",
      "Ro",
      "Sigma",
      "Tau",
      "Omega"
    ))
  }

  "While" should "work" in {
    runProgram("while.chronos", Seq(
      "X is now: 1",
      "X is now: 2",
      "X is now: 3",
      "X is now: 4",
      "13 is the value of Y",
      "12 is the value of Y",
      "11 is the value of Y"
    ))
  }



  "Queues" should "work" in {
    runProgram("queues.chronos")
  }


  "Sorted queues" should "work as expected" in {
    runProgramWithFailingStatement[PropertyNotFoundException]("sortedQueueAdvanced.chronos")
  }

  "Random functions" should "return values within bounds" in {
    runProgram("random.chronos")
  }


  "UniformIntRandom" should "return all discrete values in the range" in {
    runProgram("uniformIntRandom.chronos")
  }


  "The random seed" should "affect randomizing functions" in {
    runProgram("randomSeed.chronos")
  }


  "Global variables" should "work" in {
    runProgram("globals.chronos")
  }

  "Basic events example" should "work" in {
    runProgram("eventsBasic.chronos", Seq(
      "Hello, world! ^__^",
      "Hello, Scala! ^__^"
    ))
  }


  "Advanced events example" should "work" in {
    runProgram("eventsAdvanced.chronos", Seq(
      "In main",
      "X = 50",
      "In beta!",
      "X = 60",
      "X = 600"
    ))
  }

  "Rescheduling an event notice" should "work" in {
    runProgram("rescheduling.chronos", Seq(
      "0",
      "10",
      "20"
    ))
  }

  "Scheduling an event notice for the past" should "fail" in {
    runProgramWithFailingStatement[SchedulingException]("schedulingForThePast.chronos")
  }


  "Scheduling an event notice twice" should "fail" in {
    runProgramWithFailingStatement[SchedulingException]("schedulingTwice.chronos")
  }


  "Canceling an unscheduled event notice" should "fail" in {
    runProgramWithFailingStatement[SchedulingException]("cancelingUnscheduled.chronos")
  }


  "Canceling an event notice twice" should "fail" in {
    runProgramWithFailingStatement[SchedulingException]("cancelingTwice.chronos")
  }


  "Scheduling an object which is not an event notice" should "fail" in {
    runProgramWithFailingStatement[SchedulingException]("schedulingAnEntity.chronos")
  }


  "Scheduling an event notice not having the declared event type" should "fail" in {
    runProgramWithFailingStatement[SchedulingException]("schedulingWithWrongType.chronos")
  }



  "A duplicate event" should "raise an exception" in {
    val duplicateException = intercept[DuplicateEventException] {
      runProgram("duplicateEvent.chronos")
    }

    duplicateException.eventName should be("alpha")
  }


  "A duplicateProcedure" should "raise an exception" in {
    val duplicateException = intercept[DuplicateProcedureException] {
      runProgram("duplicateProcedure.chronos")
    }

    duplicateException.procedureName should be("beta")
  }

  "A procedure with a duplicate parameter" should "raise an exception" in {
    val duplicateException = intercept[DuplicateParameterException] {
      runProgram("duplicateProcedureParam.chronos")
    }

    duplicateException.procedureName should be("gamma")
    duplicateException.param should be("x")
  }


  "Entities not having the same memory address" should "NOT be equal" in {
    val entityA = new Entity("test", "testType")
    val entityB = new Entity("test", "testType")

    entityA should be(entityA)
    entityA should not be (entityB)
  }


  "Event notices not having the same memory address" should "NOT be equal" in {
    val event = Event("Test", Block(Nil))

    val noticeA = new EventNotice("test", event)
    val noticeB = new EventNotice("test", event)

    noticeA should be(noticeA)
    noticeA should not be (noticeB)
  }


  "now keyword" should "be legal" in {
    runProgram("now.chronos")
  }

  "println" should "work with and without arguments" in {
    runProgram("println.chronos", Seq(
      "Alpha",
      "",
      "Beta"
    ))
  }


  private def runProgram(
                          programName: String): Unit = {
    val program = TestSourcePool.getProgram(programName)

    val testInput = new TestInput
    val testOutput = new TestOutput
    val interpreter = new Interpreter(testInput, testOutput)

    interpreter.run(program)
  }


  private def runProgram(
                          programName: String,
                          expectedOutputLines: Seq[String]): Unit = runProgram(programName, new TestInput, expectedOutputLines)


  private def runProgram(
                          programName: String,
                          testInput: TestInput,
                          expectedOutputLines: Seq[String]): Unit = {
    val program = TestSourcePool.getProgram(programName)


    val testOutput = new TestOutput
    val interpreter = new Interpreter(testInput, testOutput)

    interpreter.run(program)
    testOutput.outputLines should be(expectedOutputLines)
  }


  private def runProgramWithFailingStatement[ExpectedUnderlyingException: Manifest](programName: String): Unit = {
    val statementException = intercept[StatementFailedException] {
      runProgram(programName)
    }

    val programSource = Source.fromInputStream(TestSourcePool.getProgramStream(programName))

    val failureLineNumbers = programSource.getLines().zipWithIndex.filter {
      case (line, lineIndex) =>
        line.contains("//FAILURE")
    }.map {
      case (line, lineIndex) => lineIndex + 1
    }.toList

    if (failureLineNumbers.size != 1) {
      fail(s"There must be exactly 1 failure line, but ${failureLineNumbers.size} were found")
    }


    statementException.statement.lineNumber should be(failureLineNumbers.head)
    statementException.getCause should be(an[ExpectedUnderlyingException])

  }
}
