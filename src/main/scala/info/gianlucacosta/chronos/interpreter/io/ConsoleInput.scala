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

import java.io.{BufferedReader, InputStreamReader}

import info.gianlucacosta.chronos.interpreter.atoms.StringAtom
import info.gianlucacosta.chronos.interpreter.exceptions.FailedReadException

class ConsoleInput extends BasicInput {
  private val lineReader = new BufferedReader(new InputStreamReader(System.in))

  override def readString(prompt: String): StringAtom = {
    print(prompt)

    val line = lineReader.readLine()

    if (line == null) {
      throw new FailedReadException("Interrupted input")
    }

    StringAtom(line)
  }
}
