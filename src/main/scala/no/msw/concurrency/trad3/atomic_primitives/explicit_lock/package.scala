package no.msw.concurrency.trad3.atomic_primitives

import no.msw.concurrency.log

import scala.annotation.tailrec

package object explicit_lock {
  @tailrec private
  def prepareForDelete(entry: Entry): Boolean = {
    val initialState = entry.state.get()
    initialState match {
      case idle: Idle =>
        if (entry.state.compareAndSet(initialState, new Deleting))
          true
        else
          prepareForDelete(entry)
      case creating: Creating =>
        log("Cannot delete file being created")
        false
      case copying: Copying =>
        log("Cannot delete file being copied")
        false
      case deleting: Deleting =>
        false
    }
  }
}
