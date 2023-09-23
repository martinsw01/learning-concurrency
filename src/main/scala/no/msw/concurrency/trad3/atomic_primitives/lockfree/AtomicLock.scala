package no.msw.concurrency.trad3.atomic_primitives.lockfree

import no.msw.concurrency.log
import no.msw.concurrency.trad3.execute

import java.util.concurrent.atomic.AtomicBoolean

/**
 * p. 72
 *
 * Using atomic variables is not sufficient for lock-freedom.
 * A lock can be created using atomic variables.
 */

object AtomicLock extends App {
  private val lock = new AtomicBoolean(false)
  def mySynchronized[T](body: => T): T = {
    while (!lock.compareAndSet(false, true)) {}
    try body
    finally lock.set(false)
  }

  var count = 0
  for (_ <- 0 until 10) execute { mySynchronized { count += 1 } }
  Thread.sleep(1000)
  log(s"Count is: $count")
}
