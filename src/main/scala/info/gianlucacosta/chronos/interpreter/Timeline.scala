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

import info.gianlucacosta.chronos.interpreter.atoms.EventNotice
import info.gianlucacosta.chronos.interpreter.exceptions.SchedulingException

import scala.collection.immutable.TreeMap

private class Timeline {
  private var _currentTime: Double = 0

  def currentTime = _currentTime

  private var instantsToNoticesMap = TreeMap.empty[Double, List[EventNotice]]
  private var noticesToInstantsMap = Map.empty[EventNotice, Double]

  def hasEventNotices =
    instantsToNoticesMap.nonEmpty


  def fetchNextEventNotices(): List[EventNotice] = {
    require(hasEventNotices)

    val currentCoordinates = instantsToNoticesMap.head

    _currentTime = currentCoordinates._1
    val eventNoticesAtInstant = currentCoordinates._2.reverse

    instantsToNoticesMap -= _currentTime
    eventNoticesAtInstant.foreach(noticesToInstantsMap -= _)

    eventNoticesAtInstant
  }


  def scheduleAt(instant: Double, eventNotice: EventNotice): Unit = {
    if (instant < _currentTime) {
      throw new SchedulingException(
        s"At time ${_currentTime}, it is impossible to schedule event for time ${instant}"
      )
    }

    if (noticesToInstantsMap contains eventNotice) {
      throw new SchedulingException(s"Event notice already scheduled: ${eventNotice}")
    }

    val eventNoticesAtInstant =
      eventNotice :: instantsToNoticesMap.getOrElse(instant, Nil)

    instantsToNoticesMap += (instant -> eventNoticesAtInstant)
    noticesToInstantsMap += (eventNotice -> instant)
  }


  def cancel(eventName: String, eventNotice: EventNotice): Unit = {
    val instant = noticesToInstantsMap.getOrElse(eventNotice,
      throw new SchedulingException(
        s"Cannot cancel this unscheduled event notice - ${eventNotice}"
      )
    )

    val filteredNoticesAtInstant = instantsToNoticesMap(instant).filter(!eventNotice.eq(_))
    instantsToNoticesMap += (instant -> filteredNoticesAtInstant)

    noticesToInstantsMap -= eventNotice
  }
}
