package views

import scalafx.Includes.*
import scalafx.scene.control.{ChoiceBox, Label, ScrollPane}
import scalafx.scene.layout.{BorderPane, GridPane, ColumnConstraints, VBox}
import scalafx.geometry.{Insets, Pos}
import scalafx.collections.ObservableBuffer
import logic.CalendarData
import java.time.{Duration, LocalDate, DayOfWeek}

object ReportView:

  // Ajanjaksot, joita voi käsitellä
  private enum Period(val label: String):
    case All        extends Period("Kaikki")
    case ThisWeek   extends Period("Tämä viikko")
    case ThisMonth  extends Period("Tämä kuukausi")

  // Raporttinäkymän luonti
  def createReportView(): BorderPane =

    // Ajanjakson valintaboksi
    val periodBox = new ChoiceBox[String] {
      items = ObservableBuffer(Period.values.map(_.label)*)
      value = Period.ThisWeek.label //oletus
    }

    // taulukko raporttitiedoille
    val grid = new GridPane:
      padding = Insets(10)
      hgap    = 12
      vgap    = 6
      style   = "-fx-background-color:#ffffff;-fx-border-color:#dddddd;"
      columnConstraints ++= Seq(
        new ColumnConstraints { percentWidth = 60 },  // kategoria
        new ColumnConstraints { percentWidth = 20 },  // lukumäärä
        new ColumnConstraints { percentWidth = 20 }   // tunnit
      )

    // Otsikko ja valintaboksi
    val title = new Label("Yhteenvetoraportti"):
      style = "-fx-font-size:16px;-fx-font-weight:bold;"

    val header = new VBox(10, title, periodBox):
      padding   = Insets(14)
      alignment = Pos.TopLeft

    // laskee käsiteltävän ajanjakson
    def periodBounds(per: Period): (LocalDate, LocalDate) = per match
      case Period.All =>
        (LocalDate.MIN, LocalDate.MAX)
      case Period.ThisWeek =>
        val today   = LocalDate.now()
        val monday  = today.`with`(DayOfWeek.MONDAY)
        val sunday  = monday.plusDays(6)
        (monday, sunday)
      case Period.ThisMonth =>
        val today   = LocalDate.now()
        val first   = today.withDayOfMonth(1)
        val last    = today.plusMonths(1).withDayOfMonth(1).minusDays(1)
        (first, last)

    // tyhjentää ja täyttää taulukon uusilla arvoilla
    def refreshTable(): Unit =
      grid.children.clear()

      // taulukon otsikkorivi
      grid.add(new Label("Kategoria") { style="-fx-font-weight:bold;" }, 0, 0)
      grid.add(new Label("Tapahtumia") { style="-fx-font-weight:bold;" }, 1, 0)
      grid.add(new Label("Tunnit")     { style="-fx-font-weight:bold;" }, 2, 0)

      // selvitetään valittu kuukausi
      val selectedPeriod = Period.values.find(_.label == periodBox.value.value).getOrElse(Period.ThisWeek)
      val (fromD, toD) = periodBounds(selectedPeriod)

      // valitaan ajanjakson tapahtumat ja ryhmitellään kategorian mukaan
      val data = CalendarData.getAllEvents
        .filter(e =>
          !e.endTime.toLocalDate.isBefore(fromD) &&
          !e.startTime.toLocalDate.isAfter(toD)
        )
        .groupBy(_.category.name)
        .map { case (cat, evts) =>
          // lasketaan tapahtumien määrä ja tuntimäärä
          val count = evts.size
          val hours = evts.map { e =>
            val start = if e.startTime.toLocalDate.isBefore(fromD) then fromD.atStartOfDay() else e.startTime
            val end   = if e.endTime.toLocalDate.isAfter(toD) then toD.plusDays(1).atStartOfDay() else e.endTime
            Duration.between(start, end).toMinutes.toDouble / 60.0
          }.sum
          (cat, count, hours)
        }
        .toSeq
        // kategorioiden lajittelu aakkosiin
        .sortBy(_._1.toLowerCase)

      // täytetään uusi data taulukkoon
      for (((cat, count, hours), row) <- data.zipWithIndex) do
        grid.add(new Label(cat),            0, row + 1)
        grid.add(new Label(count.toString), 1, row + 1)
        grid.add(new Label(f"$hours%.1f"),  2, row + 1)

    // kun ajanjaksoa muutetaan päivitetään taulukko
    periodBox.onAction = _ => refreshTable()
    // alussa heti kutsu
    refreshTable()

    // Lopullinen asettelu
    new BorderPane:
      top    = header
      center = new ScrollPane { content = grid; fitToWidth = true }
      style  = "-fx-background-color:#ffffff;"
end ReportView
