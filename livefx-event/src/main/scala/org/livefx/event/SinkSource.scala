package org.livefx.event
import java.io.Closeable

trait SinkSource[A, B] extends Sink[A] with Source[B] { self =>
  override def map[C](f: B => C): SinkSource[A, C] = {
    new SinkSource[A, C] { temp =>
      val that = SinkSource[B, C](f)
      val subscription = self.subscribe(that.publish)
      override def publish(event: A): Unit = self.publish(event)
      override def subscribe(subscriber: C => Unit): Closeable = that.subscribe(subscriber)
      override def close(): Unit = subscription.close()
    }
  }
}

object SinkSource {
  def apply[A, B](f: A => B): SinkSource[A, B] = new SimpleSinkSource[A, B] {
    override val transform = f
  }
}
