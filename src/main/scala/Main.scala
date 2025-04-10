package mycalendar

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.control.Button
import scalafx.geometry.Pos
import views.{DayView, WeekView, MonthView, AddEventView, RemoveEventView}
import java.time.LocalDate
import logic.{FileHandler, CalendarData}

object Main extends JFXApp3 {

  override def start(): Unit = {

    // Lisätään juhlapyhät calenderdataan
    val holidays = FileHandler.loadEventsFromIcs("src/main/resources/FinlandHolidays.ics")
    holidays.foreach(CalendarData.addEvent)

    val rootLayout = new BorderPane()

    def buttonStyle =
      """-fx-background-color: transparent;
        |-fx-border-color: transparent;
        |-fx-font-size: 13px;
        |-fx-cursor: hand;
        |-fx-padding: 5 10;
        |-fx-background-radius: 8;
      """.stripMargin

    // Sivuvalikko
    val sideMenu = new VBox {
      spacing = 10
      alignment = Pos.TopLeft
      style =
        """-fx-background-color: #f4f4f4;
          |-fx-border-color: #ddd;
          |-fx-padding: 10;
        """.stripMargin

      children = Seq(
        new Button("Lisää tapahtuma")  {
        style = buttonStyle
        onAction = _ => {
        rootLayout.center = AddEventView.createAddEventView()
        }},
        new Button("Poista tapahtuma") {
        style = buttonStyle
        onAction = _ => {
        rootLayout.center = RemoveEventView.createRemoveEventView()
        }},
        new Button("Filtteröinti")     { disable = true }, //Toteutus tulossa
        new Button("Haku")            { disable = true } //Toteutus tulossa
      )
    }
    // Sivuvalikko avatessa piilossa
    var sideMenuOpen = false

    def toggleSideMenu(): Unit = {
      sideMenuOpen = !sideMenuOpen
      if (sideMenuOpen) rootLayout.left = sideMenu
      else rootLayout.left = null
    }

    // Näkymät (päivä, viikko, kuukausi)
    def showDayView(date: LocalDate): Unit = {
      rootLayout.center = DayView.createDayView(date)
    }
    val weekViewPane = WeekView.createWeekView { clickedDate =>
      showDayView(clickedDate)
    }
    val monthViewPane = MonthView.createMonthView(showDayView)

    // Tästä valitaan aloitusnäkymä
    rootLayout.center = monthViewPane

    // Yläpalkki (liikkuminen näkymien välillä)
    val hamburgerButton = new Button("☰") {
      onAction = _ => toggleSideMenu()
      style = buttonStyle
    }

    val dayButton = new Button("Tänään") {
      style = buttonStyle
      onAction = _ => {
        showDayView(LocalDate.now())
      }
    }

    val weekButton = new Button("Viikko") {
      style = buttonStyle
      onAction = _ => {
        rootLayout.center = weekViewPane
      }
    }

    val monthButton = new Button("Kuukausi") {
      style = buttonStyle
      onAction = _ => {
        rootLayout.center = monthViewPane
      }
    }

    val topBar = new HBox {
      spacing = 20
      alignment = Pos.CenterLeft
      style =
        """-fx-background-color: #fdfdfd;
          |-fx-border-color: #ddd;
          |-fx-padding: 10 10;
        """.stripMargin
      children = Seq(hamburgerButton, dayButton, weekButton, monthButton)
    }

    // Yläpalkki näkyviin
    rootLayout.top = topBar

    // Stage käynnistämissessä
    stage = new JFXApp3.PrimaryStage {
      title = "Kalenterisovellus"
      scene = new Scene {
        root = rootLayout
      }
    }
  }
}
