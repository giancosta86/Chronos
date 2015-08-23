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

package info.gianlucacosta.chronos.interpreter.atoms

import info.gianlucacosta.chronos.interpreter.exceptions.{IllegalBinaryOperandsException, IllegalUnaryOperandException}

/**
 * Atoms are the fundamental runtime values manipulated by the virtual machine.
 */
trait Atom extends Ordered[Atom] {
  def ||(right: Atom): Atom = throw new IllegalBinaryOperandsException("or", this, right)

  def &&(right: Atom): Atom = throw new IllegalBinaryOperandsException("and", this, right)

  def unary_!(): Atom = throw new IllegalUnaryOperandException("not", this)


  def unary_-(): Atom = throw new IllegalUnaryOperandException("unary -", this)

  def +(right: Atom): Atom = throw new IllegalBinaryOperandsException("+", this, right)

  def -(right: Atom): Atom = throw new IllegalBinaryOperandsException("-", this, right)

  def *(right: Atom): Atom = throw new IllegalBinaryOperandsException("*", this, right)

  def /(right: Atom): Atom = throw new IllegalBinaryOperandsException("/", this, right)

  def compare(right: Atom): Int = throw new IllegalBinaryOperandsException("comparison", this, right)

  def apply(index: Atom): Atom = throw new IllegalUnaryOperandException("indexing", this)

  def update(index: Atom, value: Atom): Unit = throw new IllegalUnaryOperandException("indexing", this)

  def -=(index: Atom): Unit = throw new IllegalUnaryOperandException("indexing", this)


  def toDoubleAtom: DoubleAtom = throw new IllegalUnaryOperandException("Cast to double", this)

  def toIntAtom: IntAtom = throw new IllegalUnaryOperandException("Cast to int", this)

  def toBooleanAtom: BooleanAtom = throw new IllegalUnaryOperandException("Cast to boolean", this)

  def toStringAtom: StringAtom = StringAtom(this.toString)

  def getFloor: IntAtom = throw new IllegalUnaryOperandException("Floor", this)

  def getCeil: IntAtom = throw new IllegalUnaryOperandException("Ceil", this)

  def assert(): Unit = throw new IllegalUnaryOperandException("Assert", this)
}
