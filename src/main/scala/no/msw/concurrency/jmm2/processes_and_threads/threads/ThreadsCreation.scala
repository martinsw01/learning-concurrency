package no.msw.concurrency.jmm2.processes_and_threads.threads

/**
 * p. 32
 */

object ThreadsCreation extends App {
  class MyThread extends Thread {
    override def run(): Unit = {
      println("New thread running.")
    }
  }

  val t = new MyThread // new
  t.start() // runnale
  t.join() // terminated
  println("New thread joined")
}
