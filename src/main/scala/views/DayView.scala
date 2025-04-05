package views

import scalafx.scene.layout._
import scalafx.scene.control.{Label, ScrollPane}
import scalafx.geometry.{Insets, Pos}
import java.time.LocalDate
import ViewUtils._

object  DayView {

  // Päivänäkymä, tunnit ja tapahtumat omissa sarakkeissa
  def createDayView(selectedDate: LocalDate): BorderPane = {

    var currentDate = selectedDate

    val titleLabel = new Label {
      text = formatDayLabel(currentDate) + s".${currentDate.getYear}"
      style = "-fx-font-size: 16px; -fx-font-weight: bold;"
    }

    val dayGrid = new GridPane {
      padding = Insets(10)
      style = "-fx-background-color: #ffffff; -fx-border-color: #ddd;"
      prefWidth = Double.MaxValue
    }

    dayGrid.columnConstraints.addAll(
      new ColumnConstraints { percentWidth = 15 },
      new ColumnConstraints { percentWidth = 85 }
    )

    for (i <- 0 until 24) {
      dayGrid.rowConstraints.add(new RowConstraints(30))
    }
    for (hour <- 0 until 24) {
      val label = timeLabel(hour)
      label.alignmentInParent = Pos.CenterRight

      val eventBox = new VBox {
        spacing = 2
        style = "-fx-border-color: #eee; -fx-background-color: #ffffff; -fx-padding: 3;"
      }

      dayGrid.add(label, 0, hour)
      dayGrid.add(eventBox, 1, hour)
    }

    def updateDayView(): Unit = {
      val newText = formatDayLabel(currentDate) + s".${currentDate.getYear}"
      titleLabel.text = newText
    }

    // Yläpalkki
    val topBar = new HBox {
      spacing = 20
      alignment = Pos.Center
      padding = Insets(10)
      style = "-fx-background-color: #ffffff; -fx-border-color: #ddd;"
      children = Seq(
        navButton("<") {
          currentDate = currentDate.minusDays(1)
          updateDayView()
        },
        titleLabel,
        navButton(">") {
          currentDate = currentDate.plusDays(1)
          updateDayView()
        }
      )
    }

    // Srollaus pienelle näytölle
    val scrollPane = new ScrollPane {
      content = dayGrid
      fitToWidth = true
      style = "-fx-background-color: #ffffff; -fx-border-color: #ddd;"
    }

    // Asettelu
    new BorderPane {
      top = topBar
      center = scrollPane
      style = "-fx-background-color: #ffffff;"
    }
  }
}
