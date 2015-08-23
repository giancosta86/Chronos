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


case object PlusInfAtom extends InfinityAtom {
  override val sign: Int = +1


  override def unary_-(): Atom = MinusInfAtom

  override def +(right: Atom): Atom =
    right match {
      case DoubleAtom(_) | IntAtom(_) => PlusInfAtom
      case PlusInfAtom => PlusInfAtom
      case StringAtom(rightString) => StringAtom(this.toString + rightString)
      case _ => super.+(right)
    }


  override def -(right: Atom): Atom =
    right match {
      case DoubleAtom(_) | IntAtom(_) => PlusInfAtom
      case MinusInfAtom => PlusInfAtom
      case _ => super.-(right)

    }


  override def compare(right: Atom): Int =
    right match {
      case DoubleAtom(rightDouble: Double) => +1
      case IntAtom(rightInt: Int) => +1
      case PlusInfAtom => 0
      case MinusInfAtom => +1
      case _ => super.compare(right)
    }


  override def toString = "∞"
}
