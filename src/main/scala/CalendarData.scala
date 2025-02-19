package mycalendar

import scala.collection.mutable.Buffer

object CalendarData

  // Tallennetaan kaikki tapahtumat tähän listaan
  private val events: Buffer[Event] = Buffer()

  // Lisää tapahtuman listaan ja palauuttaa True, kun lisäys onnistuu
  def addEvent(event: Event): Boolean =
    events += event
    true

  // Poistaa valitun tapahtuman listalta.
  // True jos tapahuma poistetaan, False muuten
  def removeEvent(event: Event): Boolean =
    if events.contains(event) then
      events -= event
      true
    else
      false