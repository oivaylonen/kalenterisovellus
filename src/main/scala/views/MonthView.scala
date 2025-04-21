package views

import scalafx.scene.layout.*
import scalafx.scene.control.{Label, ScrollPane}
import scalafx.geometry.{Insets, Pos, HPos}
import java.time.{YearMonth, LocalDate}
import ViewUtils.*
import logic.CalendarService

object MonthView:

  // onDayClicked kutsutaan kun päivää klikataan
  // acitiveCats on aktiivisten kategorioiden lista
  def createMonthView(onDayClicked: LocalDate => Unit, activeCats: Set[String]): BorderPane =

    var ym = YearMonth.now()
    val title = new Label { style = "-fx-font-size:16px;-fx-font-weight:bold;" }

    // ruudukko kuukaudelle
    val grid = new GridPane:
      alignment = Pos.TopCenter
      hgap = 6; vgap = 6; padding = Insets(20)
      prefWidth = Double.MaxValue

    // lisätään sarakkeet viikonpäiville, 7kpl
    for _ <- 0 until 7 do
      grid.columnConstraints.add(new ColumnConstraints:
        percentWidth = 100.0 / 7; halignment = HPos.Center)

    // Lisätään 6 riviä, eli maksimi tarve
    for _ <- 0 until 6 do grid.rowConstraints.add(RowConstraints())

    // Rakentaa näkymän, kutsutaan päivittämisen yhteydessä
    def refresh(): Unit =
      title.text = s"${finnishMonthName(ym.atDay(1))} ${ym.getYear}"
      grid.children.clear()

      // Viikonpäivät ylös riville 0
      val w = Seq("MA", "TI", "KE", "TO", "PE", "LA", "SU")
      w.zipWithIndex.foreach { (n, i) =>
        grid.add(new Label(n) { style = "-fx-font-weight:bold;" }, i, 0)
      }

      // Päivien asettelu
      val days = ym.lengthOfMonth()
      val firstIdx = ym.atDay(1).getDayOfWeek.getValue
      var d = 1; var row = 1; var col = firstIdx - 1

      // Luodaan päivälle boksi ja laitetaan päivän tapahtumat sen sisään
      while d <= days do
        val dayDate = ym.atDay(d)
        val cell = new VBox:
          spacing = 3
          alignment = Pos.TopLeft
          padding = Insets(4)
          prefHeight = 120
          style = "-fx-border-color:#cccccc;-fx-background-color:#ffffff;-fx-cursor:hand;"
          onMouseClicked = _ => onDayClicked(dayDate) //Siirytään päivänäkymään klikatessa

        // Päivänumeron lisääminen
        cell.children.add(new Label(d.toString) { style = "-fx-font-weight:bold;" })

        // haetaan tapatumat ja suodatetaan aktiivisten tapahtumien mukaan
        val evs = CalendarService
          .eventsBetween(dayDate.atStartOfDay(), dayDate.plusDays(1).atStartOfDay().minusSeconds(1))
          .filter(e => activeCats.isEmpty || activeCats.contains(e.category.name))

        // näytetään vain kolme ensimmäistä
        evs.take(3).foreach(e => cell.children.add(new Label(e.name)))

        // asetetaan oikeaan paikkaan
        grid.add(cell, col, row)

        // seuraavaan päivään siirtyminen
        d += 1; col += 1
        if col > 6 then { col = 0; row += 1 }
      end while

    // Navigaatio kuukausien välillä liikkumiseen
    val nav = new HBox:
      spacing = 20; alignment = Pos.Center; padding = Insets(10)
      style = "-fx-background-color:#ffffff;-fx-border-color:#dddddd;"
      children = Seq(
        navButton("<") { ym = ym.minusMonths(1); refresh() },
        title,
        navButton(">") { ym = ym.plusMonths(1); refresh() }
      )

    // Asettelu
    new BorderPane:
      top = nav
      center = new ScrollPane { content = grid; fitToWidth = true }
      style = "-fx-background-color:#ffffff;"
      refresh()
end MonthView
