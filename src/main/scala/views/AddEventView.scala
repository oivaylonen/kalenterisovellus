package views

import scalafx.scene.layout.*
import scalafx.scene.control.*
import scalafx.geometry.{Insets, Pos}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scalafx.scene.paint.Color
import logic.{CalendarService, CalendarData}
import `type`.{Event, Category}
import scalafx.collections.ObservableBuffer

object  AddEventView:

  // Ajan formaatti
  private val dateTimeFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

  // Luodaan lomakenäkymä, joka kerää tapahtuman tiedot
  def createAddEventView(onSaved: () => Unit): BorderPane =

    // Lomakkeen pakolliset kentät
    val nameField       = new TextField { promptText = "Tapahtuman nimi*" }
    val startDTField    = new TextField { promptText = "Alku (dd.MM.yyyy HH:mm)*" }
    val endDTField      = new TextField { promptText = "Loppu (dd.MM.yyyy HH:mm)*" }

    // Valikko nykyisistä kategorioista
    val categoryNames   = CalendarData.getAllCategories.map(_.name).toSeq
    val catCombo        = new ComboBox[String](ObservableBuffer.from(categoryNames))
    catCombo.promptText = "Valitse kategoria"

    // Lomakkeen valinnaiset kentät
    val newCatField     = new TextField { promptText = "Uusi kategoria (valinnainen)" }
    val descField       = new TextField { promptText = "Kuvaus (valinnainen)" }
    val reminderBox     = new CheckBox("Muistuta tästä")
    val overLap         = new CheckBox("Salli päällekkäisyys")

    // Virheviestin kenttä
    val errLabel        = new Label { style = "-fx-text-fill:red;" }

    // Rajataan kalenterin aikavaraukset 15 minuutin väleille
    def minuteOk(t: LocalDateTime): Boolean = t.getMinute % 15 == 0

    // Aika localdatetime muotoon
    def parseDT(s: String): LocalDateTime =
      LocalDateTime.parse(s, dateTimeFmt)

    // Tarkistaa syötteen oikeellsiuuden ja tallentaa tapahtuman
    def saveAction(): Unit =
      try
        val name = nameField.text.value.trim
        if name.isEmpty then
          errLabel.text = "Nimi puuttuu"; return

        val startDT = parseDT(startDTField.text.value.trim)
        val endDT   = parseDT(endDTField.text.value.trim)

        if endDT.isBefore(startDT) then
          errLabel.text = "Loppu ennen alkua"; return
        if !minuteOk(startDT) || !minuteOk(endDT) then
          errLabel.text = "Aikojen oltava 15min välein"; return

        // Valitaan kategoria kentästä tai valikosta
        val catName =
          if newCatField.text.value.trim.nonEmpty then newCatField.text.value.trim
          else Option(catCombo.value.value).map(_.trim).filter(_.nonEmpty).getOrElse("Yleinen")

        // Kategorian värin määrittäminen
        val tempCat = Category(catName, Color.Black)
        CalendarData.addCategory(tempCat)
        val cat = CalendarData.getAllCategories.find(_.name.equalsIgnoreCase(catName)).getOrElse(tempCat)


        val newEv = Event(
          name        = name,
          startTime   = startDT,
          endTime     = endDT,
          description = descField.text.value.trim,
          remainder   = reminderBox.selected.value,
          category    = cat,
          allDay      = false
        )

        CalendarService.addEventValidated(newEv, allowOverlap = overLap.selected.value) match
          case Left(msg)  => errLabel.text = msg
          case Right(()) =>
            // tyhjennetään kentät ja boksit
            errLabel.text = ""

            Seq(nameField, startDTField, endDTField, newCatField, descField)
              .foreach(_.text = "")
            reminderBox.selected = false
            overLap.selected = false
            catCombo.value = null
            // Poistutaan lisää tapahtuma näkymästä
            onSaved()
      catch
        // Yleinen virheilmoitus lopuille käsittelemättömille virheille
        case _: Exception => errLabel.text = "Virheellinen syöte"

    // Tallennus ja poistumis napit
    val saveBtn   = new Button("Tallenna") { onAction = _ => saveAction() }
    val cancelBtn = new Button("Sulje")    { onAction = _ => onSaved() }

    // Lomakkeen boksi 
    val form = new VBox:
      spacing = 10; alignment = Pos.Center; padding = Insets(20)
      children = Seq(
        new Label("Lisää tapahtuma") { style = "-fx-font-size:14px;-fx-font-weight:bold;" },
        nameField, startDTField, endDTField,
        new Label("Kategoria (valitse tai luo uusi)"),
        catCombo, newCatField,
        descField, reminderBox,
        overLap,
        errLabel,
        new HBox(10, saveBtn, cancelBtn)
      )
    // Asettelu 
    new BorderPane:
      center = form
      style = "-fx-background-color:#ffffff;"
end AddEventView
