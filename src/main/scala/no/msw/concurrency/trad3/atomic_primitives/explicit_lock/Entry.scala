package no.msw.concurrency.trad3.atomic_primitives.explicit_lock

import java.util.concurrent.atomic.AtomicReference

class Entry(val isDir: Boolean) {
  val state = new AtomicReference[State](new Idle)
}