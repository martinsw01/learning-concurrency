package no.msw.concurrency.jmm2.monitors_and_sync.guarded_blocks

import no.msw.concurrency.log

import scala.collection.mutable

/**
 * p. 47
 *
 * Busy-waiting is worse than creating new threads
 */

object SynchronizedBadPool extends App {
  private val tasks = mutable.Queue[() => Unit]()

  private val worker: Thread = new Thread {
    def poll(): Option[() => Unit] = tasks.synchronized {
      if (tasks.nonEmpty)
        Some(tasks.dequeue())
      else
        None
    }

    override def run(): Unit = while (true) poll() match {  // busy-waiting
      case Some(task) => task()
      case None =>
    }
  }

  worker.setName("Worker")
  worker.setDaemon(true)
  worker.start()

  def asynchronous(body: =>Unit) = tasks.synchronized {
    tasks.enqueue(() => body)
  }

  asynchronous { log("Hello") }
  asynchronous { log("World") }
  Thread.sleep(20_000)
}
