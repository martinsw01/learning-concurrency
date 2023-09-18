package no.msw.concurrency.jmm2

import no.msw.concurrency.jmm2.thread
import no.msw.concurrency.log

/**
 * p. 34
 *
 * Main thread allways waits for the thread to terminate after Thread.join()
 */

object ThreadsSleep extends App {
  val t = thread {
    Thread.sleep(1000)
    log("New thread running.")
    Thread.sleep(1000)
    log("Still running.")
    Thread.sleep(1000)
    log("Completed.")
  }
  t.join()
  log("New thread joined")
}