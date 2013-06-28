package scala.react.test

import org.specs2.mutable.SpecificationWithJUnit
import org.junit.runner.RunWith
import org.junit.runners.Suite
import react.test.ReactiveTestUtils
import scala.react.EventSource
import scala.react.Reactor

class SpecDataflow extends SpecificationWithJUnit with ReactiveSpecUtils {
  "should implement map" ! {
    val es = new EventSource[Int]
    val states = new ListLog[Int]
    val res = Reactor.loop { self =>
      states.log(1)
      val x = self next es
      states.log(2)
      self.delay
    }
    states.values must_== List(1)
    es emit 0
    states.values must_== List(1, 2)
    advanceTime()
    states.values must_== List(1, 2, 1)
    es emit 1
    states.values must_== List(1, 2, 1, 2)
  }
}
