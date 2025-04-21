package mycalendar

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.scene.control.Button
import scalafx.geometry.Pos
import views.{DayView, WeekView, MonthView, AddEventView, RemoveEventView, ReportView, FilterView}
import logic.{FileHandler, CalendarData, FilterState}
import java.time.LocalDate
import scalafx.Includes.*

object Main extends JFXApp3:

  // Eri tilat: viikko, päivä, kuukausi
  sealed trait Mode
  case object Day   extends Mode
  case object Week  extends Mode
  case object Month extends Mode
  private var mode: Mode = Month
  private var dayCursor: LocalDate = LocalDate.now()

  private val root = new BorderPane()

  // Näkymien näyttämiseen funktiot
  private def showDay(date: LocalDate): Unit =
    mode = Day
    dayCursor = date
    root.center = DayView.createDayView(date, FilterState.activeCategories)

  private def showWeek(): Unit =
    mode = Week
    root.center = WeekView.createWeekView(showDay, FilterState.activeCategories)

  private def showMonth(): Unit =
    mode = Month
    root.center = MonthView.createMonthView(showDay, FilterState.activeCategories)

  private def refresh(): Unit = mode match
    case Day   => showDay(dayCursor)
    case Week  => showWeek()
    case Month => showMonth()

  // yläpalkki näkymien selaamiseen
  private def topBar =
    def btn(label: String)(action: => Unit) =
      new Button(label):
        style = "-fx-background-color:transparent;-fx-padding:6 12;-fx-cursor:hand;"
        alignment = Pos.Center
        onAction = _ => action

    new scalafx.scene.layout.HBox:
      spacing    = 20
      alignment  = Pos.CenterLeft
      style =
        """-fx-background-color:#ffffff;
          |-fx-border-color:#dddddd;
          |-fx-padding:8;""".stripMargin
      children = Seq(
        btn("Tänään")  { showDay(LocalDate.now()) },
        btn("Viikko")  { showWeek() },
        btn("Kuukausi"){ showMonth() },
        btn("Lisää")   { root.center = AddEventView.createAddEventView(() => refresh()) },
        btn("Poista")  { root.center = RemoveEventView.createRemoveEventView(() => refresh()) },
        btn("Raportti"){ root.center = ReportView.createReportView() },
        btn("Suodata") { root.center = FilterView.createFilterView(() => refresh()) }
      )

  // Kalenterin käynnistys
  override def start(): Unit =
    val events = FileHandler.loadEventsFromIcs("src/main/resources/userEvents.ics")
    events.foreach(CalendarData.addEvent)

    root.top = topBar
    showMonth()

    stage = new JFXApp3.PrimaryStage:
      title = "Kalenteri"
      scene = Scene(root)

end Main
