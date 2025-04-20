package logic

import scala.collection.mutable.Buffer
import `type`.{Event, Category}
import scalafx.scene.paint.Color

// Hallinnoi tapahtumia ja kategorioita
object CalendarData:

  // Tapahtumat
  private val events: Buffer[Event] = Buffer()

  // Kategoriat
  private val categories: Buffer[Category] = Buffer()

  // Värit kategorioille
  private val palette: Vector[Color] = Vector(
    Color.Red, Color.Blue, Color.Green,
    Color.Yellow, Color.Orange, Color.Purple,
    Color.Pink, Color.Magenta,
    Color.Brown, Color.Lime
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

  // Lisää uusi kategoria, jos sitä ei ole. Väri valitaan järjestyksessä paletista
  def addCategory(cat: Category): Unit =
    if !categories.exists(_.name.equalsIgnoreCase(cat.name)) then
      val colorIndex = categories.length % palette.length
      val coloredCat = cat.copy(color = palette(colorIndex))
      categories += coloredCat

  def getAllCategories: Buffer[Category] =
    categories

end CalendarData
