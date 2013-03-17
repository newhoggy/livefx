package org.livefx

case class BindTraceEntry(source: String, line: Int, column: Int)

object BindTraceEntry {
  implicit val bindTraceList = List[BindTraceEntry]()
}
