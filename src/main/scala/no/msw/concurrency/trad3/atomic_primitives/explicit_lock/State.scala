package no.msw.concurrency.trad3.atomic_primitives.explicit_lock

sealed trait State
class Idle extends State
class Creating extends State
class Copying(val n: Int) extends State
class Deleting extends State
