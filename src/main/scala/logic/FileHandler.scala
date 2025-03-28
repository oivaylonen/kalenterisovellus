package logic

import `type`.Event

import java.io.{File, PrintWriter}
import scala.collection.mutable.Buffer
import scala.io.Source

// Filehanler luokka lukee ja kirjoittaa Event-olioita .ics -tiedostoon
// .ics muoto mahdollistaa kalenterisovellusten yhteensopivuuden
object FileHandler:

  // Funktio ottaa listan eventtejä ja tallentaa ne haluttuun tiedostoon
  def saveEventsToIcs(events: Seq[Event], filename: String): Unit =

    // Uusi PrintWrite olio joka kirjoittaa filename tiedostoon
    val pw = PrintWriter(File(filename))

    // Try-lohkosta löytyy koodi joka kirjoitetaan tiedostoon
    try
      // Lisätään aluksi pakolliset otsikkorivit
      pw.println("BEGIN:VCALENDAR") // Aloitus
      pw.println("VERSION:2.0") // Versio
      pw.println("PRODID:-//OivanKalenteri//FI") // Palvelun tarjoaja | Projektin nimi | Lokaatio
      pw.println("CALSCALE:GREGORIAN") // Kalenterin laskujärjestelmä
      pw.println("METHOD:PUBLISH") // Jakotapa

      // Käydään kaikki listan Event-oliot läpi, ja muutetaan ne .ics muotoon
      for e <- events do

        // Lisätään tapahtuman aloitus
        pw.println("BEGIN:VEVENT")

        // Luodaan uniikki ID tunniste
        val uid = java.util.UUID.randomUUID().toString

        // Muutetaan start ja end ajat .ics muotoon
        val icsStart = ICSUtils.toIcsDateTime(e.startTime)
        val icsEnd = ICSUtils.toIcsDateTime(e.endTime)

        // Lisätään tapahtuman tiedot
        pw.println(s"UID:$uid")
        pw.println(s"DTSTART:$icsStart")
        pw.println(s"DTEND:$icsEnd")
        pw.println(s"SUMMARY:${e.name}")
        pw.println(s"DESCRIPTION:${e.description}")
        pw.println(s"CATEGORIES:${e.category.name}")

        // Lisätään tapahtuman lopetus
        pw.println("END:VEVENT")

      // Lisätään kalenterin lopetus
      pw.println("END:VCALENDAR")
    finally
      // Suljetaan tiedosto
      pw.close()

  end saveEventsToIcs


  // Funktio lukee .ics tiedostosta dataa ja palauttaa taulukon Eventtejä
  def loadEventsFromIcs(filename: String): Seq[Event] =
    // luetaan tiedston rivit listaan
    val lines = Source.fromFile(filename).getLines().toList

    var insideEvent = false // True, kun kerätään Eventtiä
    val eventLines = Buffer[String]() // Kerätään yksittäisen Eventin arvot tänne
    val results = Buffer[Event]() // Laitetaan kerätyt Eventit tänne

    for line <- lines do
      if line startsWith("BEGIN:VEVENT") then
        insideEvent = true
        eventLines.clear() //Tyhjennetään edellinen
      else if line.startsWith("END:VEVENT") then
        insideEvent = false
        val newEvent = ICSUtils.parseIcsEvent(eventLines.toSeq)
        results.append(newEvent)
      else if insideEvent then
        eventLines.append(line)

    results.toSeq
  end loadEventsFromIcs

end FileHandler
