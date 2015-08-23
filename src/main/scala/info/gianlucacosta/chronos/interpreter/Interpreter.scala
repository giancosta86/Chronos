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

package info.gianlucacosta.chronos.interpreter

import info.gianlucacosta.chronos.ast.Program

/**
 * Virtual machine facade.
 *
 * @param input The input source
 * @param output The output target
 */
class Interpreter(input: Input, output: Output) {
  /**
   * Runs the given program.
   *
   * In case of any exception *except* InterruptedException,
   * the interpretation will stop and the exception will be passed
   * to the output target.
   *
   * InterruptedException, on the other hand, will propagate as a Java-checked
   * exception.
   *
   * @param program The root node of a parsed program - which might have been generated by BasicAstBuilder.
   */
  @throws[InterruptedException]
  def run(program: Program): Unit = {
    val simulationContext = new SimulationContext

    val executingVisitor = new ExecutingVisitor(
      simulationContext,
      input,
      output
    )

    try {
      executingVisitor.visit(program)
    } catch {
      case e: InterruptedException => throw e;
      case e: Exception => output.printException(e)
    }
  }
}