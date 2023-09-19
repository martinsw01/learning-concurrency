package no.msw.concurrency.jmm2.processes_and_threads.threads

import no.msw.concurrency.jmm2.thread
import no.msw.concurrency.log

/**
 * p. 35
 *
 * Order of execution is nondeterministic.
 */

object ThreadsNondeterminism extends App {
  val t = thread {
    log("New thread running.")
  }
  log("...")
  log("...")
  t.join()
  log("New thread joined")
}
