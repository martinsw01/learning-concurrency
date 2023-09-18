package no.msw.concurrency

package object jmm2 {
  def thread(body: => Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }
}