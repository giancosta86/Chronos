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

package info.gianlucacosta.chronos

import java.io.{File, FileReader}

import info.gianlucacosta.chronos.interpreter.Interpreter
import info.gianlucacosta.chronos.interpreter.io.{ConsoleInput, ConsoleOutput}
import info.gianlucacosta.chronos.parser.BasicAstBuilder
import info.gianlucacosta.chronos.parser.exceptions.ParsingException


object App {
  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      exitWithUsage()
    }

    val sourceFile = new File(args(0))
    if (!sourceFile.exists()) {
      exitWithUsage()
    }


    try {
      val program = BasicAstBuilder.buildAST(new FileReader(sourceFile))

      val input = new ConsoleInput
      val output = new ConsoleOutput
      val interpreter = new Interpreter(input, output)

      interpreter.run(program)
      System.exit(0)
    } catch {
      case ex@(_: ParsingException) => System.err.println(ex.getMessage)
        System.exit(1)
    }
  }


  private def exitWithUsage(): Unit = {
    println(s"${ArtifactInfo.name} - Version ${ArtifactInfo.version}")
    println(s"Copyright © ${ArtifactInfo.copyrightYears} Gianluca Costa")
    println()
    println(s"Usage: <program source file>")
    System.exit(1)
  }
}
