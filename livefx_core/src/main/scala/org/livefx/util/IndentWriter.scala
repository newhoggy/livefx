package org.livefx.util

import java.io.Writer
import java.io.PrintStream
import java.io.PrintWriter

sealed class EndLn

object EndLn extends EndLn

sealed class Break

object Break extends Break

class IndentWriter(out: Writer) extends PrintWriter(out, true) {
  val defaultIndent: Int = 2
  var indent: Int = 0
  var emptyLines: Int = -1
  var line: Int = 0
  
  def atLineStart: Boolean = emptyLines != -1
  
  def this(out: PrintStream) = this(new PrintWriter(out, true))
  
  def break() = ensureEmptyLines(0)
  
  def ensureEmptyLine() = ensureEmptyLines(1)
  
  def ensureEmptyLines(lines: Int) = while (emptyLines < lines) this.println()
  
  def indent[T](offset: Int)(f: => T): T = {
    indent += offset
    try {
      return f
    } finally {
      indent -= offset
    }
  }
  
  def indent[T](f: => T): T = indent(defaultIndent)(f)
  
  override def println(): Unit = {
    super.println()
    emptyLines += 1
    line += 1
  }

  private def prePrint(): Unit = {
    if (atLineStart) {
      super.print(" " * indent)
      emptyLines = -1
    }
  }
  
  override def print(s: String): Unit = {
    if (s.length != 0) {
      prePrint()
    }
    super.print(s)
  }
  
  override def print(value: Char): Unit = {
    prePrint()
    super.print(value)
  }
  
  override def print(value: Int): Unit = {
    prePrint()
    super.print(value)
  }
  
  override def print(value: Long): Unit = {
    prePrint()
    super.print(value)
  }
  
  override def print(value: Float): Unit = {
    prePrint()
    super.print(value)
  }
  
  override def print(value: Double): Unit = {
    prePrint()
    super.print(value)
  }

  def <<(s: String): IndentWriter = {
    if (s.length != 0) {
      prePrint()
    }
    super.print(s)
    return this
  }
  
  def <<(value: Char): IndentWriter = {
    prePrint()
    super.print(value)
    return this
  }
  
  def <<(value: Int): IndentWriter = {
    prePrint()
    super.print(value)
    return this
  }
  
  def <<(value: Long): IndentWriter = {
    prePrint()
    super.print(value)
    return this
  }
  
  def <<(value: Float): IndentWriter = {
    prePrint()
    super.print(value)
    return this
  }
  
  def <<(value: Double): IndentWriter = {
    this.print(value)
    return this
  }
  
  def <<(value: Boolean): IndentWriter = {
    this.print(value)
    return this
  }
  
  def <<(value: Object): IndentWriter = {
    this.print(value.toString)
    return this
  }
  
  def <<(endLn: EndLn): IndentWriter = {
    this.println()
    return this
  }
  
  def <<(break: Break): IndentWriter = {
    if (!atLineStart) {
      this.println()
    }
    
    return this
  }

  def trace(left: String, right: String): this.type = {
    val frame = new Exception().getStackTrace()(1)
    ( this
      << left << frame.getFileName
      << ":" << frame.getLineNumber << right )
    this
  }
}

object IndentWriter {
  def wrap[T](printWriter: PrintWriter)(f: IndentWriter => T): T = {
    val writer = new IndentWriter(printWriter)
    try {
      f(writer)
    } finally {
      writer.flush()
    }
  }
}
