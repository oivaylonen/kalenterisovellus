package views

import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.geometry.{Insets, Pos}

object AddEventView {

  def createAddEventView(): BorderPane = {
    val nameField = new TextField {
      promptText = "Tapahtuman nimi"
    }

    val saveButton = new Button("Tallenna")
    val cancelButton = new Button("Peruuta")

    val form = new VBox {
      spacing = 10
      alignment = Pos.Center
      padding = Insets(20)
      children = Seq(
        new Label("Lisää uusi tapahtuma"),
        nameField,
        new VBox {
          spacing = 10
          alignment = Pos.Center
          children = Seq(saveButton, cancelButton)
        }
      )
    }

    new BorderPane {
      center = form
    }
  }
}
