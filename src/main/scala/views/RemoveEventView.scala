package views

import scalafx.scene.layout.*
import scalafx.scene.control.*
import scalafx.geometry.{Insets, Pos}
import logic.{CalendarService, CalendarData}
import scala.jdk.CollectionConverters.*
import scalafx.Includes.*

object RemoveEventView:


  def createRemoveEventView(onChange: () => Unit): BorderPane =

    val searchField = new TextField { promptText = "Hae nimellä" }
    val listView    = new ListView[String]

    // Päivittää listan sisällön
    def updateList(): Unit =

      val term = searchField.text.value.trim.toLowerCase
      val items = CalendarData.getAllEvents
        .filter(e => e.name.toLowerCase.contains(term))
        .map(e => s"${e.name} | ${e.startTime.toLocalDate}")
      // Muutetaan buffer javaan yhteensopivaksi
      listView.items = javafx.collections.FXCollections.observableArrayList(items.toSeq.asJava)

    // Kun hakukenttää muutetaan päivitetään lista samalla
    searchField.text.onChange { (_, _, _) => updateList() }

    // Poistonappi, aluksi pois päältä
    val removeBtn = new Button("Poista"):
      disable = true
      onAction = _ =>
        val sel = listView.getSelectionModel.getSelectedItem
        if sel != null then
          val name = sel.takeWhile(_ != '|').trim
          // Etsitään ensimmäinen tapahtuma joka vastaa valittua
          CalendarData.getAllEvents.find(_.name == name).foreach { ev =>
            CalendarService.removeEvent(ev) // poistetaan tapahtuma kokonaan
            onChange() // päitetään näkymät
            updateList() // listan päivitys
          }

    // seuraa muutosta valitussa rivissä
    listView.getSelectionModel.selectedItemProperty.onChange { (_, _, newVal) =>
      removeBtn.disable = (newVal == null)
    }

    // Poista-lomakkeen boksi
    val view = new VBox:
      spacing = 10; alignment = Pos.Center; padding = Insets(20)
      children = Seq(
        new Label("Poista tapahtuma") { style = "-fx-font-size:14px;-fx-font-weight:bold;" },
        searchField, listView, removeBtn
      )

    // Asettelu
    new BorderPane:
      center = view
      style = "-fx-background-color:#ffffff;"
end RemoveEventView
