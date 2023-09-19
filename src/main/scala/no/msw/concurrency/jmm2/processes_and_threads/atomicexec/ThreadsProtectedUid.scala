package no.msw.concurrency.jmm2.processes_and_threads.atomicexec

import no.msw.concurrency.jmm2.thread
import no.msw.concurrency.log


/**
 * p. 38
 *
 * Make getUniqueId atomic with this.synchronized to avoid race conditions
 */


object ThreadsProtectedUid extends App {
  var uidCount = 0L

  def getUniqueId() = this.synchronized {
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
