package views

import scalafx.scene.layout._
import scalafx.scene.control.{Label, ScrollPane}
import scalafx.geometry.{Insets, Pos, HPos}
import java.time.{YearMonth, LocalDate}
import ViewUtils._

object MonthView {

  // Kuukausinäkymä, päivää klikkaamalla päivänäkymään
  def createMonthView(onDayClicked: LocalDate => Unit): BorderPane = {

    var currentYearMonth: YearMonth = YearMonth.now()

    val titleLabel = new Label {
      style = "-fx-font-size: 16px; -fx-font-weight: bold;"
    }

    // Ruudukko, jossa 7 saraketta ja max 6 riviä
    val monthGrid = new GridPane {
      alignment = Pos.TopCenter
      hgap = 5
      vgap = 5
      padding = Insets(20)
      prefWidth = Double.MaxValue
    }

    // 7 saraketta yhtä leveinä
    for (_ <- 0 until 7) {
      monthGrid.columnConstraints.add(new ColumnConstraints {
        percentWidth = 100.0 / 7
        halignment = HPos.Center
      })
    }

    // Päivitetään näkymä ruudukkoon
    def updateMonthView(): Unit = {
      // Otsikko
      val firstDayOfMonth = currentYearMonth.atDay(1)
      titleLabel.text = s"${finnishMonthName(firstDayOfMonth)} ${currentYearMonth.getYear}"

      // Tyhjennys
      monthGrid.children.clear()
      monthGrid.rowConstraints.clear()

      // Päivämäärien laskeminen
      val daysInMonth = currentYearMonth.lengthOfMonth()
      val firstDayIndex = firstDayOfMonth.getDayOfWeek.getValue // 1..7

      // Otsikkorivi
      val weekdays = Seq("MA", "TI", "KE", "TO", "PE", "LA", "SU")
      weekdays.zipWithIndex.foreach { case (name, i) =>
        monthGrid.add(new Label(name) {
          style = "-fx-font-weight: bold;"
        }, i, 0)
      }

      // Oletetaan max 6 viikkoa
      for (_ <- 0 until 6) {
        monthGrid.rowConstraints.add(new RowConstraints { vgrow = Priority.Always })
      }

      // Sijoitetaan Vbox jokaiseen päivään
      var dayCounter = 1
      var row = 1
      var col = firstDayIndex - 1

      while (dayCounter <= daysInMonth) {
        val date = currentYearMonth.atDay(dayCounter)

        // Yhden päivän laatikko
        val box = new VBox {
          spacing = 3
          alignment = Pos.TopLeft
          padding = Insets(5)
          prefHeight = 120
          vgrow = Priority.Always
          style = "-fx-border-color: #ccc; -fx-background-color: #ffffff; -fx-cursor: hand;"
          onMouseClicked = _ => onDayClicked(date)
        }
        box.children.add(new Label(dayCounter.toString) {
          style = "-fx-font-weight: bold;"
        })

        monthGrid.add(box, col, row)

        dayCounter += 1
        col += 1
        if (col > 6) {
          col = 0
          row += 1
        }
      }
    }

    // Yläpalkki
    val topBar = new HBox {
      spacing = 20
      alignment = Pos.Center
      padding = Insets(10)
      style = "-fx-background-color: #ffffff; -fx-border-color: #ddd;"
      children = Seq(
        navButton("<") {
          currentYearMonth = currentYearMonth.minusMonths(1)
          updateMonthView()
        },
        titleLabel,
        navButton(">") {
          currentYearMonth = currentYearMonth.plusMonths(1)
          updateMonthView()
        }
      )
    }

    // Skrollaus mahdolista pystysuunnassa
    val scrollPane = new ScrollPane {
      content = monthGrid
      fitToWidth = true
      style = "-fx-background-color: #ffffff; -fx-border-color: #ddd;"
    }
    // Lopullinen asettelun määritys
    val layout = new BorderPane {
      top = topBar
      center = scrollPane
      style = "-fx-background-color: #ffffff;"
    }

    updateMonthView()
    layout
  }
}
