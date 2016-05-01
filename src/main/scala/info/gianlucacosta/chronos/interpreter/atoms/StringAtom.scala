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

package info.gianlucacosta.chronos.interpreter.atoms

case object StringAtom {
  val Empty = StringAtom("")
}


case class StringAtom(value: String) extends ImmutableAtom {
  override def +(right: Atom): Atom = StringAtom(value + right.toString)

  override def compare(right: Atom): Int =
    right match {
      case StringAtom(rightString) => value compareTo rightString
      case _ => super.compare(right)
    }


  override def toDoubleAtom: DoubleAtom = DoubleAtom(value.toDouble)

  override def toIntAtom: IntAtom = IntAtom(value.toInt)

  override def toBooleanAtom: BooleanAtom = BooleanAtom(if (value == BooleanAtom.TrueString) true else false)

  override def toStringAtom: StringAtom = this


  override def toString: String = value.toString
}
