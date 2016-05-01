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

object InfinityAtom {
  def fromSign(sign: Double): InfinityAtom =
    if (sign > 0) {
      PlusInfAtom
    } else if (sign < 0) {
      MinusInfAtom
    } else {
      throw new IllegalArgumentException("Inf without sign demanded (for example, in ∞ * 0)")
    }
}


trait InfinityAtom extends ImmutableAtom {
  val sign: Int

  override def unary_-(): Atom = InfinityAtom.fromSign(-sign)

  override def *(right: Atom): Atom =
    right match {
      case DoubleAtom(rightDouble) =>
        InfinityAtom.fromSign(this.sign * Math.signum(rightDouble))

      case IntAtom(rightInt) =>
        InfinityAtom.fromSign(this.sign * Math.signum(rightInt))

      case rightInf: InfinityAtom =>
        InfinityAtom.fromSign(this.sign * rightInf.sign)

      case _ => super.*(right)

    }


  override def /(right: Atom): Atom =
    right match {
      case DoubleAtom(rightDouble) =>
        InfinityAtom.fromSign(this.sign * Math.signum(rightDouble))

      case IntAtom(rightInt) =>
        InfinityAtom.fromSign(this.sign * Math.signum(rightInt))


      case _ => super./(right)
    }

  override def toBooleanAtom: BooleanAtom = BooleanAtom(true)
}
