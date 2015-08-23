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

package info.gianlucacosta.chronos.util

import java.io.InputStream

import scala.xml.XML


/**
 * Map-like structure initialized from a given standard XML properties file.
 *
 * @param sourceStream the source stream
 */
class XmlProperties(sourceStream: InputStream) {
  private val propertyMap = {
    val infoXml = XML.load(sourceStream)

    (infoXml \ "entry").map(
      propertyNode => (propertyNode \ "@key").text -> propertyNode.text
    ).toMap
  }

  def apply(key: String): String = propertyMap(key)

  override def toString: String = propertyMap.mkString("{", ",", "}")
}
