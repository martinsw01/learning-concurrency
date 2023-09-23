package no.msw.concurrency.jmm2.monitors_and_sync.guarded_blocks

import no.msw.concurrency.jmm2.thread
import no.msw.concurrency.log

/**
 * p. 50
 *
 * Example on how to use a lock
 */

object SynchronizedGuardedBlocks extends App {
  val lock = new AnyRef
  var message: Option[String] = None
  val greeter = thread {
    lock.synchronized {
      while (message.isEmpty) lock.wait()
      log(message.get)
    }
  }
  lock.synchronized {
    message = Some("Hello!")
    lock.notify()
  }

  greeter.join()
}
