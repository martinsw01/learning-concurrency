package no.msw.concurrency.trad3.atomic_primitives.variables

import no.msw.concurrency.log
import no.msw.concurrency.trad3.execute

import java.util.concurrent.atomic.AtomicLong

object AtomicUid extends App {
  private val uid = new AtomicLong(0L)
  def getUniqueId(): Long = uid.incrementAndGet()

  execute { log(s"Uid asynchronously: ${getUniqueId()}") }
  log(s"Got a unique id: ${getUniqueId()}")
}
