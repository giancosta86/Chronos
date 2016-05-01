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

package info.gianlucacosta.chronos.ast

import info.gianlucacosta.chronos.ast.expressions._
import info.gianlucacosta.chronos.ast.expressions.ground._
import info.gianlucacosta.chronos.ast.statements._

trait AstVisitor[T] {
  def visit(node: Node): T

  def visit(node: Program): T

  def visit(node: DisableHeapCheck): T

  def visit(node: Event): T

  def visit(node: Procedure): T

  def visit(node: Block): T

  def visit(statement: Statement): T

  def visit(node: Return): T

  def visit(node: Exit): T

  def visit(node: Print): T

  def visit(node: Println): T

  def visit(node: AssignLocalReference): T

  def visit(node: AssignGlobalReference): T

  def visit(node: Call): T

  def visit(node: Assert): T

  def visit(node: CreateMap): T

  def visit(node: CreateEntity): T

  def visit(node: DestroyEntity): T

  def visit(node: ReadDouble): T

  def visit(node: ReadInt): T

  def visit(node: ReadBoolean): T

  def visit(node: ReadString): T

  def visit(node: CreateFifoQueue): T

  def visit(node: CreateLifoQueue): T

  def visit(node: CreateSortedQueue): T

  def visit(node: Enqueue): T

  def visit(node: Remove): T

  def visit(node: ScheduleAt): T

  def visit(node: ScheduleAfter): T

  def visit(node: Cancel): T

  def visit(node: If): T

  def visit(node: While): T

  def visit(node: SetRandomSeed): T


  /*
   * EXPRESSIONS
   */

  def visit(expression: Expression): T

  def visit(expression: Or): T

  def visit(expression: And): T

  def visit(expression: Not): T

  def visit(expression: Comparison): T

  def visit(expression: AlgebraicSum): T

  def visit(expression: UnaryMinus): T

  def visit(expression: Multiplication): T

  def visit(expression: Division): T

  def visit(expression: ReferenceValue): T

  def visit(expression: Dequeue): T

  def visit(expression: CastToDouble): T

  def visit(expression: CastToInt): T

  def visit(expression: CastToBoolean): T

  def visit(expression: CastToString): T

  def visit(expression: Floor): T

  def visit(expression: Ceil): T

  def visit(expression: IsEmpty): T

  def visit(expression: UniformRandom): T

  def visit(expression: UniformIntRandom): T

  def visit(expression: ExponentialRandom): T

  def visit(expression: DoubleTerm): T

  def visit(expression: IntTerm): T

  def visit(expression: BooleanTerm): T

  def visit(expression: StringTerm): T

  def visit(expression: PlusInfTerm): T

  def visit(expression: MinusInfTerm): T

  def visit(expression: Now): T

  def visit(expression: Condition): T

  def visit(reference: Reference): T
}
