package mycalendar

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Apufunktioita Filehandler-objektille
object ICSUtils:

  // Funktio muuttaa ajan "dd.MM.yyyy HH:mm" -> "yyyyMMdd'T'HHmmss"
  def toIcsDateTime(input: String): String =

    val inFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    val outFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")

    val dateTimeObject = LocalDateTime.parse(input, inFormatter)
    dateTimeObject.format(outFormatter)

  end toIcsDateTime


  // Funktio muuttaa ajan "yyyyMMdd'T'HHmmss" -> "dd.MM.yyyy HH:mm"
  def fromIcsDataTime(icsInput: String): String =
    val inFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
    val outFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    val dateTimeObject = LocalDateTime.parse(icsInput, inFormatter)
    dateTimeObject.format(outFormatter)

  end fromIcsDataTime


  // Funktio parsii .ics muotoisen tapahtuman kalenteriin sopivaksi Event-olioksi
  def parseIcsEvent(lines: Seq[String]): Event =

    var name = ""
    var start = ""
    var end = ""
    var desc = ""
    var catName = "Other"  //Other, jos ei kategoriaa määritelty

    for line <- lines do
      if line startsWith("SUMMARY:") then
        name = line.stripPrefix("SUMMARY:").trim

      else if line.startsWith("DESCRIPTION:") then
        desc = line.stripPrefix("DESCRIPTION:").trim

      else if line.startsWith("DTSTART:") then
        val data = line.stripPrefix("DTSTART:").trim
        start = fromIcsDataTime(data)

      else if line.startsWith("DTEND:") then
        val data = line.stripPrefix("DTEND:").trim
        end = fromIcsDataTime(data)

      else if line.startsWith("CATEGORIES:") then
        catName = line.stripPrefix("CATEGORIES:").trim

    // Korjaa testaus: Nyt viallista dataa voi päätyä Eventtiin
    // Korjaa Category: jos category löytyy ota se väri
    Event(name, start, end, desc, false, Category(catName, scalafx.scene.paint.Color.Black))

  end parseIcsEvent

end ICSUtils
