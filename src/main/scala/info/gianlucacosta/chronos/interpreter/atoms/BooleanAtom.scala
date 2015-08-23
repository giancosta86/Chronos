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

import info.gianlucacosta.chronos.interpreter.exceptions.AssertionFailedException

case object BooleanAtom {
  val TrueString = "true"
  val FalseString = "false"
}

case class BooleanAtom(value: Boolean) extends ImmutableAtom {

  override def ||(right: Atom): Atom =
    right match {
      case BooleanAtom(rightBoolean) => BooleanAtom(value || rightBoolean)

      case _ => super.||(right)
    }


  override def &&(right: Atom): Atom =
    right match {
      case BooleanAtom(rightBoolean) => BooleanAtom(value && rightBoolean)

      case _ => super.&&(right)
    }


  override def unary_!(): Atom =
    BooleanAtom(!value)


  override def compare(right: Atom): Int =
    right match {
      case BooleanAtom(rightBoolean) => value compareTo rightBoolean

      case _ => super.compare(right)
    }


  override def toDoubleAtom: DoubleAtom = DoubleAtom(if (value) 1 else 0)


  override def toIntAtom: IntAtom = IntAtom(if (value) 1 else 0)

  override def toBooleanAtom: BooleanAtom = this


  override def assert(): Unit = if (!value) {
    throw new AssertionFailedException
  }

  override def toString: String = if (value) BooleanAtom.TrueString else BooleanAtom.FalseString
}
