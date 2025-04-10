package logic

import scala.collection.mutable.Buffer
import `type`.{Event, Category}
import scalafx.scene.paint.Color

// Hallinnoi tapahtumia ja kategorioita
object CalendarData:

  // Tapahtumat
  private val events: Buffer[Event] = Buffer()

  // Kategoriat
  private val categories: Buffer[Category] = Buffer(
    Category("Yleinen", Color.Black),
    Category("Opiskelu", Color.Blue),
    Category("Työ", Color.Green),
    Category("Vapaa-aika", Color.Magenta)
  )

  // Lisää uusi tapahtuma, jos se ei jo ole listassa
  // Tarkistetaan, myös että categoria löytyy categorioista
  def addEvent(event: Event): Boolean =
    // Lisätään kategoria, jos se puuttuu
    addCategory(event.category)
    // Lisätään tapahtuma
    events += event
    true

  // Poista haluttu tapahtuma
  def removeEvent(event: Event): Boolean =
    if events.contains(event) then
      events -= event
      true
    else
      false

  def getAllEvents: Buffer[Event] =
    events

  // Kategorioiden lisääminen
  def addCategory(cat: Category): Unit =
    // Jos ei löydy saman nimistä, lisätään
    if !categories.exists(_.name.equalsIgnoreCase(cat.name)) then
      categories += cat

  def getAllCategories: Buffer[Category] =
    categories

end CalendarData
