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