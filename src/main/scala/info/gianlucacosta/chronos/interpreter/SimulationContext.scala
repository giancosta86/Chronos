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

import info.gianlucacosta.chronos.ast.{Event, Procedure}
import info.gianlucacosta.chronos.interpreter.atoms._
import info.gianlucacosta.chronos.interpreter.exceptions._
import info.gianlucacosta.chronos.interpreter.heap.{ActiveHeap, DisabledHeap, Heap}
import info.gianlucacosta.chronos.interpreter.queues.EntityQueue

import scala.util.Random


private class SimulationContext {
  private var queues = Map.empty[String, EntityQueue]

  private var events = Map.empty[String, Event]
  private var procedures = Map.empty[String, Procedure]

  private var heap: Heap = new ActiveHeap

  private val globalFrame = new ContextFrame(heap)
  private var frameStack = List(globalFrame)

  private val random = new Random()

  private val timeline = new Timeline


  /*
   * QUEUES
   */

  def addQueue(queueName: String, queue: EntityQueue): Unit = {
    if (queues.contains(queueName)) {
      throw new QueueException(s"Queue '${queueName}' already defined")
    }

    queues += (queueName -> queue)
  }


  def getQueue(queueName: String): EntityQueue = {
    if (!queues.contains(queueName)) {
      throw new QueueException(s"Queue '${queueName}' not found")
    }

    queues(queueName)
  }


  /*
   * GLOBAL ELEMENTS
   */


  def registerEvent(event: Event): Unit = {
    events += (event.name -> event)
  }


  def registerProcedure(procedure: Procedure): Unit = {
    procedures += (procedure.name -> procedure)
  }


  def getProcedure(procedureName: String): Procedure = {
    procedures.getOrElse(
      procedureName,
      throw new ProcedureNotFoundException(procedureName)
    )
  }


  def disableHeapCheck(): Unit = {
    require(heap.isEmpty)
    heap = new DisabledHeap
  }


  /*
   * FRAMES
   */

  private def currentFrame = frameStack.head

  def pushNewFrame(): Unit = {
    val localFrame = new ContextFrame(heap, Some(globalFrame))
    frameStack = localFrame :: frameStack
  }


  def terminateCurrentFrame(): Unit = {
    currentFrame.terminate()
  }


  def popCurrentFrame(): Unit = {
    frameStack = frameStack.tail

    require(frameStack.nonEmpty)
  }


  def terminateProgram(): Unit = {
    globalFrame.terminate()
  }


  def canRunCurrentFrame = currentFrame.canRun

  def canRunProgram = globalFrame.canRun


  /*
   * REFERENCES
   */

  def resolveReference(reference: RuntimeReference): Atom = {
    currentFrame.resolveReference(reference)
  }


  def setReference(reference: RuntimeReference, value: Atom): Unit = {
    currentFrame.setReference(reference, value)
  }


  def setGlobalReference(reference: RuntimeReference, value: Atom): Unit = {
    globalFrame.setReference(reference, value)
  }


  /*
   * HEAP OPERATIONS
   */

  def createEntity(entityType: String, reference: Option[RuntimeReference]): Unit = {
    val entityName = reference.map(_.identifier).getOrElse(entityType)

    val relatedEvent = events.get(entityType)
    val entity =
      relatedEvent.map(new EventNotice(entityName, _))
        .getOrElse(new Entity(entityName, entityType))

    setReference(
      reference.getOrElse(new RuntimeReference(entityName)),
      entity)

    heap.allocate(entity)
  }


  def destroyEntity(reference: RuntimeReference): Unit = {
    val resolvedObject = resolveReference(reference)

    resolvedObject match {
      case resolvedEntity: Entity =>
        heap.deallocate(resolvedEntity)
        currentFrame.removeReference(reference)


      case _ => throw new HeapException(s"Cannot delete object '${resolvedObject}'")
    }
  }


  /*
   * TIMELINE
   */


  def scheduleAt(eventName: String, eventNoticeReference: RuntimeReference, instantAtom: Atom): Unit = {
    val instant = instantAtom match {
      case DoubleAtom(doubleInstant) => doubleInstant
      case IntAtom(intInstant) => intInstant.toDouble
      case _ => throw new SchedulingException(s"Invalid scheduling instant: '${instantAtom}'")
    }


    val resolvedEventNotice = resolveReference(eventNoticeReference)

    resolvedEventNotice match {
      case eventNotice: EventNotice =>
        if (eventNotice.event.name != eventName) {
          throw new SchedulingException(s"The event notice is related to the event '${eventNotice.event.name}', not '${eventName}'")
        }

        timeline.scheduleAt(instant, eventNotice)

      case _ =>
        throw new SchedulingException(s"Only event notices can be schedules, not '${resolvedEventNotice}'")
    }
  }


  def cancel(eventName: String, eventNoticeReference: RuntimeReference): Unit = {
    val resolvedEventNotice = resolveReference(eventNoticeReference)

    resolvedEventNotice match {
      case eventNotice: EventNotice =>
        if (eventNotice.event.name != eventName) {
          throw new SchedulingException(s"The event notice is related to the event '${eventNotice.event.name}', not '${eventName}'")
        }

        timeline.cancel(eventName, eventNotice)
      case _ =>
        throw new SchedulingException(s"Only event notices can be schedules, not '${resolvedEventNotice}'")
    }
  }

  def currentTime = timeline.currentTime

  def hasEventNotices = timeline.hasEventNotices

  def fetchNextEventNotices(): List[EventNotice] = timeline.fetchNextEventNotices()


  def onSimulationEnded(): Unit = {
    heap.assertEmpty()
  }

  /*
   * MISCELLANEOUS
   */

  def setRandomSeed(randomSeed: Double): Unit = {
    random.setSeed(randomSeed.toLong)
  }


  def nextRandomDouble(): Double = {
    random.nextDouble()
  }
}
