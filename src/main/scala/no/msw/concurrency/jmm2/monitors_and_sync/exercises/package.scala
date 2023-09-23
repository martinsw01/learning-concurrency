package no.msw.concurrency.jmm2.monitors_and_sync

import no.msw.concurrency.jmm2.monitors_and_sync.deadlocks.SynchronizedNoDeadlock.Account
import no.msw.concurrency.jmm2.thread

import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.duration.{Duration, pairIntToDuration}

package object exercises {
  def parallel[A, B](a: =>A, b: =>B): (A, B) = {
    var v1: Option[A] = None
    var v2: Option[B] = None

    val t1 = thread { v1 = Some(a) }
    val t2 = thread { v2 = Some(b) }

    t1.join(); t2.join()

    (v1.get, v2.get)
  }

  def periodically(duration: Duration)(b: =>Unit): Unit = {
    thread {
      while (true) {
        b
        Thread.sleep(duration.toMillis)
      }
    }
  }

  class SyncVar[T] {
    var t: T = null.asInstanceOf[T]
    var empty = true

    def get(): T = this.synchronized {
      if (empty)
        throw new Exception("No value present")
      else {
        empty = true
        t
      }
    }

    def put(x: T): Unit = this.synchronized {
      if (empty) {
        empty = false
        t = x
      } else
        throw new Exception("Value already present")
    }

    def isEmpty(): Boolean = this.synchronized {
      empty
    }

    def nonEmpty(): Boolean = this.synchronized {
      !empty
    }

    def getWait(): T = this.synchronized {
      if (empty) {
        this.wait()
        getWait()
      } else {
        empty = true
        this.notify()
        t
      }
    }

    def putWait(x: T): Unit = this.synchronized {
      if (empty) {
        empty = false
        t = x
        this.notify()
      } else {
        this.wait()
        putWait(x)
      }
    }
  }

  class SyncQueue[T](val n: Int) {
    private val queue = mutable.Queue[T]()
    private var size = 0

    def put(x: T): Unit = this.synchronized {
      if (size < n) {
        queue.enqueue(x)
        size += 1
      } else
        throw new Exception("Queue is full!")
    }

    def get(): T = this.synchronized {
      if (queue.nonEmpty) {
        size -= 1
        queue.dequeue()
      } else
        throw new Exception("Queue is empty!")
    }

    def isEmpty(): Boolean = this.synchronized { queue.isEmpty }
    def nonEmpty(): Boolean = this.synchronized { queue.nonEmpty }

//    @tailrec
    final def putWait(x: T): Unit = this.synchronized {
      if (size < n) {
        queue.enqueue(x)
        size += 1
        this.notify()
      } else {
        this.wait()
        putWait(x)
      }
    }

    final def getWait(): T = this.synchronized {
      if (isEmpty()) {
        this.wait()
        getWait()
      } else {
        size -= 1
        val x = queue.dequeue()
        if (size == n-1)
          this.notify()
        x
      }
    }
  }

  def sendAll(accounts: Set[Account], target: Account): Unit = {
    def adjust(from: Account): Unit = {
      target.money += from.money
      from.money = 0
    }
    for (account <- accounts) {
      if (account.uid < target.uid)
        account.synchronized { target.synchronized {
          adjust(account)
        }}
      else
        target.synchronized { target.synchronized {
          adjust(account)
        }}
    }
  }

  class PrioritizedTask(val task: () => Unit, val priority: Int)

  class PriorityTaskPool {
    private val priorityQueue = mutable.PriorityQueue.empty(Ordering.fromLessThan[PrioritizedTask](_.priority < _.priority))

    private object Worker extends Thread {
      setName("Worker")
      setDaemon(true)

      def poll(): () => Unit = priorityQueue.synchronized {
        while (priorityQueue.isEmpty)
          priorityQueue.wait()
        priorityQueue.dequeue().task
      }

      override def run(): Unit = while (true) {
        val task = poll()
        task()
      }
    }

    def start(): Unit = {
//      println("start")
      Worker.start()
    }
    def asynchronous(priority: Int)(task: =>Unit): Unit = priorityQueue.synchronized {
      val prioritizedTask = new PrioritizedTask(() => task, priority)
      priorityQueue.enqueue(prioritizedTask)
      priorityQueue.notify()
    }
  }

  class PriorityTaskThreadPool {
    private val ordering = Ordering.fromLessThan[PrioritizedTask](_.priority < _.priority)
    private val priorityQueue = mutable.PriorityQueue.empty(ordering)

    class Worker extends Thread {
      setDaemon(true)
      def poll(): () => Unit = priorityQueue.synchronized {
        while (priorityQueue.isEmpty)
          priorityQueue.wait()
        priorityQueue.dequeue().task
      }
      override def run(): Unit = while (true) {
        val task = poll()
        task()
      }
    }

    def this(threads: Int) {
      this()
      for (_ <- 1 to threads)
        new Worker().start()
    }

    def asynchronous(priority: Int)(task: =>Unit): Unit = priorityQueue.synchronized {
      val prioritizedTask = new PrioritizedTask(() => task, priority)
      priorityQueue.enqueue(prioritizedTask)
      priorityQueue.notify()
    }
  }

  class GracefulPriorityTaskThreadPool(private val threads: Int, private val important: Int) {

    private val ordering = Ordering.fromLessThan[PrioritizedTask](_.priority < _.priority)
    private val priorityQueue = mutable.PriorityQueue.empty(ordering)

    private var terminate = false

    class Worker extends Thread {
      def poll(): Option[() => Unit] = priorityQueue.synchronized {
        while (priorityQueue.isEmpty && !terminate)
          priorityQueue.wait()
        if (!terminate)
          Some(priorityQueue.dequeue().task)
        else if (priorityQueue.nonEmpty) {
          val prioritizedTask = priorityQueue.dequeue()
          if (prioritizedTask.priority >= important)
            Some(prioritizedTask.task)
          else None
        }
        else
          None
      }

      @tailrec
      final override def run(): Unit = poll() match {
        case Some(task) => task(); run()
        case None =>
      }
    }

    def start(): Unit = {
      for (_ <- 1 to threads)
        new Worker().start()
    }

    def asynchronous(priority: Int)(task: =>Unit): Unit = priorityQueue.synchronized {
      val prioritizedTask = new PrioritizedTask(() => task, priority)
      priorityQueue.enqueue(prioritizedTask)
      priorityQueue.notify()
    }

    def shutdown(): Unit = priorityQueue.synchronized {
      terminate = true
      priorityQueue.notifyAll()
    }
  }
}
