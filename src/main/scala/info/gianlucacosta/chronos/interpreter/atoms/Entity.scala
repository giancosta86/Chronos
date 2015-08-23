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

import info.gianlucacosta.chronos.interpreter.exceptions.{IllegalIndexTypeException, PropertyNotFoundException}

class Entity(val name: String, val entityType: String) extends Atom with HeapAtom {
  protected var properties = Map.empty[String, Atom]


  override def compare(right: Atom): Int = {
    right match {
      case rightEntity: Entity =>
        System.identityHashCode(this) compare System.identityHashCode(rightEntity)

      case _ => super.compare(right)
    }
  }

  override def apply(property: Atom): Atom = {
    property match {
      case StringAtom(propertyName) =>
        properties.getOrElse(
          propertyName,

          throw new PropertyNotFoundException(property)
        )

      case _ => throw new IllegalIndexTypeException(s"Invalid value for property name: '${property}'")
    }
  }

  override def update(property: Atom, value: Atom): Unit = {
    property match {
      case StringAtom(propertyName) =>
        properties += (propertyName -> value)

      case _ => throw new IllegalIndexTypeException(s"Invalid value for property name: '${property}'")
    }
  }


  override def -=(property: Atom): Unit = {
    property match {
      case StringAtom(propertyName) =>
        if (!properties.contains(propertyName)) {
          throw new PropertyNotFoundException(property)
        }
        properties -= propertyName

      case _ => throw new IllegalIndexTypeException(s"Invalid property name: '${property}'")
    }
  }


  override def toString =
    s"Entity(name: ${name}, type: ${entityType}) {${properties.mkString(",")}}"
}
