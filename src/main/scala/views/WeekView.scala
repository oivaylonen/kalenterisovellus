package views

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{BorderPane, GridPane, HBox, Pane, VBox}
import scalafx.geometry.{Insets, Pos}
import java.time.{DayOfWeek, LocalDate}
import java.time.format.TextStyle
import java.util.Locale

object WeekView extends JFXApp3 {

  override def start(): Unit = {

    var currentMonday: LocalDate = LocalDate.now().`with`(DayOfWeek.MONDAY)

    def finnishDayName(d: DayOfWeek): String = d match {
      case DayOfWeek.MONDAY    => "MA"
      case DayOfWeek.TUESDAY   => "TI"
      case DayOfWeek.WEDNESDAY => "KE"
      case DayOfWeek.THURSDAY  => "TO"
      case DayOfWeek.FRIDAY    => "PE"
      case DayOfWeek.SATURDAY  => "LA"
      case DayOfWeek.SUNDAY    => "SU"
    }

    def finnishMonthName(date: LocalDate): String = {
      val base = date.getMonth.getDisplayName(TextStyle.FULL, Locale("fi"))
      base.capitalize
    }

    val titleLabel = new Label()

    val weekGrid = new GridPane {
      alignment = Pos.Center
      hgap = 10
      padding = Insets(10)
    }

    def updateWeekView(): Unit = {
      // Yläotsikko
      titleLabel.text = s"${finnishMonthName(currentMonday)} – ${currentMonday.getYear}"

      // Ruudukon tyhjennys
      weekGrid.children.clear()

      // Sarakkeet päiville
      for (i <- 0 until 7) {
        val date = currentMonday.plusDays(i)
        // esim. MA 24.3.
        val dayText = s"${finnishDayName(date.getDayOfWeek)} ${date.getDayOfMonth}.${date.getMonthValue}."

        val dayBox = new VBox {
          spacing = 5
          alignment = Pos.TopCenter
          children = Seq(
            new Label(dayText) {
              style = "-fx-font-weight: bold; -fx-font-size: 14px;"
            },
            new Pane {
              prefWidth = 150
              prefHeight = 150
              style =
                """-fx-border-color: gray;
                  |-fx-background-color: #e0e0e0;
                """.stripMargin
            }
          )
        }

        // dayBox sarakkeeseen i
        weekGrid.add(dayBox, i, 0)
      }
    }

    // Napit viikon selaamiseen
    val prevWeekButton = new Button("<") {
      onAction = _ => {
        currentMonday = currentMonday.minusWeeks(1)
        updateWeekView()
      }
    }
    val nextWeekButton = new Button(">") {
      onAction = _ => {
        currentMonday = currentMonday.plusWeeks(1)
        updateWeekView()
      }
    }

    // Yläpalkki
    val topBar = new HBox {
      spacing = 20
      alignment = Pos.Center
      padding = Insets(10)
      children = Seq(prevWeekButton, titleLabel, nextWeekButton)
    }

    val mainLayout = new BorderPane {
      top = topBar
      center = weekGrid
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Viikkonäkymä"
      scene = new Scene {
        root = mainLayout
      }
    }

    updateWeekView()
  }
}
