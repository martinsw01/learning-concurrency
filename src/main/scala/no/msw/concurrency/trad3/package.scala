package no.msw.concurrency

import scala.concurrent.ExecutionContext

package object trad3 {
  def execute(body: => Unit): Unit = ExecutionContext.global.execute(() => body)
}
