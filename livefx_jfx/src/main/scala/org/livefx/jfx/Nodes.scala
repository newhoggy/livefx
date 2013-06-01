package org.livefx.jfx

import javafx.scene.control.ButtonBase
import org.livefx.Events
import javafx.event.ActionEvent

object Nodes {
  object Implicits {
    implicit class RichButtonBase(self: ButtonBase) {
      def actions: Events[ActionEvent] = EventsWithEventHandler.on(self.onActionProperty())
    }
  }
}
