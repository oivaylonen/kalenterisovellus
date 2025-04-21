package views

import scalafx.Includes.*
import scalafx.scene.layout.*
import scalafx.scene.control.{Label, ScrollPane}
import scalafx.geometry.{Insets, Pos}
import java.time.{LocalDate, LocalTime}
import logic.CalendarService
import ViewUtils.*
import scalafx.scene.paint.Color

object DayView:

  private val slotMinutes = 15
  private val rowHeight   = 24
  private val rowsPerDay  = (24 * 60) / slotMinutes

  // scalaFX värin muutaminen hex koodiksi
  def css(c: Color): String =
    f"#${(c.red * 255).toInt}%02x${(c.green * 255).toInt}%02x${(c.blue * 255).toInt}%02x"

  // visualisointi yksittäiselle tapahtumalle
  // span kertoo käytettävien solujen määrän
  def makeNode(name: String, color: Color, span: Int, localStart: LocalTime, localEnd: LocalTime): Pane =
    val h = span * rowHeight - 4
    val timeTxt = f"${localStart.getHour}%02d:${localStart.getMinute}%02d–${localEnd.getHour}%02d:${localEnd.getMinute}%02d"
    new VBox:
      alignment = Pos.TopLeft
      padding   = Insets(3, 6, 3, 8)
      prefHeight = h; minHeight = h; maxHeight = h
      userData   = "event"
      style =
        s"""-fx-background-color:#eeeeee;
           |-fx-background-radius:6;
           |-fx-border-color:${css(color)};
           |-fx-border-width:1;
           |-fx-border-radius:6;""".stripMargin
      children = Seq(
        new Label(name)    { style = "-fx-font-size:11px;-fx-font-weight:bold;" },
        new Label(timeTxt) { style = "-fx-font-size:10px;" }
      )

  def createDayView(initialDate: LocalDate, activeCats: Set[String]): BorderPane =

    var currentDate = initialDate

    // Ruudukko tapahtumille
    val grid = new GridPane:
      padding = Insets(10)
      style   = "-fx-background-color:#ffffff;-fx-border-color:#dddddd;"
      prefWidth = Double.MaxValue

    grid.columnConstraints.add(new ColumnConstraints { percentWidth = 12 }) // kellonajat
    grid.columnConstraints.add(new ColumnConstraints { percentWidth = 88 }) // tapahtumat

    for r <- 0 until rowsPerDay do
      val hour   = r / 4
      val minute = (r % 4) * slotMinutes
      val lblTxt = if minute == 0 then f"$hour%02d:00" else ""
      grid.rowConstraints.add(new RowConstraints(rowHeight))
      grid.add(new Label(lblTxt) { style = "-fx-font-size:10px;" }, 0, r)
      grid.add(new Pane { style = "-fx-border-color:#f5f5f5;" }, 1, r)

    val titleLabel = new Label { style = "-fx-font-size:16px;-fx-font-weight:bold;" }

    // näkymän päitys
    def refresh(): Unit =
      titleLabel.text = formatDayLabel(currentDate) + "." + currentDate.getYear
      val old = grid.children.filter(_.userData == "event")
      // poistetaan tapahtumat
      grid.children.removeAll(old.toSeq*)

      // päivän alku ja loppu
      val dayStart = currentDate.atStartOfDay()
      val dayEnd   = currentDate.plusDays(1).atStartOfDay().minusSeconds(1)

      // haetaan tapahtumat päivälle ja suodatetaan kategorian perusteella
      val events = CalendarService.eventsBetween(dayStart, dayEnd)
        .filter(e => activeCats.isEmpty || activeCats.contains(e.category.name))

      // käsitellään usean päivän kestäviä tapahtumia
      for ev <- events do
        val localStartDT = if ev.startTime.isBefore(dayStart) then dayStart else ev.startTime
        val localEndDT   = if ev.endTime.isAfter(dayEnd) then dayEnd else ev.endTime

        val sRow = ((localStartDT.getHour * 60 + localStartDT.getMinute) / slotMinutes).max(0).min(rowsPerDay - 1) //aloitusajan ruutu
        val eRow = ((localEndDT.getHour * 60 + localEndDT.getMinute) / slotMinutes).max(sRow + 1).min(rowsPerDay) //lopetusajan ruutu
        val span = eRow - sRow // tapahtuman korkeus

        val node = makeNode(ev.name, ev.category.color, span,
                            localStartDT.toLocalTime, localEndDT.toLocalTime)
        grid.add(node, 1, sRow) // lisätään tapahtuma
        GridPane.setRowSpan(node, span)
      end for

    // päivä taaksepäin
    val prevBtn = navButton("<") {
      currentDate = currentDate.minusDays(1)
      refresh()
    }
    // päivä eteenpäin
    val nextBtn = navButton(">") {
      currentDate = currentDate.plusDays(1)
      refresh()
    }
    // navigaatio päivien välillä liikkumiseen
    val topBar = new HBox:
      spacing = 20
      alignment = Pos.Center
      padding = Insets(10)
      style = "-fx-background-color:#ffffff;-fx-border-color:#dddddd;"
      children = Seq(prevBtn, titleLabel, nextBtn)

    refresh()

    // Asettelu
    new BorderPane:
      top = topBar
      center = new ScrollPane:
        content = grid
        fitToWidth = true
      style = "-fx-background-color:#ffffff;"
end DayView
