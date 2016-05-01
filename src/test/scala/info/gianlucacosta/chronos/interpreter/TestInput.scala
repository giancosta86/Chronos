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

import info.gianlucacosta.chronos.interpreter.atoms.{BooleanAtom, DoubleAtom, IntAtom, StringAtom}

class TestInput(
                 private var doubles: Seq[Double],
                 private var ints: Seq[Int],
                 private var booleans: Seq[Boolean],
                 private var strings: Seq[String]) extends Input {
  def this() = this(Nil, Nil, Nil, Nil)

  override def readDouble(prompt: String): DoubleAtom = {
    val result = DoubleAtom(doubles.head)
    doubles = doubles.tail
    result
  }

  override def readInt(prompt: String): IntAtom = {
    val result = IntAtom(ints.head)
    ints = ints.tail
    result
  }


  override def readBoolean(prompt: String): BooleanAtom = {
    val result = BooleanAtom(booleans.head)
    booleans = booleans.tail
    result
  }


  override def readString(prompt: String): StringAtom = {
    val result = StringAtom(strings.head)
    strings = strings.tail
    result
  }
}
