package org.livefx

trait Events[+E] { self =>
  def subscribe(subscriber: E => Unit): Disposable
  def subscribeWeak(subscriber: E => Unit): Disposable
  def unsubscribe(subscriber: E => Unit): Unit

  def +[F >: E](that: Events[F]): Events[F] = new Events[F] {
    def subscribe(subscriber: F => Unit): Disposable = new Disposable {
      val disposable1 = self.subscribe(subscriber)
      val disposable2 = that.subscribe(subscriber)
      def dispose(): Unit = {
        disposable1.dispose()
        disposable2.dispose()
      }
    }

    def subscribeWeak(subscriber: F => Unit): Disposable = new Disposable {
      val disposable1 = self.subscribe(subscriber)
      val disposable2 = that.subscribe(subscriber)
      def dispose(): Unit = {
        disposable1.dispose()
        disposable2.dispose()
      }
    }

    def unsubscribe(subscriber: F => Unit): Unit = {
      self.unsubscribe(subscriber)
      that.unsubscribe(subscriber)
    }
  }
}
