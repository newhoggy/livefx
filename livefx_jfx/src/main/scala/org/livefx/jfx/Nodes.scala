package org.livefx.jfx

import org.livefx.Events

object Nodes {
  object Implicits {
    implicit class RichNode(self: javafx.scene.Node) {
//      def contextMenuRequested = EventsWithEventHandler.on(self.onContextMenuRequestedProperty())
//      def inputMethodTextChanged: Events[javafx.scene.input.InputMethodEvent] = EventsWithEventHandler.on(self.onInputMethodTextChangedProperty())
//      // skipped 6 clipProperty
//      // skipped 6 parentPropertyImpl
//      // skipped 6 scenePropertyImpl
//      // skipped 6 cursorProperty
//      // skipped 6 blendModeProperty
//      // skipped 6 cacheHintProperty
//      // skipped 6 effectProperty
//      // skipped 6 depthTestProperty
//      def dragEntered: Events[javafx.scene.input.DragEvent] = EventsWithEventHandler.on(self.onDragEnteredProperty())
//      def dragExited: Events[javafx.scene.input.DragEvent] = EventsWithEventHandler.on(self.onDragExitedProperty())
//      def dragOver: Events[javafx.scene.input.DragEvent] = EventsWithEventHandler.on(self.onDragOverProperty())
//      def dragDropped: Events[javafx.scene.input.DragEvent] = EventsWithEventHandler.on(self.onDragDroppedProperty())
//      def dragDone: Events[javafx.scene.input.DragEvent] = EventsWithEventHandler.on(self.onDragDoneProperty())
//      // skipped 6 rotationAxisProperty
//      def mouseClicked: Events[javafx.scene.input.MouseEvent] = EventsWithEventHandler.on(self.onMouseClickedProperty())
//      def mouseDragged: Events[javafx.scene.input.MouseEvent] = EventsWithEventHandler.on(self.onMouseDraggedProperty())
//      def mouseEntered: Events[javafx.scene.input.MouseEvent] = EventsWithEventHandler.on(self.onMouseEnteredProperty())
//      def mouseExited: Events[javafx.scene.input.MouseEvent] = EventsWithEventHandler.on(self.onMouseExitedProperty())
//      def mouseMoved: Events[javafx.scene.input.MouseEvent] = EventsWithEventHandler.on(self.onMouseMovedProperty())
//      def mousePressed: Events[javafx.scene.input.MouseEvent] = EventsWithEventHandler.on(self.onMousePressedProperty())
//      def mouseReleased: Events[javafx.scene.input.MouseEvent] = EventsWithEventHandler.on(self.onMouseReleasedProperty())
//      def dragDetected: Events[javafx.scene.input.MouseEvent] = EventsWithEventHandler.on(self.onDragDetectedProperty())
//      def mouseDragOver: Events[javafx.scene.input.MouseDragEvent] = EventsWithEventHandler.on(self.onMouseDragOverProperty())
//      def mouseDragReleased: Events[javafx.scene.input.MouseDragEvent] = EventsWithEventHandler.on(self.onMouseDragReleasedProperty())
//      def mouseDragEntered: Events[javafx.scene.input.MouseDragEvent] = EventsWithEventHandler.on(self.onMouseDragEnteredProperty())
//      def mouseDragExited: Events[javafx.scene.input.MouseDragEvent] = EventsWithEventHandler.on(self.onMouseDragExitedProperty())
//      def scrollStarted: Events[javafx.scene.input.ScrollEvent] = EventsWithEventHandler.on(self.onScrollStartedProperty())
//      def scroll: Events[javafx.scene.input.ScrollEvent] = EventsWithEventHandler.on(self.onScrollProperty())
//      def scrollFinished: Events[javafx.scene.input.ScrollEvent] = EventsWithEventHandler.on(self.onScrollFinishedProperty())
//      def rotationStarted: Events[javafx.scene.input.RotateEvent] = EventsWithEventHandler.on(self.onRotationStartedProperty())
//      def rotate: Events[javafx.scene.input.RotateEvent] = EventsWithEventHandler.on(self.onRotateProperty())
//      def rotationFinished: Events[javafx.scene.input.RotateEvent] = EventsWithEventHandler.on(self.onRotationFinishedProperty())
//      def zoomStarted: Events[javafx.scene.input.ZoomEvent] = EventsWithEventHandler.on(self.onZoomStartedProperty())
//      def zoom: Events[javafx.scene.input.ZoomEvent] = EventsWithEventHandler.on(self.onZoomProperty())
//      def zoomFinished: Events[javafx.scene.input.ZoomEvent] = EventsWithEventHandler.on(self.onZoomFinishedProperty())
//      def swipeUp: Events[javafx.scene.input.SwipeEvent] = EventsWithEventHandler.on(self.onSwipeUpProperty())
//      def swipeDown: Events[javafx.scene.input.SwipeEvent] = EventsWithEventHandler.on(self.onSwipeDownProperty())
//      def swipeLeft: Events[javafx.scene.input.SwipeEvent] = EventsWithEventHandler.on(self.onSwipeLeftProperty())
//      def swipeRight: Events[javafx.scene.input.SwipeEvent] = EventsWithEventHandler.on(self.onSwipeRightProperty())
//      def touchPressed: Events[javafx.scene.input.TouchEvent] = EventsWithEventHandler.on(self.onTouchPressedProperty())
//      def touchMoved: Events[javafx.scene.input.TouchEvent] = EventsWithEventHandler.on(self.onTouchMovedProperty())
//      def touchReleased: Events[javafx.scene.input.TouchEvent] = EventsWithEventHandler.on(self.onTouchReleasedProperty())
//      def touchStationary: Events[javafx.scene.input.TouchEvent] = EventsWithEventHandler.on(self.onTouchStationaryProperty())
//      def keyPressed: Events[javafx.scene.input.KeyEvent] = EventsWithEventHandler.on(self.onKeyPressedProperty())
//      def keyReleased: Events[javafx.scene.input.KeyEvent] = EventsWithEventHandler.on(self.onKeyReleasedProperty())
//      def keyTyped: Events[javafx.scene.input.KeyEvent] = EventsWithEventHandler.on(self.onKeyTypedProperty())
//      // skipped 6 inputMethodRequestsProperty
//      // skipped 6 eventDispatcherProperty
    }
//
//    implicit class RichFlowPane(self: javafx.scene.layout.FlowPane) {
//      // skipped 6 alignmentProperty
//      // skipped 6 orientationProperty
//      // skipped 6 columnHalignmentProperty
//      // skipped 6 rowValignmentProperty
//    }
//
//    implicit class RichImageView(self: javafx.scene.image.ImageView) {
//      // skipped 6 viewportProperty
//      // skipped 6 imageProperty
//    }
//
//    implicit class RichTextFieldTreeCell[T](self: javafx.scene.control.cell.TextFieldTreeCell[T]) {
//      // skipped 5 converterProperty
//    }
//
//    implicit class RichRegion(self: javafx.scene.layout.Region) {
//      // skipped 6 paddingProperty
//      // skipped 6 shapeProperty
//      // skipped 5 backgroundFillsProperty
//      // skipped 5 backgroundImagesProperty
//      // skipped 5 strokeBordersProperty
//      // skipped 5 imageBordersProperty
//    }
//
//    implicit class RichRectangle(self: javafx.scene.shape.Rectangle) {
//    }
//
//    implicit class RichSlider(self: javafx.scene.control.Slider) {
//      // skipped 6 orientationProperty
//      // skipped 5 labelFormatterProperty
//    }
//
//    implicit class RichHBox(self: javafx.scene.layout.HBox) {
//      // skipped 6 alignmentProperty
//    }
//
//    implicit class RichCheckBox(self: javafx.scene.control.CheckBox) {
//    }
//
//    implicit class RichStackPane(self: javafx.scene.layout.StackPane) {
//      // skipped 6 alignmentProperty
//    }
//
//    implicit class RichScrollBar(self: javafx.scene.control.ScrollBar) {
//      // skipped 6 orientationProperty
//    }
//
//    implicit class RichTextFieldTableCell[S, T](self: javafx.scene.control.cell.TextFieldTableCell[S, T]) {
//      // skipped 5 converterProperty
//    }
//
//    implicit class RichPane(self: javafx.scene.layout.Pane) {
//    }
//
//    implicit class RichControl(self: javafx.scene.control.Control) {
//      // skipped 0 skinProperty
//      // skipped 6 tooltipProperty
//      // skipped 6 contextMenuProperty
//    }
//
//    implicit class RichCubicCurve(self: javafx.scene.shape.CubicCurve) {
//    }
//
//    implicit class RichProgressBarTableCell[S](self: javafx.scene.control.cell.ProgressBarTableCell[S]) {
//    }
//
//    implicit class RichHTMLEditor(self: javafx.scene.web.HTMLEditor) {
//    }
//
//    implicit class RichTextArea(self: javafx.scene.control.TextArea) {
//    }
//
//    implicit class RichCheckBoxListCell[T](self: javafx.scene.control.cell.CheckBoxListCell[T]) {
//      // skipped 5 converterProperty
//      // skipped 6 selectedStateCallbackProperty
//    }
//
//    implicit class RichToggleButton(self: javafx.scene.control.ToggleButton) {
//      // skipped 6 toggleGroupProperty
//    }
//
//    implicit class RichMenuButton(self: javafx.scene.control.MenuButton) {
//      // skipped 6 popupSideProperty
//    }
//
//    implicit class RichAnchorPane(self: javafx.scene.layout.AnchorPane) {
//    }
//
//    implicit class RichNumberAxis(self: javafx.scene.chart.NumberAxis) {
//    }
//
//    implicit class RichXYChart[X, Y](self: javafx.scene.chart.XYChart[X, Y]) {
//      // skipped 5 dataProperty
//      // skipped 6 currentDisplayedXValueProperty
//      // skipped 6 currentDisplayedYValueProperty
//      // skipped 6 currentDisplayedExtraValueProperty
//    }
//
//    implicit class RichTextInputControl(self: javafx.scene.control.TextInputControl) {
//    }
//
//    implicit class RichAccordion(self: javafx.scene.control.Accordion) {
//      // skipped 6 expandedPaneProperty
//    }
//
//    implicit class RichValueAxis[T](self: javafx.scene.chart.ValueAxis[T]) {
//      // skipped 5 tickLabelFormatterProperty
//    }
//
//    implicit class RichWebView(self: javafx.scene.web.WebView) {
//      // skipped 6 fontSmoothingTypeProperty
//    }
//
//    implicit class RichGroup(self: javafx.scene.Group) {
//    }
//
//    implicit class RichProgressIndicator(self: javafx.scene.control.ProgressIndicator) {
//    }
//
//    implicit class RichIndexedCell[T](self: javafx.scene.control.IndexedCell[T]) {
//    }
//
//    implicit class RichParent(self: javafx.scene.Parent) {
//      // skipped 6 impl_traversalEngineProperty
//    }
//
//    implicit class RichTableRow[T](self: javafx.scene.control.TableRow[T]) {
//      // skipped 5 tableViewPropertyImpl
//    }
//
//    implicit class RichChoiceBoxTableCell[S, T](self: javafx.scene.control.cell.ChoiceBoxTableCell[S, T]) {
//      // skipped 5 converterProperty
//    }
//
//    implicit class RichChoiceBoxTreeCell[T](self: javafx.scene.control.cell.ChoiceBoxTreeCell[T]) {
//      // skipped 5 converterProperty
//    }
//
//    implicit class RichTabPane(self: javafx.scene.control.TabPane) {
//      // skipped 6 sideProperty
//      // skipped 5 selectionModelProperty
//      // skipped 6 tabClosingPolicyProperty
//    }
//
//    implicit class RichListView[T](self: javafx.scene.control.ListView[T]) {
//      // skipped 5 selectionModelProperty
//      // skipped 6 orientationProperty
//      // skipped 6 cellFactoryProperty
//      // skipped 5 focusModelProperty
////      def editStart: Events[ListView.this.EditEvent[T]] = EventsWithEventHandler.on(self.onEditStartProperty())
////      def editCommit: Events[ListView.this.EditEvent[T]] = EventsWithEventHandler.on(self.onEditCommitProperty())
////      def editCancel: Events[ListView.this.EditEvent[T]] = EventsWithEventHandler.on(self.onEditCancelProperty())
//      // skipped 5 itemsProperty
//    }
//
//    implicit class RichAxis[T](self: javafx.scene.chart.Axis[T]) {
//      // skipped 6 sideProperty
//      // skipped 6 labelProperty
//      // skipped 6 tickLabelFontProperty
//      // skipped 6 tickLabelFillProperty
//    }
//
//    implicit class RichChoiceBox[T](self: javafx.scene.control.ChoiceBox[T]) {
//      // skipped 5 converterProperty
//      // skipped 5 selectionModelProperty
//      // skipped 5 itemsProperty
//      // skipped 6 valueProperty
//    }
//
//    implicit class RichLabel(self: javafx.scene.control.Label) {
//      // skipped 6 labelForProperty
//    }
//
//    implicit class RichHyperlink(self: javafx.scene.control.Hyperlink) {
//    }
//
//    implicit class RichPolyline(self: javafx.scene.shape.Polyline) {
//    }
//
//    implicit class RichComboBoxBase[T](self: javafx.scene.control.ComboBoxBase[T]) {
//      def action: Events[javafx.event.ActionEvent] = EventsWithEventHandler.on(self.onActionProperty())
//      // skipped 6 valueProperty
//      def showing: Events[javafx.event.Event] = EventsWithEventHandler.on(self.onShowingProperty())
//      def shown: Events[javafx.event.Event] = EventsWithEventHandler.on(self.onShownProperty())
//      def hiding: Events[javafx.event.Event] = EventsWithEventHandler.on(self.onHidingProperty())
//      def hidden: Events[javafx.event.Event] = EventsWithEventHandler.on(self.onHiddenProperty())
//    }
//
//    implicit class RichAreaChart[X, Y](self: javafx.scene.chart.AreaChart[X, Y]) {
//    }
//
//    implicit class RichLabeled(self: javafx.scene.control.Labeled) {
//      // skipped 6 fontProperty
//      // skipped 6 alignmentProperty
//      // skipped 6 textAlignmentProperty
//      // skipped 6 textOverrunProperty
//      // skipped 6 graphicProperty
//      // skipped 6 contentDisplayProperty
//      // skipped 6 labelPaddingPropertyImpl
//      // skipped 6 textFillProperty
//    }
//
//    implicit class RichChoiceBoxListCell[T](self: javafx.scene.control.cell.ChoiceBoxListCell[T]) {
//      // skipped 5 converterProperty
//    }
//
//    implicit class RichCategoryAxis(self: javafx.scene.chart.CategoryAxis) {
//    }
//
//    implicit class RichCheckBoxTreeCell[T](self: javafx.scene.control.cell.CheckBoxTreeCell[T]) {
//      // skipped 5 converterProperty
//      // skipped 6 selectedStateCallbackProperty
//    }
//
//    implicit class RichProgressBar(self: javafx.scene.control.ProgressBar) {
//    }
//
//    implicit class RichVBox(self: javafx.scene.layout.VBox) {
//      // skipped 6 alignmentProperty
//    }
//
//    implicit class RichMenuBar(self: javafx.scene.control.MenuBar) {
//    }
//
//    implicit class RichComboBoxListCell[T](self: javafx.scene.control.cell.ComboBoxListCell[T]) {
//      // skipped 5 converterProperty
//    }
//
//    implicit class RichTableView[S](self: javafx.scene.control.TableView[S]) {
//      // skipped 5 selectionModelProperty
//      // skipped 5 focusModelProperty
//      // skipped 5 itemsProperty
//      // skipped 6 columnResizePolicyProperty
//      // skipped 6 rowFactoryProperty
//      // skipped 6 placeholderProperty
//      // skipped 0 editingCellPropertyImpl
//    }
//
//    implicit class RichTextFieldListCell[T](self: javafx.scene.control.cell.TextFieldListCell[T]) {
//      // skipped 5 converterProperty
//    }
//
//    implicit class RichStackedAreaChart[X, Y](self: javafx.scene.chart.StackedAreaChart[X, Y]) {
//    }
//
//    implicit class RichBubbleChart[X, Y](self: javafx.scene.chart.BubbleChart[X, Y]) {
//    }
//
//    implicit class RichCheckBoxTableCell[S, T](self: javafx.scene.control.cell.CheckBoxTableCell[S, T]) {
//      // skipped 5 converterProperty
//      // skipped 6 selectedStateCallbackProperty
//    }
//
    implicit class RichButtonBase(self: javafx.scene.control.ButtonBase) {
      def action = EventsWithEventHandler.on(self.onActionProperty())
    }
//
//    implicit class RichToolBar(self: javafx.scene.control.ToolBar) {
//      // skipped 6 orientationProperty
//    }
//
//    implicit class RichSVGPath(self: javafx.scene.shape.SVGPath) {
//      // skipped 6 fillRuleProperty
//    }
//
//    implicit class RichBarChart[X, Y](self: javafx.scene.chart.BarChart[X, Y]) {
//    }
//
//    implicit class RichTilePane(self: javafx.scene.layout.TilePane) {
//      // skipped 6 alignmentProperty
//      // skipped 6 orientationProperty
//      // skipped 6 tileAlignmentProperty
//    }
//
//    implicit class RichText(self: javafx.scene.text.Text) {
//      // skipped 6 fontProperty
//      // skipped 6 textAlignmentProperty
//      // skipped 6 fontSmoothingTypeProperty
//      // skipped 6 textOriginProperty
//      // skipped 6 boundsTypeProperty
//      // skipped 5 impl_selectionShapeProperty
//      // skipped 5 impl_caretShapeProperty
//      // skipped 6 impl_selectionFillProperty
//    }
//
//    implicit class RichScrollPane(self: javafx.scene.control.ScrollPane) {
//      // skipped 6 hbarPolicyProperty
//      // skipped 6 vbarPolicyProperty
//      // skipped 6 contentProperty
//      // skipped 6 viewportBoundsProperty
//    }
//
//    implicit class RichTreeView[T](self: javafx.scene.control.TreeView[T]) {
//      // skipped 5 rootProperty
//      // skipped 5 selectionModelProperty
//      // skipped 6 cellFactoryProperty
//      // skipped 5 focusModelProperty
//      // skipped 5 editingItemPropertyImpl
//      def editStart: Events[TreeView.this.EditEvent[T]] = EventsWithEventHandler.on(self.onEditStartProperty())
//      def editCommit: Events[TreeView.this.EditEvent[T]] = EventsWithEventHandler.on(self.onEditCommitProperty())
//      def editCancel: Events[TreeView.this.EditEvent[T]] = EventsWithEventHandler.on(self.onEditCancelProperty())
//    }
//
//    implicit class RichPolygon(self: javafx.scene.shape.Polygon) {
//    }
//
//    implicit class RichStackedBarChart[X, Y](self: javafx.scene.chart.StackedBarChart[X, Y]) {
//    }
//
//    implicit class RichShape(self: javafx.scene.shape.Shape) {
//      // skipped 6 fillProperty
//      // skipped 6 strokeTypeProperty
//      // skipped 6 strokeLineJoinProperty
//      // skipped 6 strokeLineCapProperty
//      // skipped 6 strokeProperty
//    }
//
//    implicit class RichQuadCurve(self: javafx.scene.shape.QuadCurve) {
//    }
//
//    implicit class RichComboBoxTableCell[S, T](self: javafx.scene.control.cell.ComboBoxTableCell[S, T]) {
//      // skipped 5 converterProperty
//    }
//
//    implicit class RichRadioButton(self: javafx.scene.control.RadioButton) {
//    }
//
//    implicit class RichSplitMenuButton(self: javafx.scene.control.SplitMenuButton) {
//    }
//
//    implicit class RichListCell[T](self: javafx.scene.control.ListCell[T]) {
//    }
//
//    implicit class RichSeparator(self: javafx.scene.control.Separator) {
//      // skipped 6 orientationProperty
//      // skipped 6 halignmentProperty
//      // skipped 6 valignmentProperty
//    }
//
//    implicit class RichCell[T](self: javafx.scene.control.Cell[T]) {
//      // skipped 6 itemProperty
//    }
//
//    implicit class RichPasswordField(self: javafx.scene.control.PasswordField) {
//    }
//
//    implicit class RichEllipse(self: javafx.scene.shape.Ellipse) {
//    }
//
//    implicit class RichTableCell[S, T](self: javafx.scene.control.TableCell[S, T]) {
//      // skipped 5 tableViewPropertyImpl
//    }
//
//    implicit class RichGridPane(self: javafx.scene.layout.GridPane) {
//      // skipped 6 alignmentProperty
//    }
//
//    implicit class RichButton(self: javafx.scene.control.Button) {
//    }
//
//    implicit class RichBorderPane(self: javafx.scene.layout.BorderPane) {
//      // skipped 6 topProperty
//      // skipped 6 leftProperty
//      // skipped 6 centerProperty
//      // skipped 6 bottomProperty
//      // skipped 6 rightProperty
//      // skipped 6 createObjectPropertyModelImpl
//    }
//
//    implicit class RichScatterChart[X, Y](self: javafx.scene.chart.ScatterChart[X, Y]) {
//    }
//
//    implicit class RichChart(self: javafx.scene.chart.Chart) {
//      // skipped 6 titleSideProperty
//      // skipped 6 legendProperty
//      // skipped 6 legendSideProperty
//    }
//
//    implicit class RichColorPicker(self: javafx.scene.control.ColorPicker) {
//    }
//
//    implicit class RichTextField(self: javafx.scene.control.TextField) {
//      // skipped 6 alignmentProperty
//      def action: Events[javafx.event.ActionEvent] = EventsWithEventHandler.on(self.onActionProperty())
//    }
//
//    implicit class RichLineChart[X, Y](self: javafx.scene.chart.LineChart[X, Y]) {
//    }
//
//    implicit class RichComboBox[T](self: javafx.scene.control.ComboBox[T]) {
//      // skipped 5 converterProperty
//      // skipped 5 selectionModelProperty
//      // skipped 6 cellFactoryProperty
//      // skipped 5 itemsProperty
//      // skipped 5 buttonCellProperty
//    }
//
//    implicit class RichPagination(self: javafx.scene.control.Pagination) {
//      // skipped 6 pageFactoryProperty
//    }
//
//    implicit class RichTreeCell[T](self: javafx.scene.control.TreeCell[T]) {
//      // skipped 6 disclosureNodeProperty
//    }
//
//    implicit class RichSplitPane(self: javafx.scene.control.SplitPane) {
//      // skipped 6 orientationProperty
//    }
//
//    implicit class RichPieChart(self: javafx.scene.chart.PieChart) {
//      // skipped 5 dataProperty
//    }
//
//    implicit class RichComboBoxTreeCell[T](self: javafx.scene.control.cell.ComboBoxTreeCell[T]) {
//      // skipped 5 converterProperty
//    }
//
//    implicit class RichCircle(self: javafx.scene.shape.Circle) {
//    }
//
//    implicit class RichLine(self: javafx.scene.shape.Line) {
//    }
//
//    implicit class RichTitledPane(self: javafx.scene.control.TitledPane) {
//      // skipped 6 contentProperty
//    }
//
//    implicit class RichPath(self: javafx.scene.shape.Path) {
//      // skipped 6 fillRuleProperty
//    }
//
//    implicit class RichArc(self: javafx.scene.shape.Arc) {
//      // skipped 6 typeProperty
//    }
//
//    implicit class RichMediaView(self: javafx.scene.media.MediaView) {
//      // skipped 6 mediaPlayerProperty
//      def error: Events[javafx.scene.media.MediaErrorEvent] = EventsWithEventHandler.on(self.onErrorProperty())
//      // skipped 6 viewportProperty
//    }
//
//    implicit class RichCanvas(self: javafx.scene.canvas.Canvas) {
//    }
  }
}
