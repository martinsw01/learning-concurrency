package no.msw.concurrency.jmm2.processes_and_threads.atomicexec

import no.msw.concurrency.jmm2.thread
import no.msw.concurrency.log

/**
 * p. 36
 *
 * All writes to memory occurs Thread.join() returns, and are thus visible to the main thread.
 */

object ThreadsCommunicate extends App {
  var result: String = null
  val t = thread { result = "\nTitle\n" + "=" * 5 }
  t.join()
  log(result)
}
