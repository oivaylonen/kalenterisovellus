package views

import scalafx.scene.control.{Button, Label}
import java.time.{DayOfWeek, LocalDate}
import java.time.format.TextStyle
import java.util.Locale

object ViewUtils {

  // Suomenkielinen kuukauden nimi
  def finnishMonthName(date: LocalDate): String = {
    val monthName = date.getMonth.getDisplayName(TextStyle.FULL_STANDALONE, Locale("fi"))
    monthName.capitalize
  }

  // Suomenkielinen viikonpäivän lyhenne
  def finnishDayName(d: DayOfWeek): String = d match {
    case DayOfWeek.MONDAY    => "MA"
    case DayOfWeek.TUESDAY   => "TI"
    case DayOfWeek.WEDNESDAY => "KE"
    case DayOfWeek.THURSDAY  => "TO"
    case DayOfWeek.FRIDAY    => "PE"
    case DayOfWeek.SATURDAY  => "LA"
    case DayOfWeek.SUNDAY    => "SU"
  }

  // Päiväotsikko "MA 5.4."
  def formatDayLabel(date: LocalDate): String =
    s"${finnishDayName(date.getDayOfWeek)} ${date.getDayOfMonth}.${date.getMonthValue}."

  //  Tuntilabel "00:00"
  def timeLabel(hour: Int): Label = new Label(f"$hour%02d:00") {
    style = "-fx-font-size: 12px; -fx-padding: 2;"
  }

  // Tyyli päivien otsikoille
  val dayHeaderStyle: String =
    """-fx-font-weight: bold;
      |-fx-background-color: #ffffff;
      |-fx-padding: 5;
    """.stripMargin

  // Navigaationappi
  def navButton(text: String)(onClick: => Unit): Button = new Button(text) {
    style =
      """-fx-background-color: transparent;
        |-fx-font-size: 14px;
        |-fx-font-weight: bold;
      """.stripMargin
    onAction = _ => onClick
  }
}
