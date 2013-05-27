package org.livefx.util

import java.lang.ref.ReferenceQueue
//import java.util.HashMap
//import java.util.Map
//import java.util.Set
//import java.util.Collection
//import java.util.HashSet
//import java.util.Collections
//import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap
import scala.collection.mutable.{Map => MutMap}
import scala.collection.{mutable => mutable}
import scala.annotation.migration

object Moo {
  val x = new HashMap[Int, Int]()
  x.+(0 -> 0)
}

class WeakIdHashMap[K, V]() extends MutMap[K, V] {
  private implicit val queue = new ReferenceQueue[K]
  private val backingStore = new HashMap[WeakIdRef[K], V]

  override def clear(): Unit = {
    backingStore.clear()
    reap()
  }

  override def size: Int = backingStore.size

//  def this() = this(null)
//
  override def contains(key: K): Boolean = reapThen(backingStore.contains(WeakIdRef(key)))

  override def apply(key: K): V = backingStore(WeakIdRef(key))

  def get(key: K): Option[V] = backingStore.get(WeakIdRef(key))

  override def put(key: K, value: V): Option[V] = reapThen {
    backingStore.put(WeakIdRef(key), value)
  }

  override def update(key: K, value: V): Unit = reapThen {
    backingStore.put(WeakIdRef(key), value)
  }

  override def remove(key: K): Option[V] = reapThen {
    backingStore.remove(WeakIdRef(key.asInstanceOf[K]))
  }

  def += (kv: (K, V)): this.type = reapThen {
    backingStore.put(WeakIdRef(kv._1), kv._2)
    this
  }

  def -=(key: K): this.type = reapThen {
    backingStore.remove(WeakIdRef(key))
    this
  }

  override def iterator = backingStore.iterator.flatMap {
    case (kRef, v) => {
      val k = kRef.get
      if (k != null) Some((k, v)) else None
    }
  }

  private final def reap(): Unit = this.synchronized {
    var zombie = queue.poll()

    while (zombie != null) {
      val victim = zombie.asInstanceOf[WeakIdRef[K]]
      backingStore.remove(victim)
      zombie = queue.poll()
    }
  }

  private final def reapThen[T](f: => T): T = { reap(); f }
}
