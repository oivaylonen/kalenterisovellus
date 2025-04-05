package mycalendar

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.control.Button
import scalafx.geometry.Pos
import scalafx.scene.paint.Color
import views.{WeekView, DayView, MonthView}
import java.time.LocalDate

object Main extends JFXApp3 {

  override def start(): Unit = {
    val rootLayout = new BorderPane()

    val dayViewPane   = DayView.createDayView(LocalDate.now())
    val weekViewPane  = WeekView.createWeekView(date => {/*TODO: siirry DayViewiin tms.*/})
    val monthViewPane = MonthView.createMonthView()

    // Sivuvalikko (drawer)
    val sideMenu = new VBox {
      spacing = 10
      alignment = Pos.TopLeft
      prefWidth = 180
      style =
        """-fx-background-color: #f4f4f4;
          |-fx-border-color: #ddd;
          |-fx-padding: 10;
        """.stripMargin
      visible = false // piilossa oletuksena

      // myöhemmin otetaan käyttöön
      children = Seq(
        new Button("Lisää tapahtuma") {
          disable = true
        },
        new Button("Poista tapahtuma") {
          disable = true
        },
        new Button("Filtteröinti") {
          disable = true
        },
        new Button("Haku") {
          disable = true
        }
      )
    }

    var sideMenuOpen = false

    // Apufunktio sivuvalikon togglaukseen
    def toggleSideMenu(): Unit = {
      sideMenuOpen = !sideMenuOpen
      sideMenu.visible = sideMenuOpen
    }

    // Yläpalkki (sisältää ☰-napin ja päivä/viikko/kuukausi-napit)
    val hamburgerButton = new Button("☰") {
      onAction = _ => toggleSideMenu()
      style =
        """-fx-font-size: 16px;
          |-fx-background-color: transparent;
          |-fx-border-color: transparent;
          |-fx-cursor: hand;
        """.stripMargin
    }

    val dayButton = new Button("Päivä") {
      onAction = _ => rootLayout.center = dayViewPane
      style =
        """-fx-background-color: transparent;
          |-fx-border-color: transparent;
          |-fx-font-size: 14px;
        """.stripMargin
    }

    val weekButton = new Button("Viikko") {
      onAction = _ => rootLayout.center = weekViewPane
      style =
        """-fx-background-color: transparent;
          |-fx-border-color: transparent;
          |-fx-font-size: 14px;
        """.stripMargin
    }

    val monthButton = new Button("Kuukausi") {
      onAction = _ => rootLayout.center = monthViewPane
      style =
        """-fx-background-color: transparent;
          |-fx-border-color: transparent;
          |-fx-font-size: 14px;
        """.stripMargin
    }

    val topBar = new HBox {
      spacing = 20
      alignment = Pos.CenterLeft
      style =
        """-fx-background-color: #f8f8f8;
          |-fx-border-color: #ddd;
          |-fx-padding: 5 15;
        """.stripMargin
      children = Seq(hamburgerButton, dayButton, weekButton, monthButton)
    }

    // Aloitusnäkymä
    rootLayout.left = sideMenu
    rootLayout.top = topBar
    rootLayout.center = monthViewPane

    // Pääikkuna
    stage = new JFXApp3.PrimaryStage {
      title = "Kalenterisovellus"
      scene = new Scene {
        root = rootLayout
        fill = Color.WhiteSmoke
      }
    }
  }
}
