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

package info.gianlucacosta.chronos.interpreter.queues

import info.gianlucacosta.chronos.interpreter.atoms.Entity

import scala.collection.mutable.ListBuffer

private[interpreter] class LifoQueue extends EntityQueue {
  private val internalQueue = ListBuffer.empty[Entity]

  override def enqueue(entity: Entity): Unit = {
    internalQueue.insert(0, entity)
  }

  override def dequeue(): Entity = {
    internalQueue.remove(0)
  }

  override def remove(entity: Entity): Unit = {
    internalQueue -= entity
  }

  override def isEmpty: Boolean =
    internalQueue.isEmpty
}