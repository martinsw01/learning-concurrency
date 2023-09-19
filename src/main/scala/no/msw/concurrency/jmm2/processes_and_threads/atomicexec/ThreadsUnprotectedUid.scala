package no.msw.concurrency.jmm2.processes_and_threads.atomicexec

import no.msw.concurrency.jmm2.thread
import no.msw.concurrency.log

/**
 * p. 37
 *
 * An example of a race condition.
 * Ids are not necessarily unique, as getUniqueId is not atomic
 */

object ThreadsUnprotectedUid extends App {
  var uidCount = 0L

  def getUniqueId() = {
    val freshUid = uidCount + 1
    uidCount = freshUid
    freshUid
  }

  def printUniqueIds(n: Int): Unit = {
    val uids = for (i<- 0 until n) yield getUniqueId()
    log(s"Generated uids: $uids")
  }

  val t = thread { printUniqueIds(5) }
  printUniqueIds(5)
  t.join()
}
