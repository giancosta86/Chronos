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

package info.gianlucacosta.chronos.interpreter.heap

import info.gianlucacosta.chronos.interpreter.atoms.HeapAtom
import info.gianlucacosta.chronos.interpreter.exceptions.{HeapException, MissedDeallocationException}

import scala.collection.mutable

private[interpreter] class ActiveHeap extends Heap {
  private val allocatedAtoms = new mutable.HashSet[HeapAtom]()


  def assertAtomValidity(atom: HeapAtom) {
    if (!allocatedAtoms.contains(atom)) {
      throw new HeapException(s"${atom} has already been deallocated")
    }
  }


  override def assertEmpty(): Unit = {
    if (allocatedAtoms.nonEmpty) {
      throw new MissedDeallocationException(allocatedAtoms)
    }
  }

  def allocate(atom: HeapAtom): Unit = {
    allocatedAtoms += atom
  }


  def deallocate(atom: HeapAtom): Unit = {
    allocatedAtoms -= atom
  }

  override def isEmpty: Boolean = allocatedAtoms.isEmpty
}
