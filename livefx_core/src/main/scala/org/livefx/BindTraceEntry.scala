package org.livefx

import java.io.PrintWriter
import java.io.PrintStream

case class BindTraceEntry(source: String, line: Int, column: Int, snippet: String) {
  def printTo(out: PrintWriter): Unit = {
    out.println(s"(${source}:${line}:${column}) ==> ${snippet}")
  }

  def printTo(out: PrintStream): Unit = {
    out.println(s"(${source}:${line}:${column}) ==> ${snippet}")
  }
}
