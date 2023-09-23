package no.msw.concurrency.jmm2.monitors_and_sync.interrupt

import no.msw.concurrency.log

import scala.annotation.tailrec
import scala.collection.mutable

object SynchronizedPoolGracefulShutdown extends App {
  val tasks = mutable.Queue[() => Unit]()

  object Worker extends Thread {
    setName("Worker")
    setDaemon(false)

    var terminated = false

    def poll(): Option[() => Unit] = tasks.synchronized {
      while (tasks.isEmpty && !terminated) tasks.wait()
      if (!terminated)
        Some(tasks.dequeue())
      else
        None
    }

    @tailrec override def run(): Unit = poll() match {
      case Some(task) => task(); run()
      case None => // Terminate
    }

    def shutdown(): Unit = tasks.synchronized {
      terminated = true
      tasks.notify()
    }
  }

  Worker.start()

  def asynchronous(body: => Unit): Unit = tasks.synchronized {
    tasks.enqueue(() => body)
    tasks.notify()
  }

  asynchronous {
    log("Hello")
  }
  asynchronous {
    log("World")
  }
  Worker.shutdown()
}