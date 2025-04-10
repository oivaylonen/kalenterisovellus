package logic

import `type`.{Category, Event}

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

object ICSUtils:

  // Funktio muuttaa ajan "dd.MM.yyyy HH:mm" -> "yyyyMMdd'T'HHmmss"
  def toIcsDateTime(input: String): String =
    val inFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    val outFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
    val dateTimeObject = LocalDateTime.parse(input, inFormatter)
    dateTimeObject.format(outFormatter)
  end toIcsDateTime

  // Funktio muuttaa "yyyyMMdd'T'HHmmss" -> "dd.MM.yyyy HH:mm"
  def fromIcsDataTime(icsInput: String): String =
    val inFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
    val outFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    val dateTimeObject = LocalDateTime.parse(icsInput, inFormatter)
    dateTimeObject.format(outFormatter)
  end fromIcsDataTime

  // Funktio parsii .ics muotoisen tapahtuman kalenteriin sopivaksi Event-olioksi
  def parseIcsEvent(lines: Seq[String]): Event =
    var name    = ""
    var start   = ""
    var end     = ""
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
        start = localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " 00:00"

      else if line.startsWith("DTEND;VALUE=DATE:") then
        allDay = true
        val data = line.stripPrefix("DTEND;VALUE=DATE:").trim
        val localDate = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyyMMdd"))
        end = localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " 23:59"

      else if line.startsWith("DTSTART:") then
        val data = line.stripPrefix("DTSTART:").trim
        start = fromIcsDataTime(data)

      else if line.startsWith("DTEND:") then
        val data = line.stripPrefix("DTEND:").trim
        end = fromIcsDataTime(data)

      else if line.startsWith("CATEGORIES:") then
        catName = line.stripPrefix("CATEGORIES:").trim

    Event(name, start, end, desc, remainder = false, Category(catName, scalafx.scene.paint.Color.Black), allDay = allDay)
  end parseIcsEvent

end ICSUtils
