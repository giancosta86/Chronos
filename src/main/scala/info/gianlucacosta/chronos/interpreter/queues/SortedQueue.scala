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

package info.gianlucacosta.chronos.interpreter.queues

import info.gianlucacosta.chronos.ast.SortOrder
import info.gianlucacosta.chronos.interpreter.atoms.{Entity, StringAtom}

import scala.collection.mutable

private[interpreter] class SortedQueue(propertyName: String, sortOrder: SortOrder.Type) extends EntityQueue {

  private case class QueueItem(entity: Entity) extends Ordered[QueueItem] {
    override def compare(that: QueueItem): Int = {
      val thisPropertyValue = entity(StringAtom(propertyName))
      val thatPropertyValue = that.entity(StringAtom(propertyName))

      val ascendingComparisonResult = thisPropertyValue.compare(thatPropertyValue)

      if (sortOrder == SortOrder.Asc)
        ascendingComparisonResult
      else
        -ascendingComparisonResult
    }
  }

  private val internalSet = mutable.TreeSet.empty[QueueItem]

  override def enqueue(entity: Entity): Unit = {
    internalSet += new QueueItem(entity)
  }

  override def dequeue(): Entity = {
    val resultItem = internalSet.head
    internalSet -= resultItem

    resultItem.entity
  }

  override def remove(entity: Entity): Unit = {
    internalSet.remove(QueueItem(entity))
  }

  override def isEmpty: Boolean = internalSet.isEmpty
}
