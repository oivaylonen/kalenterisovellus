package views

import scalafx.scene.layout.*
import scalafx.scene.control.*
import scalafx.geometry.{Insets, Pos}
import logic.{CalendarData, FilterState}

object FilterView:

  def createFilterView(onUpdated: () => Unit): BorderPane =
    // Tehdään kaikille kategorioille oma chekbox
    val checkboxes = CalendarData.getAllCategories.map { cat =>
      val cb = new CheckBox(cat.name):
        selected = FilterState.activeCategories.contains(cat.name)
        // Lisätään/poistetaan kategoria valituista
        onAction = _ =>
          if selected.value then FilterState.activeCategories += cat.name
          else FilterState.activeCategories -= cat.name
          onUpdated() //Päivitetään näkymät
      cb
    }

    // Kategoriaboksit saman boksin sisään
    val box = new VBox:
      spacing = 6
      padding = Insets(20)
      alignment = Pos.TopLeft
      children = Seq(
        new Label("Valitse näkyvät kategoriat") { style = "-fx-font-weight:bold;" }
      ) ++ checkboxes

    // Lopullinen asettelu
    new BorderPane:
      center = box
      style = "-fx-background-color:#ffffff;"
end FilterView
