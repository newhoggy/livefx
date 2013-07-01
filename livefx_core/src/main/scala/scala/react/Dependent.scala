/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2007-2010, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scala.react

/** A node in the dependency graph that has dependencies.
  */
trait Dependent extends DependencyNode {
	@throws(classOf[LevelMismatchNow]) def receive(eng: Engine): Unit

	def dependsOn[A, B](reactive: Dependency[A, B]): Unit

	/** Updates the internal state so that `valid` immediately after a call to this method
	  * will return `false`.
	  *
	  * Important(!): Never ever call this method outside of an `Engine` implementation!
	  */
	private[react] def invalidate(): Unit
}

/** Convenience implementation trait.
  */
trait InvalidatableDependent extends Dependent {
	protected var _valid = false

	def valid = _valid

	private[react] def invalidate(): Unit = {
		_valid = false
	}
}

trait LevelCachingDependent extends Dependent {
	protected var _level = Int.MinValue

	def level = _level

	def dependsOn[A, B](dep: Dependency[A, B]): Unit = {
		val oldLevel = _level
		_level = math.max(dep.level + 1, _level)
	}
}

