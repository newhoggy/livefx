package org.livefx.event

import java.io.Closeable

import org.livefx.core.std.autoCloseable._

/** A SinkSource is both a Sink and a Source.
  * Any events published to the SinkSource will have a transformation function applied to it
  * before emitting the transformed event to subscribers.
  */
trait SinkSource[A, B] extends Sink[A] with Source[B] { self =>
  /** Create a new Sink that applies a function to the event before propagating it to the
    * original sink.
    */
  override def comap[C](f: C => A): Sink[C] = new SinkSource[C, B] { temp =>
    val that = SinkSource[C, A](f)
    temp.disposes(that.subscribe(self.publish))
    override def subscribe(subscriber: B => Unit): Closeable = temp.subscribe(subscriber)
    override def publish(event: C): Unit = temp.publish(event)
  }

  /** Create a new Source that will emit transformed events that have been emitted by the original
    * Source.  The transformation is described by the function argument.
    */
  override def map[C](f: B => C): SinkSource[A, C] = new SinkSource[A, C] { temp =>
    val that = SinkSource[B, C](f)
    temp.disposes(self.subscribe(that.publish))
    override def subscribe(subscriber: C => Unit): Closeable = that.subscribe(subscriber)
    override def publish(event: A): Unit = self.publish(event)
  }
}

object SinkSource {
  def apply[A, B](f: A => B): SinkSource[A, B] = new SimpleSinkSource[A, B] {
    override val transform = f
  }
}
