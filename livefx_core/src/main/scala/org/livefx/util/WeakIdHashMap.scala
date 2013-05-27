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

//  type Entry = DefaultEntry[K, V]
//
//  override def empty: HashMap[A, B] = reapThen(backingStore.isEmpty)

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

//
//  override def foreach[C](f: ((K, V)) => C): Unit = foreachEntry(e => f(e.key, e.value))
//
//  override def keySet: scala.collection.Set[A] = new DefaultKeySet {
//    override def foreach[C](f: A => C) = foreachEntry(e => f(e.key))
//  }
//
//  /* Override to avoid tuple allocation in foreach */
//  override def values: scala.collection.Iterable[V] = new DefaultValuesIterable {
//    override def foreach[C](f: V => C) = foreachEntry(e => f(e.value))
//  }
//
//  /* Override to avoid tuple allocation */
//  override def keysIterator: Iterator[A] = new AbstractIterator[A] {
//    val iter    = entriesIterator
//    def hasNext = iter.hasNext
//    def next()  = iter.next.key
//  }
//
//  /* Override to avoid tuple allocation */
//  override def valuesIterator: Iterator[V] = new AbstractIterator[V] {
//    val iter    = entriesIterator
//    def hasNext = iter.hasNext
//    def next()  = iter.next.value
//  }
//
//  def useSizeMap(t: Boolean) = if (t) {
//    if (!isSizeMapDefined) sizeMapInitAndRebuild
//  } else sizeMapDisable
//
//  protected def createNewEntry[B1](key: K, value: B1): Entry = {
//    new Entry(key, value.asInstanceOf[V])
//  }
//
//  private def writeObject(out: java.io.ObjectOutputStream) {
//    serializeTo(out, { entry =>
//      out.writeObject(entry.key)
//      out.writeObject(entry.value)
//    })
//  }
//
//  private def readObject(in: java.io.ObjectInputStream) {
//    init(in, createNewEntry(in.readObject().asInstanceOf[A], in.readObject()))
//  }

  private final def reap(): Unit = this.synchronized {
    var zombie = queue.poll()

    while (zombie != null) {
      val victim = zombie.asInstanceOf[WeakIdRef[K]]
      backingStore.remove(victim)
      zombie = queue.poll()
    }
  }

  private final def reapThen[T](f: => T): T = { reap(); f }

//  override def + [V1 >: V] (kv: (K, V1)): Map[K, V1] = ???
//  override def -(key: K): this.type = clone().asInstanceOf[this.type] -= key
}

//object HashMap extends MutableMapFactory[HashMap] {
//  implicit def canBuildFrom[A, B]: CanBuildFrom[Coll, (A, B), HashMap[A, B]] = new MapCanBuildFrom[A, B]
//  def empty[A, B]: HashMap[A, B] = new HashMap[A, B]
//}
//
//
//
//
//
//class WeakIdHashMap[K, V] extends Map[K, V] {
////  override def entrySet(): Set[Map.Entry[K, V]] = reapThen {
////    val ret = new HashSet[Map.Entry[K, V]]
////
////    for (ref <- backingStore.entrySet()) {
////      val key = ref.getKey.get
////      val value = ref.getValue
////      val entry = new Map.Entry[K, V] {
////        override def getKey(): K = key
////        override def getValue(): V = value
////        override def setValue(value: V): V = throw new UnsupportedOperationException
////      }
////
////      ret.add(entry)
////    }
////
////    Collections.unmodifiableSet(ret)
////  }
//  
//  override def keySet(): Set[K] = reapThen {
//    val ret = new HashSet[K]
//
//    for (ref <- backingStore.keySet) {
//        ret.add(ref.get)
//    }
//
//    Collections.unmodifiableSet(ret)
//  }
//
//  override def equals(any: Any): Boolean = {
//    backingStore.equals(any.asInstanceOf[WeakIdHashMap[K, V]].backingStore)
//  }
//
//  override def get(key: Any): V = reapThen {
//    backingStore.get(WeakIdRef(key.asInstanceOf[K]))
//  }
//
//  override def put(key: K, value: V): V = reapThen {
//    backingStore.put(WeakIdRef(key), value)
//  }
//
//  override def remove(key: Any): V  = reapThen {
//    backingStore.remove(WeakIdRef(key.asInstanceOf[K]))
//  }
//  
//  override def size(): Int = reapThen(backingStore.size)
//
//  override def values(): Collection[V] = reapThen(backingStore.values)
//
//  private final def reap(): Unit = this.synchronized {
//    var zombie = queue.poll()
//
//    while (zombie != null) {
//      val victim = zombie.asInstanceOf[WeakIdRef[K]]
//      backingStore.remove(victim)
//      zombie = queue.poll()
//    }
//  }
//
//  private final def reapThen[T](f: => T): T = { reap(); f }
//}
