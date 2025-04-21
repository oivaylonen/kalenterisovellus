package views

import scalafx.scene.layout.*
import scalafx.scene.control.{Label, ScrollPane}
import scalafx.geometry.{Insets, Pos}
import java.time.{DayOfWeek, LocalDate}
import ViewUtils.*
import logic.CalendarService
import scalafx.scene.paint.Color

object WeekView:

  private val slotMinutes   = 30
  private val rowHeight     = 36
  private val rowsPerDay    = (24 * 60) / slotMinutes

  // onDayClicked kutsutaan kun päivän otsikkoa klikataan
  // activeCats on aktiivisten kategorioiden lista
  def createWeekView(onDayClicked: LocalDate => Unit, activeCats: Set[String]): BorderPane =

    // nykyisen viikon maanantai
    var monday = LocalDate.now().`with`(DayOfWeek.MONDAY)
    val title  = new Label { style = "-fx-font-size:16px;-fx-font-weight:bold;" }

    // ruudukko, johon laitetaan tunnit ja tapahtumat
    val grid = new GridPane:
      padding = Insets(10)
      style   = "-fx-background-color:#ffffff;-fx-border-color:#dddddd;"
      prefWidth = Double.MaxValue
    grid.columnConstraints.add(new ColumnConstraints { percentWidth = 10 })
    for _ <- 1 to 7 do grid.columnConstraints.add(new ColumnConstraints { percentWidth = 90.0 / 7 }) // päivät
    for _ <- 0 until rowsPerDay do grid.rowConstraints.add(new RowConstraints(rowHeight)) // tunnit

    // Muutetaan scalaFX color CSS muotoon
    def css(c: Color): String =
      f"#${(c.red * 255).toInt}%02x${(c.green * 255).toInt}%02x${(c.blue * 255).toInt}%02x"

    // Luodaan yksittäinen tapahtuma
    def makeNode(name: String, col: Color, span: Int, start: java.time.LocalTime, end: java.time.LocalTime): Pane =
      val h = span * rowHeight - 4
      val hex = css(col)
      val timeLabel = f"${start.getHour}%02d:${start.getMinute}%02d–${end.getHour}%02d:${end.getMinute}%02d"
      // tapahtuma harmaalla taustalla ja reuna kertoo kategoriasta
      new VBox:
        alignment = Pos.TopLeft
        padding   = Insets(3, 6, 3, 8)
        prefHeight = h; minHeight = h; maxHeight = h
        style =
          s"""-fx-background-color: #eeeeee;
             |-fx-background-radius: 6;
             |-fx-border-color: $hex;
             |-fx-border-width: 1;
             |-fx-border-radius: 6;""".stripMargin
        children = Seq(
          new Label(name)      { style = "-fx-text-fill: #000000; -fx-font-size: 11px; -fx-font-weight: bold;" },
          new Label(timeLabel) { style = "-fx-text-fill: #000000; -fx-font-size: 10px;" }
        )

    // päivittää näkymän uudelleen
    def refresh(): Unit =
      grid.children.clear()
      // tuntiotsikko
      grid.add(new Label("Tunnit") { style = "-fx-font-weight:bold;" }, 0, 0)
      // päiväotsikot ja siirtyminen päivänäkymään
      for d <- 0 until 7 do
        val date = monday.plusDays(d)
        grid.add(new Label(formatDayLabel(date)) {
          style = dayHeaderStyle
          onMouseClicked = _ => onDayClicked(date)
        }, d + 1, 0)

      // aikatekstit sivuun
      for r <- 0 until rowsPerDay do
        val hour = r / 2; val min = if r % 2 == 0 then "00" else "30"
        grid.add(new Label(f"$hour%02d:$min") { style = "-fx-font-size:10px;" }, 0, r + 1)

      // viikon alku ja loppu
      val weekStart = monday.atStartOfDay()
      val weekEnd   = monday.plusDays(7).atStartOfDay().minusSeconds(1)

      // tapahtumien hakeminen
      val events = CalendarService.eventsBetween(weekStart, weekEnd)
        .filter(e => activeCats.isEmpty || activeCats.contains(e.category.name))
        .sortBy(_.startTime)

      // Lisätään tapahtuma kalenteritaulukkoon
      for ev <- events do
        val dayIdx = java.time.Duration.between(weekStart, ev.startTime).toDays.toInt
        if dayIdx >= 0 && dayIdx < 7 then
          val sRow = ((ev.startTime.getHour * 60 + ev.startTime.getMinute) / slotMinutes).max(0).min(rowsPerDay - 1) // aloitusajan ruutu
          val eRow = ((ev.endTime.getHour   * 60 + ev.endTime.getMinute) / slotMinutes).max(sRow + 1).min(rowsPerDay) // lotetusajan ruutu
          val span = eRow - sRow // tapahtuman korkeus
          val node = makeNode(ev.name, ev.category.color, span, ev.startTime.toLocalTime, ev.endTime.toLocalTime) // visualisointi tapahtumalle
          grid.add(node, dayIdx + 1, sRow + 1) // lisäys ruudukkoon
          GridPane.setRowSpan(node, span) // määrittelee kuinka monta ruutua käytetään

      title.text = s"${finnishMonthName(monday)} ${monday.getYear}"

    // navigaatio palkki viikkojen välillä liikkumiseen
    val nav = new HBox:
      spacing = 20; alignment = Pos.Center; padding = Insets(10)
      style = "-fx-background-color:#ffffff;-fx-border-color:#dddddd;"
      children = Seq(
        navButton("<") { monday = monday.minusWeeks(1); refresh() },
        title,
        navButton(">") { monday = monday.plusWeeks(1); refresh() }
      )

    // asettelu
    new BorderPane:
      top    = nav
      center = new ScrollPane { content = grid; fitToWidth = true }
      style  = "-fx-background-color:#ffffff;"
      refresh()
end WeekView