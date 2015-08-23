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

package info.gianlucacosta.chronos.interpreter.io

import info.gianlucacosta.chronos.interpreter.Input
import info.gianlucacosta.chronos.interpreter.atoms.{BooleanAtom, DoubleAtom, IntAtom}

/**
 * Simplifies input by implementing most functions in terms of readString(),
 * which remains abtract.
 */
abstract class BasicInput extends Input {
  override def readDouble(prompt: String): DoubleAtom = {
    var result: Option[DoubleAtom] = None

    while (result.isEmpty) {
      val inputString = readString(prompt)

      try {
        result = Some(inputString.toDoubleAtom)
      } catch {
        case _: Exception =>
      }
    }

    result.get
  }

  override def readInt(prompt: String): IntAtom = {
    var result: Option[IntAtom] = None

    while (result.isEmpty) {
      val inputString = readString(prompt)

      try {
        result = Some(inputString.toIntAtom)
      } catch {
        case _: Exception =>
      }
    }

    result.get
  }

  override def readBoolean(prompt: String): BooleanAtom = {
    var result: Option[BooleanAtom] = None

    while (result.isEmpty) {
      val inputString = readString(prompt)

      try {
        result = Some(inputString.toBooleanAtom)
      } catch {
        case _: Exception =>
      }
    }

    result.get
  }
}
