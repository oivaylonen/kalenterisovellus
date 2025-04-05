package views

import scalafx.scene.layout._
import scalafx.scene.control.{Label, ScrollPane}
import scalafx.geometry.{Insets, Pos}
import java.time.{DayOfWeek, LocalDate}
import ViewUtils._

object WeekView {

  // Viikkonäkymä, tunnit ja viikonpäivät omissa sarakkeissa
  // Päiväotsikosta klikkaamalla päästään päivänäkymään
  def createWeekView(onDayClicked: LocalDate => Unit): BorderPane = {

    var currentMonday: LocalDate = LocalDate.now().`with`(DayOfWeek.MONDAY)

    val titleLabel = new Label {
      style = "-fx-font-size: 16px; -fx-font-weight: bold;"
    }
    // Ruudukko, jonne viikkonäkymän elementit lisätään
    val weekGrid = new GridPane {
      alignment = Pos.Center
      padding = Insets(10)
      style = "-fx-background-color: #ffffff; -fx-border-color: #ddd;"
      prefWidth = Double.MaxValue
    }

    // Sarakkeet
    // ensimmäinen sarake on tunnit, siten ma-su päivät
    val colHours = new ColumnConstraints { percentWidth = 10 }
    weekGrid.columnConstraints.add(colHours)

    for (_ <- 1 to 7) {
      val col = new ColumnConstraints { percentWidth = 90.0 / 7 }
      weekGrid.columnConstraints.add(col)
    }

    // Rivit
    // ensimmäinen otsikkorivi, sitten 1..24 = tunnit
    // Kehitä ehkä vielä koko päivä sarake
    for (_ <- 0 to 24) {
      weekGrid.rowConstraints.add(new RowConstraints(30))
    }

    // Tilan piirtämiseen ja päivittämiseen funktio
    def updateWeekView(): Unit = {
      val year = currentMonday.getYear
      titleLabel.text = s"${finnishMonthName(currentMonday)} $year"

      weekGrid.children.clear()

      // Tunneille otsikko
      weekGrid.add(new Label("Tunnit") {
        style = "-fx-font-weight: bold; -fx-font-size: 12px;"
      }, 0, 0)

      // Päiville otsikko
      for (i <- 0 until 7) {
        val date = currentMonday.plusDays(i)
        val dayLabel = new Label(formatDayLabel(date)) {
          style = dayHeaderStyle
          onMouseClicked = _ => onDayClicked(date)
        }
        weekGrid.add(dayLabel, i + 1, 0)
      }

      // Tuntirivit
      for (hour <- 0 until 24) {
        weekGrid.add(timeLabel(hour), 0, hour + 1)

        //Päiville boksit
        for (d <- 0 until 7) {
          val cell = new VBox {
            spacing = 2
            style = "-fx-border-color: #eee; -fx-background-color: #ffffff; -fx-padding: 3;"
          }
          weekGrid.add(cell, d + 1, hour + 1)
        }
      }
    }

    // Yläpalkki viikkojen välillä liikkumiseen
    val topBar = new HBox {
      spacing = 20
      alignment = Pos.Center
      padding = Insets(10)
      style = "-fx-background-color: #ffffff; -fx-border-color: #ddd;"
      children = Seq(
        navButton("<") {
          currentMonday = currentMonday.minusWeeks(1)
          updateWeekView()
        },
        titleLabel,
        navButton(">") {
          currentMonday = currentMonday.plusWeeks(1)
          updateWeekView()
        }
      )
    }

    // Skrollaus mahdolista pystysuunnassa
    val scrollPane = new ScrollPane {
      content = weekGrid
      fitToWidth = true
      style = "-fx-background-color: #ffffff; -fx-border-color: #ddd;"
    }

    // Lopullinen asettelun määritys
    val layout = new BorderPane {
      top = topBar
      center = scrollPane
      style = "-fx-background-color: #ffffff;"
    }

    updateWeekView()
    layout
  }
}
