package logic

import `type`.{Event, Category}
import java.time.LocalDateTime

object CalendarService:

  // apufunktio tallentaa kaikki tapahtumat ICS-tedostoon
  private def persist(): Unit =
    FileHandler.saveEventsToIcs(CalendarData.getAllEvents.toSeq, "src/main/resources/userEvents.ics")

  // Tapahtuman lisäämiseen funktio, käsittelee virhetilanteita lisäämisen yhteydessä
  def addEventValidated(event: Event, allowOverlap: Boolean = true): Either[String, Unit] =

    if event.endTime.isBefore(event.startTime) then
      Left("Lopetusaika ei voi olla ennen aloitusaikaa")

    else if !allowOverlap && hasOverlap(event) then
      Left("Tapahtuma menee päällekkäin olemassa olevan varauksen kanssa")

    else
      CalendarData.addEvent(event)
      persist()
      Right(())

  def removeEvent(event: Event): Boolean =
    val removed = CalendarData.removeEvent(event)
    if removed then persist()
    removed

  // haetaan kaikki tapahtumat tietyltä väliltä
  def eventsBetween(from: LocalDateTime, to: LocalDateTime): Seq[Event] =
    CalendarData.getAllEvents.toSeq.filter(e =>
      !e.endTime.isBefore(from) && !e.startTime.isAfter(to)
    )

  // haetaan kaikki tapahtumat tietystä kategoriasta
  def eventsByCategory(cat: Category): Seq[Event] =
    CalendarData.getAllEvents.toSeq.filter(_.category.name.equalsIgnoreCase(cat.name))

  // tarkistetaan päällekkäisyys
  private def hasOverlap(newEv: Event): Boolean =
    CalendarData.getAllEvents.exists { e =>
      !(e.endTime.isEqual(newEv.startTime) || e.endTime.isBefore(newEv.startTime) ||
        e.startTime.isEqual(newEv.endTime) || e.startTime.isAfter(newEv.endTime))
    }

end CalendarService
