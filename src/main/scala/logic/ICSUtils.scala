package logic

import `type`.{Category, Event}

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import scalafx.scene.paint.Color

object ICSUtils:

  // localdatetime => ICS merkkijono
  def toIcsDateTime(input: LocalDateTime): String =
  input.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
  end toIcsDateTime

  // ICS merkkijono => localdatetime
  def fromIcsDataTime(icsInput: String): LocalDateTime =
    LocalDateTime.parse(icsInput, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
  end fromIcsDataTime

  // Apufunktio ajan näyttämiseen
  def formatForGui(input: LocalDateTime): String =
    input.format(DateTimeFormatter.ofPattern("HH:mm"))
  end formatForGui

  // Funktio parsii .ics muotoisen tapahtuman kalenteriin sopivaksi Event-olioksi
  def parseIcsEvent(lines: Seq[String]): Event =
    var name    = ""
    var startOpt   = Option.empty[LocalDateTime]
    var endOpt     = Option.empty[LocalDateTime]
    var desc    = ""
    var catName = "Other"
    var allDay  = false

    for line <- lines do
      if line.startsWith("SUMMARY:") then
        name = line.stripPrefix("SUMMARY:").trim

      else if line.startsWith("DESCRIPTION:") then
        desc = line.stripPrefix("DESCRIPTION:").trim

      else if line.startsWith("DTSTART;VALUE=DATE:") then
        allDay = true
        val data = line.stripPrefix("DTSTART;VALUE=DATE:").trim
        val localDate = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyyMMdd"))
        startOpt = Some(localDate.atStartOfDay())

      else if line.startsWith("DTEND;VALUE=DATE:") then
        allDay = true
        val data = line.stripPrefix("DTEND;VALUE=DATE:").trim
        val localDate = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyyMMdd"))
        endOpt = Some(localDate.minusDays(1).atTime(23, 59))

      else if line.startsWith("DTSTART:") then
        val data = line.stripPrefix("DTSTART:").trim
        startOpt = Some(fromIcsDataTime(data))

      else if line.startsWith("DTEND:") then
        val data = line.stripPrefix("DTEND:").trim
        endOpt = Some(fromIcsDataTime(data))

      else if line.startsWith("CATEGORIES:") then
        catName = line.stripPrefix("CATEGORIES:").trim


    val start = startOpt.getOrElse(throw new IllegalArgumentException("DSTART puuttuu tiedostosta"))
    val end = endOpt.getOrElse(start)

    // Lisätään ladattaville kategorioille värit
    val tempCat = Category(catName, Color.Black)
    CalendarData.addCategory(tempCat)
    val finalCat = CalendarData.getAllCategories.find(_.name.equalsIgnoreCase(catName)).getOrElse(tempCat)

    Event(
      name        = name,
      startTime   = start,
      endTime     = end,
      description = desc,
      remainder   = false,
      category    = finalCat,
      allDay      = allDay
    )
  end parseIcsEvent

end ICSUtils
