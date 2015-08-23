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

/**
 * Extends XmlProperties by providing common, typed fields
 *
 * @param sourceStream the source stream
 */
class CommonXmlProperties(sourceStream: InputStream) extends XmlProperties(sourceStream) {
  val name = this("name")
  val version = this("version")
  val copyrightYears = this("copyrightYears")
  val license = this("license")
  val website = this("website")
  val facebookPage = this("facebookPage")
  val release = this("release").toBoolean
}
