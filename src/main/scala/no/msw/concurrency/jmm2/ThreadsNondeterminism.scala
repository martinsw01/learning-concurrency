package no.msw.concurrency.jmm2

import no.msw.concurrency.log
import no.msw.concurrency.jmm2.thread

/**
 * p. 35
 *
 * Order of execution is nondeterministic.
 */

object ThreadsNondeterminism extends App {
  val t = thread { log("New thread running.") }
  log("...")
  log("...")
  t.join()
  log("New thread joined")
}