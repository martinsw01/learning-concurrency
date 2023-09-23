package no.msw.concurrency.jmm2.monitors_and_sync.deadlocks

import no.msw.concurrency.jmm2.monitors_and_sync.SynchronizedNesting.Account
import no.msw.concurrency.jmm2.thread
import no.msw.concurrency.log

/**
 * p. 45
 *
 * t1 locks b, and t2 locks a, resulting in a deadlock
 */

object SynchronizedDeadlock extends App {
  def send(from: Account, to: Account, n: Int): Unit = from.synchronized {
    to.synchronized {
      from.money -= n
      to.money += n
    }
  }

  val a = new Account("Jack", 1_000)
  val b = new Account("Jill", 2_000)

  val t1 = thread { for (i <- 0 until 100) send(a, b, 1) }
  val t2 = thread { for (i <- 0 until 100) send(b, a, 1) }

  t1.join(); t1.join()

  log(s"a = ${a.money},  = ${b.money}")
}
