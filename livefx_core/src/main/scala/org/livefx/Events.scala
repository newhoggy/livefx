package org.livefx

trait Events[+P, +E] { self =>
  def subscribe(subscriber: (P, E) => Unit): Disposable
  def subscribeWeak(subscriber: (P, E) => Unit): Disposable
  def unsubscribe(subscriber: (P, E) => Unit): Unit

  def +[Q >: P, F >: E](that: Events[Q, F]): Events[Q, F] = new Events[Q, F] {
    def subscribe(subscriber: (Q, F) => Unit): Disposable = new Disposable {
      val disposable1 = self.subscribe(subscriber)
      val disposable2 = that.subscribe(subscriber)
      def dispose(): Unit = {
        disposable1.dispose()
        disposable2.dispose()
      }
    }

    def subscribeWeak(subscriber: (Q, F) => Unit): Disposable = new Disposable {
      val disposable1 = self.subscribe(subscriber)
      val disposable2 = that.subscribe(subscriber)
      def dispose(): Unit = {
        disposable1.dispose()
        disposable2.dispose()
      }
    }

    def unsubscribe(subscriber: (Q, F) => Unit): Unit = {
      self.unsubscribe(subscriber)
      that.unsubscribe(subscriber)
    }
  }
}
