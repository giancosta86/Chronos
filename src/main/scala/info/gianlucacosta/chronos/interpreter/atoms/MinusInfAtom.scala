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


case object MinusInfAtom extends InfinityAtom {
  override val sign: Int = -1


  override def unary_-(): Atom = PlusInfAtom

  override def +(right: Atom): Atom =
    right match {
      case DoubleAtom(_) | IntAtom(_) => MinusInfAtom
      case MinusInfAtom => MinusInfAtom
      case StringAtom(rightString) => StringAtom(this.toString + rightString)
      case _ => super.+(right)
    }


  override def -(right: Atom): Atom =
    right match {
      case DoubleAtom(_) | IntAtom(_) => MinusInfAtom
      case PlusInfAtom => MinusInfAtom
      case _ => super.-(right)
    }


  override def compare(right: Atom): Int =
    right match {
      case DoubleAtom(rightDouble: Double) => -1
      case IntAtom(rightInt: Int) => -1
      case PlusInfAtom => -1
      case MinusInfAtom => 0
      case _ => super.compare(right)
    }


  override def toString = "-∞"
}
