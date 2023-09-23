package no.msw.concurrency.jmm2.monitors_and_sync.guarded_blocks

import no.msw.concurrency.log

import scala.collection.mutable

/**
 * p. 50
 *
 * Use guarded locks to wait for results
 */

object SynchronizedPool extends App {
  val tasks = mutable.Queue[() => Unit]()

  object Worker extends Thread {
    setName("Worker")
    setDaemon(true)

    def poll(): () => Unit = tasks.synchronized {
      while (tasks.isEmpty) tasks.wait()
      tasks.dequeue()
    }

    override def run(): Unit = while (true) {
      val task = poll()
      task()
      log("Doing task!")
    }
  }

  Worker.start()

  def asynchronous(body: =>Unit): Unit = tasks.synchronized {
    tasks.enqueue(() => body)
    tasks.notify()
  }

  asynchronous { log("Hello") }
  asynchronous { log("World") }
  Thread.sleep(20_000)
}
