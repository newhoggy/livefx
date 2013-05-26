package org.livefx.util

import java.lang.ref.ReferenceQueue
import java.util.HashMap
import java.util.Map
import java.util.Set
import java.util.Collection
import java.util.HashSet
import java.util.Collections
import scala.collection.JavaConversions._

class WeakIdentityHashMap[K, V] extends Map[K, V] {
  private implicit val queue = new ReferenceQueue[K]
  private val backingStore = new HashMap[WeakIdentityReference[K], V]

  override def clear(): Unit = {
    backingStore.clear()
    reap()
  }

  override def containsKey(key: Any): Boolean = reapThen {
    backingStore.containsKey(WeakIdentityReference(key.asInstanceOf[K]))
  }

  override def containsValue(value: Any): Boolean = reapThen(backingStore.containsValue(value))

  override def entrySet(): Set[Map.Entry[K, V]] = reapThen {
    val ret = new HashSet[Map.Entry[K, V]]

    for (ref <- backingStore.entrySet()) {
      val key = ref.getKey.get
      val value = ref.getValue
      val entry = new Map.Entry[K, V] {
        override def getKey(): K = key
        override def getValue(): V = value
        override def setValue(value: V): V = throw new UnsupportedOperationException
      }

      ret.add(entry)
    }

    Collections.unmodifiableSet(ret)
  }
  
  override def keySet(): Set[K] = reapThen {
    val ret = new HashSet[K]

    for (ref <- backingStore.keySet) {
        ret.add(ref.get)
    }

    Collections.unmodifiableSet(ret)
  }

  override def equals(any: Any): Boolean = {
    backingStore.equals(any.asInstanceOf[WeakIdentityHashMap[K, V]].backingStore)
  }

  override def get(key: Any): V = reapThen {
    backingStore.get(WeakIdentityReference(key.asInstanceOf[K]))
  }

  override def put(key: K, value: V): V = reapThen {
    backingStore.put(WeakIdentityReference(key), value)
  }

  override def hashCode(): Int = reapThen(backingStore.hashCode)

  override def isEmpty(): Boolean = reapThen(backingStore.isEmpty)
  
  override def putAll(map: Map[_ <: K, _ <: V]): Unit = throw new UnsupportedOperationException
  
  override def remove(key: Any): V  = reapThen {
    backingStore.remove(WeakIdentityReference(key.asInstanceOf[K]))
  }
  
  override def size(): Int = reapThen(backingStore.size)

  override def values(): Collection[V] = reapThen(backingStore.values)

  private final def reap(): Unit = this.synchronized {
    var zombie = queue.poll()

    while (zombie != null) {
      val victim = zombie.asInstanceOf[WeakIdentityReference[K]]
      backingStore.remove(victim)
      zombie = queue.poll()
    }
  }

  private final def reapThen[T](f: => T): T = { reap(); f }
}
