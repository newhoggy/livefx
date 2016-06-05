package org.livefx

trait EventSource[+E] { self =>
  def subscribe(subscriber: E => Unit): Disposable
  def asEvents: EventSource[E] = this

  def |[F >: E](that: EventSource[F]): EventSource[F] = new EventBus[F] with Disposable {
    private var subscription1 = self.subscribe(publish)
    private var subscription2 = that.subscribe(publish)

    override def onDispose(): Unit = {
      // TODO: Make exception safe
      try {
        subscription1.dispose()
        subscription2.dispose()
      } finally {
        subscription1 = null
        subscription2 = null
      }
    }
  }
  
  def impeded: EventSource[E] = new EventBus[E] with Disposable {
    private var stored = Option.empty[E]
    private var subscription = self.subscribe { e =>
      stored.foreach(publish(_))
      stored = Some(e)
    }

    override def onDispose(): Unit = {
      // TODO: Make exception safe
      try {
        subscription.dispose()
      } finally {
        subscription = null
      }
    }
  }

  def map[F](f: E => F): EventSource[F] = new EventBus[F] with Disposable {
    private var subscription = self.subscribe(e => publish(f(e)))

    override def onDispose(): Unit = {
      // TODO: Make exception safe
      try {
        subscription.dispose()
      } finally {
        subscription = null
      }
    }
  }

  def flatMap[F](f: E => EventSource[F]): EventSource[F] = new EventBus[F] with Disposable {
    private val mappedEvents = self.map(e => f(e))    
    private var mapped = Option.empty[Disposable]
    private var subscription = mappedEvents.subscribe { events =>
      mapped.foreach(_.dispose())
      mapped = Some(events.subscribe(publish))
    }

    override def onDispose(): Unit = {
      // TODO: Make exception safe
      try {
        mapped.foreach(_.dispose())
        subscription.dispose()
      } finally {
        mapped = null
        subscription = null
      }
    }
  }
}
