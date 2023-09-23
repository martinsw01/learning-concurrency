package no.msw.concurrency.jmm2.monitors_and_sync.deadlocks

import no.msw.concurrency.jmm2.processes_and_threads.atomicexec.ThreadsProtectedUid.getUniqueId
import no.msw.concurrency.jmm2.thread
import no.msw.concurrency.log

/**
 * p. 46
 * 
 * Establish a total order between resources when requiring them to avoid deadlocks
 */

object SynchronizedNoDeadlock extends App {
  class Account(val name: String, var money: Int) {
    final val uid = getUniqueId()
  }
  
  def send(from: Account, to: Account, money: Int): Unit = {
    def adjust(): Unit = {
      to.money += money
      from.money -= money
    }
    if (from.uid < to.uid)
      from.synchronized { to.synchronized { adjust() } }
    else
      to.synchronized { from.synchronized { adjust() } }
  }

  val a = new Account("Jack", 1_000)
  val b = new Account("Jill", 2_000)

  val t1 = thread { for (i <- 0 until 100) send(a, b, 1) }
  val t2 = thread { for (i <- 0 until 100) send(b, a, 1) }

  t1.join(); t1.join()

  log(s"a = ${a.money},  = ${b.money}")
}
