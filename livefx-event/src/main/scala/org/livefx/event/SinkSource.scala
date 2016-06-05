package org.livefx.event

trait SinkSource[A, B] extends Sink[A] with Source[B]
