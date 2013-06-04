package org.livefx.app

import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.control.Label
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ListView
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.event.EventHandler
import javafx.event.ActionEvent
import org.livefx.jfx.Nodes.Implicits._

class DemoTableView extends javafx.application.Application {
  override def start(primaryStage: Stage): Unit = {
    //The primaryStage is the top-level container
    primaryStage.setTitle("example Gui")

    //The BorderPane has the same areas laid out as the
    //BorderLayout layout manager
    val componentLayout = new BorderPane()
    componentLayout.setPadding(new Insets(20,0,20,20))

    //The FlowPane is a conatiner that uses a flow layout
    val choicePane = new FlowPane()
    choicePane.setHgap(100)
    val choiceLbl = new Label("Fruits")

    //The choicebox is populated from an observableArrayList
    val fruits = new ChoiceBox(FXCollections.observableArrayList("Asparagus", "Beans", "Broccoli", "Cabbage"
     , "Carrot", "Celery", "Cucumber", "Leek", "Mushroom"
     , "Pepper", "Radish", "Shallot", "Spinach", "Swede"
     , "Turnip"))

    //Add the label and choicebox to the flowpane
    choicePane.getChildren().add(choiceLbl)
    choicePane.getChildren().add(fruits)

    //put the flowpane in the top area of the BorderPane
    componentLayout.setTop(choicePane)

    val listPane = new FlowPane()
    listPane.setHgap(100)
    val listLbl = new Label("Vegetables")

    val vegetables = new ListView(
        FXCollections.observableArrayList("Apple", "Apricot", "Banana", "Cherry", "Date", "Kiwi", "Orange", "Pear", "Strawberry"))
    listPane.getChildren().add(listLbl)
    listPane.getChildren().add(vegetables)
    listPane.setVisible(false)

    componentLayout.setCenter(listPane)

    //The button uses an inner class to handle the button click event
    val vegFruitBut = new Button("Fruit or Veg")
    vegFruitBut.action.subscribe { event =>
      choicePane.setVisible(!choicePane.isVisible())
      listPane.setVisible(!listPane.isVisible())
    }

    componentLayout.setBottom(vegFruitBut)

    //Add the BorderPane to the Scene
    val scene = new Scene(componentLayout,500,500)

    //Add the Scene to the Stage
    primaryStage.setScene(scene)
    primaryStage.show()
  }
}

object DemoTableView {
  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[Main], args: _*)
  }
}
