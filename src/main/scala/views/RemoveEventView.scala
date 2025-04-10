package views

import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.geometry.{Insets, Pos}

object RemoveEventView {

  def createRemoveEventView(): BorderPane = {
    val nameField = new TextField {
      promptText = "Hae tapahtumaa nimellä"
    }

    val removeButton = new Button("Poista")
    val cancelButton = new Button("Peruuta")

    val content = new VBox {
      spacing = 10
      alignment = Pos.Center
      padding = Insets(20)
      children = Seq(
        new Label("Poista tapahtuma"),
        nameField,
        new VBox {
          spacing = 10
          alignment = Pos.Center
          children = Seq(removeButton, cancelButton)
        }
      )
    }

    new BorderPane {
      center = content
    }
  }
}
