package no.msw.concurrency.jmm2.processes_and_threads.reordering

import no.msw.concurrency.jmm2.thread

/**
 * p. 40
 *
 * x and y should never simultaneously be 1, but it can happen in this example.
 * The JVM is allowed to reorder certain program statements under some conditions.
 */

object ThreadsSharedStateAccessReordering extends App {
  for (_ <- 0 until 100_000) {
    var a = false
    var b = false
    var x = -1
    var y = -1
    val t1 = thread {
      a = true
      y = if (b) 0 else 1
    }
    val t2 = thread {
      b = true
      x = if (a) 0 else 1
    }
    t1.join()
    t2.join()
    assert(!(x==1 && y==1), s"x = $x, y = $y")
  }
}
