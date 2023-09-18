package no.msw.concurrency.jmm2

/**
 * p. 31
 */

object ThreadsMain extends App {
  val t = Thread.currentThread
  val name = t.getName
  println(s"I am the thread $name")
}