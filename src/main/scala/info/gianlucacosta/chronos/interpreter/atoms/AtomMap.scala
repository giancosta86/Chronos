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

import info.gianlucacosta.chronos.interpreter.exceptions.{IllegalIndexTypeException, MapKeyNotFoundException}

class AtomMap extends Atom {
  var internalMap = Map.empty[Atom, Atom]

  override def apply(key: Atom): Atom = {
    key match {
      case _: ImmutableAtom =>
        internalMap.getOrElse(
          key,

          throw new MapKeyNotFoundException(key)
        )

      case _ => throw new IllegalIndexTypeException(s"Invalid key: '${key}'")
    }
  }


  override def update(key: Atom, value: Atom): Unit = {
    key match {
      case _: ImmutableAtom =>
        internalMap += (key -> value)

      case _ => throw new IllegalIndexTypeException(s"Invalid key: '${key}'")
    }
  }


  override def -=(key: Atom): Unit = {
    key match {
      case _: ImmutableAtom =>
        if (!internalMap.contains(key)) {
          throw new MapKeyNotFoundException(key)
        }
        internalMap -= key

      case _ => throw new IllegalIndexTypeException(s"Invalid key: '${key}'")
    }
  }

  override def toString = s"Map(${internalMap.mkString(",")})"
}
