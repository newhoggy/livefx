/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2007-2010, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scala.react

trait Publisher[+Msg, +Now] extends MessageCachingReactive[Msg, Now] {
	private[this] var _dependents = newDependents
	private[this] def newDependents = new WeakHashSet[Dependent]

	override def subscribe(dep: Dependent): Unit = {
		if (dep != Observer.Nil) {
			_dependents += dep
			log("Added dependent " + dbgInfo(dep) + " to " + dbgInfo(this))
			dep.dependsOn(this)
		}
	}

	def dependents = _dependents

	private[react] def clearDependents(): WeakHashSet[Dependent] = {
		val old = _dependents
		_dependents = newDependents
		old
	}

	protected[this] def propagate(msg: Msg): Unit = {
		cacheMessage(msg)
		Engine.propagateFrom(this)
	}
}
