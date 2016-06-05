package org.livefx.event.syntax

import org.livefx.event.{SimpleBus, Sink, Source}

package object source {
  implicit class SourceOps_qMVKwgW[A, B](val self: Source[Either[A, B]]) extends AnyVal {
    def divertLeft(sink: Sink[A]): Source[B] = {
      new SimpleBus[B] { temp =>
        val subscription = self.subscribe {
          case Right(rt) => temp.publish(rt)
          case Left(lt) => sink.publish(lt)
        }
      }
    }

    def divertRight(sink: Sink[B]): Source[A] = {
      new SimpleBus[A] { temp =>
        val subscription = self.subscribe {
          case Right(rt) => sink.publish(rt)
          case Left(lt) => temp.publish(lt)
        }
      }
    }
  }
}
