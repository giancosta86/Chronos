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

import info.gianlucacosta.chronos.interpreter.atoms.{Atom, Entity, HeapAtom, StringAtom}
import info.gianlucacosta.chronos.interpreter.exceptions.{ExecutionException, IdentifierNotFoundException}
import info.gianlucacosta.chronos.interpreter.heap.HeapView


private class ContextFrame(heapView: HeapView, parentFrame: Option[ContextFrame]) {
  private var variables = Map.empty[String, Atom]
  private var _canRun = true


  def this(heapView: HeapView) = this(heapView, None)

  def canRun = _canRun

  def terminate(): Unit = {
    _canRun = false
  }


  def resolveReference(reference: RuntimeReference): Atom = {
    val identifier = reference.identifier

    reference.parameter match {
      case None =>
        resolveIdentifier(identifier)


      case Some(entity: Entity) =>
        val propertyValue = entity(StringAtom(identifier))
        checkAtomValidity(propertyValue)
        propertyValue


      case Some(key: Atom) =>
        val resolvedMap = resolveIdentifier(identifier)
        val retrievedValue = resolvedMap(key)
        checkAtomValidity(retrievedValue)
        retrievedValue

      case _ => throw new ExecutionException(s"Error while resolving the reference: '${reference}'")
    }
  }


  def setReference(reference: RuntimeReference, value: Atom): Unit = {
    val identifier = reference.identifier

    reference.parameter match {
      case None =>
        setIdentifier(identifier, value)

      case Some(entity: Entity) =>
        entity(StringAtom(identifier)) = value


      case Some(key: Atom) =>
        val resolvedMap = resolveIdentifier(identifier)
        resolvedMap(key) = value

      case _ => throw new ExecutionException(s"Error while resolving the reference: '${reference}'")
    }
  }


  def removeReference(reference: RuntimeReference): Unit = {
    val identifier = reference.identifier

    reference.parameter match {
      case None =>
        removeIdentifier(identifier)

      case Some(entity: Entity) =>
        entity -= StringAtom(identifier)

      case Some(key: Atom) =>
        val resolvedMap = resolveIdentifier(identifier)
        resolvedMap -= key
    }
  }


  private def resolveIdentifier(identifier: String): Atom = {
    val resolvedAtom = variables.get(identifier)

    if (resolvedAtom.isDefined) {
      checkAtomValidity(resolvedAtom.get)
      resolvedAtom.get
    } else if (parentFrame.isDefined) {
      parentFrame.get.resolveIdentifier(identifier)
    } else {
      throw new IdentifierNotFoundException(identifier)
    }
  }


  private def setIdentifier(identifier: String, atom: Atom): Unit = {
    variables += (identifier -> atom)
  }


  private def removeIdentifier(identifier: String): Unit = {
    if (!variables.contains(identifier)) {
      if (parentFrame.isDefined) {
        parentFrame.get.removeIdentifier(identifier)
      } else {
        throw new IdentifierNotFoundException(identifier)
      }
    } else {
      variables -= identifier
    }
  }


  private def checkAtomValidity(atom: Atom): Unit = {
    atom match {
      case heapAtom: HeapAtom =>
        heapView.assertAtomValidity(heapAtom)

      case _ =>
    }
  }
}