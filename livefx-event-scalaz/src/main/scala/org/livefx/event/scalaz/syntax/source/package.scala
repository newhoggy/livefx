package org.livefx.event.scalaz.syntax

import org.livefx.event.{SimpleBus, Sink, Source}

import scalaz.{-\/, \/, \/-}

package object source {
  implicit class SourceOps_qMVKwgW[A, B](val self: Source[A \/ B]) extends AnyVal {
    def divertLeft(sink: Sink[A]): Source[B] = {
      new SimpleBus[B] { temp =>
        val subscription = self.subscribe {
          case \/-(rt) => temp.publish(rt)
          case -\/(lt) => sink.publish(lt)
        }
      }
    }

    def divertRight(sink: Sink[B]): Source[A] = {
      new SimpleBus[A] { temp =>
        val subscription = self.subscribe {
          case \/-(rt) => sink.publish(rt)
          case -\/(lt) => temp.publish(lt)
        }
      }
    }
  }
}
