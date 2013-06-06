package org.livefx.jfx

import org.livefx.Events

object Nodes {
  object Implicits {
    implicit class RichNode(self: javafx.scene.Node) {
      // skipped 6 clipProperty
      def contextMenuRequested: Events[javafx.scene.input.ContextMenuEvent] = {
        val handler = self.onContextMenuRequestedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.ContextMenuEvent]
          self.onContextMenuRequestedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.ContextMenuEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def inputMethodTextChanged: Events[javafx.scene.input.InputMethodEvent] = {
        val handler = self.onInputMethodTextChangedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.InputMethodEvent]
          self.onInputMethodTextChangedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.InputMethodEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      // skipped 6 parentPropertyImpl
      // skipped 6 scenePropertyImpl
      // skipped 6 cursorProperty
      // skipped 6 blendModeProperty
      // skipped 6 cacheHintProperty
      // skipped 6 effectProperty
      // skipped 6 depthTestProperty
      def zoom: Events[javafx.scene.input.ZoomEvent] = {
        val handler = self.onZoomProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.ZoomEvent]
          self.onZoomProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.ZoomEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def dragEntered: Events[javafx.scene.input.DragEvent] = {
        val handler = self.onDragEnteredProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.DragEvent]
          self.onDragEnteredProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.DragEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def dragExited: Events[javafx.scene.input.DragEvent] = {
        val handler = self.onDragExitedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.DragEvent]
          self.onDragExitedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.DragEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def dragOver: Events[javafx.scene.input.DragEvent] = {
        val handler = self.onDragOverProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.DragEvent]
          self.onDragOverProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.DragEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def dragDropped: Events[javafx.scene.input.DragEvent] = {
        val handler = self.onDragDroppedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.DragEvent]
          self.onDragDroppedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.DragEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def dragDone: Events[javafx.scene.input.DragEvent] = {
        val handler = self.onDragDoneProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.DragEvent]
          self.onDragDoneProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.DragEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      // skipped 6 rotationAxisProperty
      def mouseClicked: Events[javafx.scene.input.MouseEvent] = {
        val handler = self.onMouseClickedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseEvent]
          self.onMouseClickedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mouseDragged: Events[javafx.scene.input.MouseEvent] = {
        val handler = self.onMouseDraggedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseEvent]
          self.onMouseDraggedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mouseEntered: Events[javafx.scene.input.MouseEvent] = {
        val handler = self.onMouseEnteredProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseEvent]
          self.onMouseEnteredProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mouseExited: Events[javafx.scene.input.MouseEvent] = {
        val handler = self.onMouseExitedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseEvent]
          self.onMouseExitedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mouseMoved: Events[javafx.scene.input.MouseEvent] = {
        val handler = self.onMouseMovedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseEvent]
          self.onMouseMovedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mousePressed: Events[javafx.scene.input.MouseEvent] = {
        val handler = self.onMousePressedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseEvent]
          self.onMousePressedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mouseReleased: Events[javafx.scene.input.MouseEvent] = {
        val handler = self.onMouseReleasedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseEvent]
          self.onMouseReleasedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def dragDetected: Events[javafx.scene.input.MouseEvent] = {
        val handler = self.onDragDetectedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseEvent]
          self.onDragDetectedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mouseDragOver: Events[javafx.scene.input.MouseDragEvent] = {
        val handler = self.onMouseDragOverProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseDragEvent]
          self.onMouseDragOverProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseDragEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mouseDragReleased: Events[javafx.scene.input.MouseDragEvent] = {
        val handler = self.onMouseDragReleasedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseDragEvent]
          self.onMouseDragReleasedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseDragEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mouseDragEntered: Events[javafx.scene.input.MouseDragEvent] = {
        val handler = self.onMouseDragEnteredProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseDragEvent]
          self.onMouseDragEnteredProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseDragEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def mouseDragExited: Events[javafx.scene.input.MouseDragEvent] = {
        val handler = self.onMouseDragExitedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.MouseDragEvent]
          self.onMouseDragExitedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.MouseDragEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def scrollStarted: Events[javafx.scene.input.ScrollEvent] = {
        val handler = self.onScrollStartedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.ScrollEvent]
          self.onScrollStartedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.ScrollEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def scroll: Events[javafx.scene.input.ScrollEvent] = {
        val handler = self.onScrollProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.ScrollEvent]
          self.onScrollProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.ScrollEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def scrollFinished: Events[javafx.scene.input.ScrollEvent] = {
        val handler = self.onScrollFinishedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.ScrollEvent]
          self.onScrollFinishedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.ScrollEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def rotationStarted: Events[javafx.scene.input.RotateEvent] = {
        val handler = self.onRotationStartedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.RotateEvent]
          self.onRotationStartedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.RotateEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def rotate: Events[javafx.scene.input.RotateEvent] = {
        val handler = self.onRotateProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.RotateEvent]
          self.onRotateProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.RotateEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def rotationFinished: Events[javafx.scene.input.RotateEvent] = {
        val handler = self.onRotationFinishedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.RotateEvent]
          self.onRotationFinishedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.RotateEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def zoomStarted: Events[javafx.scene.input.ZoomEvent] = {
        val handler = self.onZoomStartedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.ZoomEvent]
          self.onZoomStartedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.ZoomEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def zoomFinished: Events[javafx.scene.input.ZoomEvent] = {
        val handler = self.onZoomFinishedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.ZoomEvent]
          self.onZoomFinishedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.ZoomEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def swipeUp: Events[javafx.scene.input.SwipeEvent] = {
        val handler = self.onSwipeUpProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.SwipeEvent]
          self.onSwipeUpProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.SwipeEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def swipeDown: Events[javafx.scene.input.SwipeEvent] = {
        val handler = self.onSwipeDownProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.SwipeEvent]
          self.onSwipeDownProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.SwipeEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def swipeLeft: Events[javafx.scene.input.SwipeEvent] = {
        val handler = self.onSwipeLeftProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.SwipeEvent]
          self.onSwipeLeftProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.SwipeEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def swipeRight: Events[javafx.scene.input.SwipeEvent] = {
        val handler = self.onSwipeRightProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.SwipeEvent]
          self.onSwipeRightProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.SwipeEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def touchPressed: Events[javafx.scene.input.TouchEvent] = {
        val handler = self.onTouchPressedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.TouchEvent]
          self.onTouchPressedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.TouchEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def touchMoved: Events[javafx.scene.input.TouchEvent] = {
        val handler = self.onTouchMovedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.TouchEvent]
          self.onTouchMovedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.TouchEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def touchReleased: Events[javafx.scene.input.TouchEvent] = {
        val handler = self.onTouchReleasedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.TouchEvent]
          self.onTouchReleasedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.TouchEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def touchStationary: Events[javafx.scene.input.TouchEvent] = {
        val handler = self.onTouchStationaryProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.TouchEvent]
          self.onTouchStationaryProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.TouchEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def keyPressed: Events[javafx.scene.input.KeyEvent] = {
        val handler = self.onKeyPressedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.KeyEvent]
          self.onKeyPressedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.KeyEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def keyReleased: Events[javafx.scene.input.KeyEvent] = {
        val handler = self.onKeyReleasedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.KeyEvent]
          self.onKeyReleasedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.KeyEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      def keyTyped: Events[javafx.scene.input.KeyEvent] = {
        val handler = self.onKeyTypedProperty().getValue()
        if (handler == null) {
          val events = new EventsWithEventHandler[javafx.scene.input.KeyEvent]
          self.onKeyTypedProperty().setValue(events)
          events
        } else {
          if (handler.isInstanceOf[EventsWithEventHandler[_]]) {
            handler.asInstanceOf[EventsWithEventHandler[javafx.scene.input.KeyEvent]]
          } else {
            throw new UnsupportedOperationException("event handler already installed")
          }
        }
      }
      // skipped 6 inputMethodRequestsProperty
      // skipped 6 eventDispatcherProperty
    }

    implicit class RichScatterChart[X, Y](self: javafx.scene.chart.ScatterChart[X, Y]) {
    }

    implicit class RichArc(self: javafx.scene.shape.Arc) {
      // skipped 6 typeProperty
    }

    implicit class RichStackedAreaChart[X, Y](self: javafx.scene.chart.StackedAreaChart[X, Y]) {
    }

    implicit class RichAxis[T](self: javafx.scene.chart.Axis[T]) {
      // skipped 6 sideProperty
      // skipped 6 labelProperty
      // skipped 6 tickLabelFontProperty
      // skipped 6 tickLabelFillProperty
    }

    implicit class RichTableRow[T](self: javafx.scene.control.TableRow[T]) {
      // skipped 5 tableViewPropertyImpl
    }

    implicit class RichCircle(self: javafx.scene.shape.Circle) {
    }

    implicit class RichSplitMenuButton(self: javafx.scene.control.SplitMenuButton) {
    }

    implicit class RichMenuButton(self: javafx.scene.control.MenuButton) {
      // skipped 6 popupSideProperty
    }

    implicit class RichPieChart(self: javafx.scene.chart.PieChart) {
      // skipped 5 dataProperty
    }

    implicit class RichTableView[S](self: javafx.scene.control.TableView[S]) {
      // skipped 5 selectionModelProperty
      // skipped 5 focusModelProperty
      // skipped 5 itemsProperty
      // skipped 6 columnResizePolicyProperty
      // skipped 6 rowFactoryProperty
      // skipped 6 placeholderProperty
      // skipped 0 editingCellPropertyImpl
    }

    implicit class RichComboBox[T](self: javafx.scene.control.ComboBox[T]) {
      // skipped 5 converterProperty
      // skipped 5 selectionModelProperty
      // skipped 6 cellFactoryProperty
      // skipped 5 itemsProperty
      // skipped 5 buttonCellProperty
    }

    implicit class RichAnchorPane(self: javafx.scene.layout.AnchorPane) {
    }

    implicit class RichTreeCell[T](self: javafx.scene.control.TreeCell[T]) {
      // skipped 6 disclosureNodeProperty
    }

    implicit class RichProgressBar(self: javafx.scene.control.ProgressBar) {
    }

    implicit class RichCheckBoxTableCell[S, T](self: javafx.scene.control.cell.CheckBoxTableCell[S, T]) {
      // skipped 5 converterProperty
      // skipped 6 selectedStateCallbackProperty
    }

    implicit class RichComboBoxTableCell[S, T](self: javafx.scene.control.cell.ComboBoxTableCell[S, T]) {
      // skipped 5 converterProperty
    }

    implicit class RichChoiceBoxListCell[T](self: javafx.scene.control.cell.ChoiceBoxListCell[T]) {
      // skipped 5 converterProperty
    }

    implicit class RichProgressBarTableCell[S](self: javafx.scene.control.cell.ProgressBarTableCell[S]) {
    }

    implicit class RichRadioButton(self: javafx.scene.control.RadioButton) {
    }

    implicit class RichStackedBarChart[X, Y](self: javafx.scene.chart.StackedBarChart[X, Y]) {
    }

    implicit class RichEllipse(self: javafx.scene.shape.Ellipse) {
    }

    implicit class RichCategoryAxis(self: javafx.scene.chart.CategoryAxis) {
    }

    implicit class RichLabeled(self: javafx.scene.control.Labeled) {
      // skipped 6 alignmentProperty
      // skipped 6 textAlignmentProperty
      // skipped 6 textOverrunProperty
      // skipped 6 graphicProperty
      // skipped 6 contentDisplayProperty
      // skipped 6 labelPaddingPropertyImpl
      // skipped 6 textFillProperty
      // skipped 6 fontProperty
    }

    implicit class RichGroup(self: javafx.scene.Group) {
    }

    implicit class RichSlider(self: javafx.scene.control.Slider) {
      // skipped 5 labelFormatterProperty
      // skipped 6 orientationProperty
    }

    implicit class RichComboBoxBase[T](self: javafx.scene.control.ComboBoxBase[T]) {
      def action: Events[javafx.event.ActionEvent] = EventsWithEventHandler.on(self.onActionProperty())
      // skipped 6 valueProperty
      def showing: Events[javafx.event.Event] = EventsWithEventHandler.on(self.onShowingProperty())
      def shown: Events[javafx.event.Event] = EventsWithEventHandler.on(self.onShownProperty())
      def hiding: Events[javafx.event.Event] = EventsWithEventHandler.on(self.onHidingProperty())
      def hidden: Events[javafx.event.Event] = EventsWithEventHandler.on(self.onHiddenProperty())
    }

    implicit class RichButton(self: javafx.scene.control.Button) {
    }

    implicit class RichChoiceBox[T](self: javafx.scene.control.ChoiceBox[T]) {
      // skipped 5 converterProperty
      // skipped 5 selectionModelProperty
      // skipped 5 itemsProperty
      // skipped 6 valueProperty
    }

    implicit class RichTilePane(self: javafx.scene.layout.TilePane) {
      // skipped 6 alignmentProperty
      // skipped 6 orientationProperty
      // skipped 6 tileAlignmentProperty
    }

    implicit class RichSVGPath(self: javafx.scene.shape.SVGPath) {
      // skipped 6 fillRuleProperty
    }

    implicit class RichComboBoxTreeCell[T](self: javafx.scene.control.cell.ComboBoxTreeCell[T]) {
      // skipped 5 converterProperty
    }

    implicit class RichPasswordField(self: javafx.scene.control.PasswordField) {
    }

    implicit class RichScrollPane(self: javafx.scene.control.ScrollPane) {
      // skipped 6 hbarPolicyProperty
      // skipped 6 vbarPolicyProperty
      // skipped 6 contentProperty
      // skipped 6 viewportBoundsProperty
    }

    implicit class RichPane(self: javafx.scene.layout.Pane) {
    }

    implicit class RichToolBar(self: javafx.scene.control.ToolBar) {
      // skipped 6 orientationProperty
    }

    implicit class RichLine(self: javafx.scene.shape.Line) {
    }

    implicit class RichControl(self: javafx.scene.control.Control) {
      // skipped 6 tooltipProperty
      // skipped 6 contextMenuProperty
      // skipped 0 skinProperty
    }

    implicit class RichTableCell[S, T](self: javafx.scene.control.TableCell[S, T]) {
      // skipped 5 tableViewPropertyImpl
    }

    implicit class RichSplitPane(self: javafx.scene.control.SplitPane) {
      // skipped 6 orientationProperty
    }

    implicit class RichXYChart[X, Y](self: javafx.scene.chart.XYChart[X, Y]) {
      // skipped 6 currentDisplayedXValueProperty
      // skipped 6 currentDisplayedYValueProperty
      // skipped 6 currentDisplayedExtraValueProperty
      // skipped 5 dataProperty
    }

    implicit class RichListView[T](self: javafx.scene.control.ListView[T]) {
      // skipped 5 selectionModelProperty
      // skipped 6 orientationProperty
      // skipped 6 cellFactoryProperty
      // skipped 5 focusModelProperty
      def editStart: Events[javafx.scene.control.ListView.EditEvent[T]] = EventsWithEventHandler.on(self.onEditStartProperty())
      def editCommit: Events[javafx.scene.control.ListView.EditEvent[T]] = EventsWithEventHandler.on(self.onEditCommitProperty())
      def editCancel: Events[javafx.scene.control.ListView.EditEvent[T]] = EventsWithEventHandler.on(self.onEditCancelProperty())
      // skipped 5 itemsProperty
    }

    implicit class RichTreeView[T](self: javafx.scene.control.TreeView[T]) {
      // skipped 5 rootProperty
      // skipped 5 selectionModelProperty
      // skipped 6 cellFactoryProperty
      // skipped 5 focusModelProperty
      // skipped 5 editingItemPropertyImpl
      def editStart: Events[javafx.scene.control.TreeView.EditEvent[T]] = EventsWithEventHandler.on(self.onEditStartProperty())
      def editCommit: Events[javafx.scene.control.TreeView.EditEvent[T]] = EventsWithEventHandler.on(self.onEditCommitProperty())
      def editCancel: Events[javafx.scene.control.TreeView.EditEvent[T]] = EventsWithEventHandler.on(self.onEditCancelProperty())
    }

    implicit class RichButtonBase(self: javafx.scene.control.ButtonBase) {
      def action: Events[javafx.event.ActionEvent] = EventsWithEventHandler.on(self.onActionProperty())
    }

    implicit class RichQuadCurve(self: javafx.scene.shape.QuadCurve) {
    }

    implicit class RichPolygon(self: javafx.scene.shape.Polygon) {
    }

    implicit class RichImageView(self: javafx.scene.image.ImageView) {
      // skipped 6 imageProperty
      // skipped 6 viewportProperty
    }

    implicit class RichText(self: javafx.scene.text.Text) {
      // skipped 6 fontSmoothingTypeProperty
      // skipped 6 textOriginProperty
      // skipped 6 boundsTypeProperty
      // skipped 5 impl_selectionShapeProperty
      // skipped 5 impl_caretShapeProperty
      // skipped 6 impl_selectionFillProperty
      // skipped 6 textAlignmentProperty
      // skipped 6 fontProperty
    }

    implicit class RichChoiceBoxTreeCell[T](self: javafx.scene.control.cell.ChoiceBoxTreeCell[T]) {
      // skipped 5 converterProperty
    }

    implicit class RichTextFieldTableCell[S, T](self: javafx.scene.control.cell.TextFieldTableCell[S, T]) {
      // skipped 5 converterProperty
    }

    implicit class RichTextFieldListCell[T](self: javafx.scene.control.cell.TextFieldListCell[T]) {
      // skipped 5 converterProperty
    }

    implicit class RichCheckBox(self: javafx.scene.control.CheckBox) {
    }

    implicit class RichLabel(self: javafx.scene.control.Label) {
      // skipped 6 labelForProperty
    }

    implicit class RichAreaChart[X, Y](self: javafx.scene.chart.AreaChart[X, Y]) {
    }

    implicit class RichChart(self: javafx.scene.chart.Chart) {
      // skipped 6 titleSideProperty
      // skipped 6 legendProperty
      // skipped 6 legendSideProperty
    }

    implicit class RichTitledPane(self: javafx.scene.control.TitledPane) {
      // skipped 6 contentProperty
    }

    implicit class RichTextFieldTreeCell[T](self: javafx.scene.control.cell.TextFieldTreeCell[T]) {
      // skipped 5 converterProperty
    }

    implicit class RichProgressIndicator(self: javafx.scene.control.ProgressIndicator) {
    }

    implicit class RichRegion(self: javafx.scene.layout.Region) {
      // skipped 6 paddingProperty
      // skipped 6 shapeProperty
      // skipped 5 backgroundFillsProperty
      // skipped 5 backgroundImagesProperty
      // skipped 5 strokeBordersProperty
      // skipped 5 imageBordersProperty
    }

    implicit class RichTextInputControl(self: javafx.scene.control.TextInputControl) {
    }

    implicit class RichBorderPane(self: javafx.scene.layout.BorderPane) {
      // skipped 6 centerProperty
      // skipped 6 bottomProperty
      // skipped 6 rightProperty
      // skipped 6 createObjectPropertyModelImpl
      // skipped 6 topProperty
      // skipped 6 leftProperty
    }

    implicit class RichStackPane(self: javafx.scene.layout.StackPane) {
      // skipped 6 alignmentProperty
    }

    implicit class RichRectangle(self: javafx.scene.shape.Rectangle) {
    }

    implicit class RichBarChart[X, Y](self: javafx.scene.chart.BarChart[X, Y]) {
    }

    implicit class RichMenuBar(self: javafx.scene.control.MenuBar) {
    }

    implicit class RichAccordion(self: javafx.scene.control.Accordion) {
      // skipped 6 expandedPaneProperty
    }

    implicit class RichParent(self: javafx.scene.Parent) {
      // skipped 6 impl_traversalEngineProperty
    }

    implicit class RichComboBoxListCell[T](self: javafx.scene.control.cell.ComboBoxListCell[T]) {
      // skipped 5 converterProperty
    }

    implicit class RichValueAxis[T <: Number](self: javafx.scene.chart.ValueAxis[T]) {
      // skipped 5 tickLabelFormatterProperty
    }

    implicit class RichCell[T](self: javafx.scene.control.Cell[T]) {
      // skipped 6 itemProperty
    }

    implicit class RichVBox(self: javafx.scene.layout.VBox) {
      // skipped 6 alignmentProperty
    }

    implicit class RichHyperlink(self: javafx.scene.control.Hyperlink) {
    }

    implicit class RichCanvas(self: javafx.scene.canvas.Canvas) {
    }

    implicit class RichCubicCurve(self: javafx.scene.shape.CubicCurve) {
    }

    implicit class RichCheckBoxListCell[T](self: javafx.scene.control.cell.CheckBoxListCell[T]) {
      // skipped 5 converterProperty
      // skipped 6 selectedStateCallbackProperty
    }

    implicit class RichChoiceBoxTableCell[S, T](self: javafx.scene.control.cell.ChoiceBoxTableCell[S, T]) {
      // skipped 5 converterProperty
    }

    implicit class RichPolyline(self: javafx.scene.shape.Polyline) {
    }

    implicit class RichTextArea(self: javafx.scene.control.TextArea) {
    }

    implicit class RichColorPicker(self: javafx.scene.control.ColorPicker) {
    }

    implicit class RichTextField(self: javafx.scene.control.TextField) {
      // skipped 6 alignmentProperty
      def action: Events[javafx.event.ActionEvent] = EventsWithEventHandler.on(self.onActionProperty())
    }

    implicit class RichBubbleChart[X, Y](self: javafx.scene.chart.BubbleChart[X, Y]) {
    }

    implicit class RichPath(self: javafx.scene.shape.Path) {
      // skipped 6 fillRuleProperty
    }

    implicit class RichFlowPane(self: javafx.scene.layout.FlowPane) {
      // skipped 6 columnHalignmentProperty
      // skipped 6 rowValignmentProperty
      // skipped 6 alignmentProperty
      // skipped 6 orientationProperty
    }

    implicit class RichTabPane(self: javafx.scene.control.TabPane) {
      // skipped 6 sideProperty
      // skipped 5 selectionModelProperty
      // skipped 6 tabClosingPolicyProperty
    }

    implicit class RichLineChart[X, Y](self: javafx.scene.chart.LineChart[X, Y]) {
    }

    implicit class RichPagination(self: javafx.scene.control.Pagination) {
      // skipped 6 pageFactoryProperty
    }

    implicit class RichListCell[T](self: javafx.scene.control.ListCell[T]) {
    }

    implicit class RichMediaView(self: javafx.scene.media.MediaView) {
      // skipped 6 mediaPlayerProperty
      def error: Events[javafx.scene.media.MediaErrorEvent] = EventsWithEventHandler.on(self.onErrorProperty())
      // skipped 6 viewportProperty
    }

    implicit class RichGridPane(self: javafx.scene.layout.GridPane) {
      // skipped 6 alignmentProperty
    }

    implicit class RichToggleButton(self: javafx.scene.control.ToggleButton) {
      // skipped 6 toggleGroupProperty
    }

    implicit class RichNumberAxis(self: javafx.scene.chart.NumberAxis) {
    }

    implicit class RichHTMLEditor(self: javafx.scene.web.HTMLEditor) {
    }

    implicit class RichSeparator(self: javafx.scene.control.Separator) {
      // skipped 6 halignmentProperty
      // skipped 6 valignmentProperty
      // skipped 6 orientationProperty
    }

    implicit class RichScrollBar(self: javafx.scene.control.ScrollBar) {
      // skipped 6 orientationProperty
    }

    implicit class RichHBox(self: javafx.scene.layout.HBox) {
      // skipped 6 alignmentProperty
    }

    implicit class RichIndexedCell[T](self: javafx.scene.control.IndexedCell[T]) {
    }

    implicit class RichShape(self: javafx.scene.shape.Shape) {
      // skipped 6 strokeTypeProperty
      // skipped 6 strokeLineJoinProperty
      // skipped 6 strokeLineCapProperty
      // skipped 6 strokeProperty
      // skipped 6 fillProperty
    }

    implicit class RichWebView(self: javafx.scene.web.WebView) {
      // skipped 6 fontSmoothingTypeProperty
    }

    implicit class RichCheckBoxTreeCell[T](self: javafx.scene.control.cell.CheckBoxTreeCell[T]) {
      // skipped 5 converterProperty
      // skipped 6 selectedStateCallbackProperty
    }
  }
}
