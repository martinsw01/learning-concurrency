package no.msw.concurrency.jmm2.monitors_and_sync.exercises

import no.msw.concurrency.jmm2.monitors_and_sync.deadlocks.SynchronizedNoDeadlock.Account
import no.msw.concurrency.jmm2.thread
import no.msw.concurrency.log


object RunExercises extends App {

  def e4(): Unit = {
    val syncVar = new SyncVar[Int]
    val producer = thread {
      var x = 0
      while (x < 15) {
        if (syncVar.isEmpty()) {
          syncVar.put(x)
          x += 1
        }
      }
    }
    val consumer = thread {
      var x = 0
      while (x != 14) {
        if (syncVar.nonEmpty()) {
          x = syncVar.get()
          log(x.toString)
        }
      }
    }
  }

  def e5(): Unit = {
    val syncVar = new SyncVar[Int]

    val producer = thread {
      var x = 0
      while (x < 15) {
        syncVar.putWait(x)
        x += 1
      }
    }
    val consumer = thread {
      var x = 0
      while (x != 14) {
        x = syncVar.getWait()
        log(x.toString)
      }
    }
    consumer.join()
  }

  def e6(): Unit = {
    val syncQueue = new SyncQueue[Int](1)

    val producer = thread {
      var x = 0
      while (x < 15) {
        syncQueue.putWait(x)
        x += 1
      }
    }
    val consumer = thread {
      var x = 0
      while (x != 14) {
        x = syncQueue.getWait()
        log(x.toString)
      }
    }
    consumer.join()
  }

  def e7(): Unit = {
    val accounts = (1 to 100).map(i => new Account(s"Account: $i",i*10)).toSet
    val target = new Account("Target account", 0)

    sendAll(accounts,target)

    accounts.foreach(a => log(s"${a.name}, money = ${a.money}"))
    log(s"${target.name} - money = ${target.money}")
  }

  def e8(): Unit = {
    val taskPool = new PriorityTaskPool

    taskPool.asynchronous(1) { log("1") }
    taskPool.asynchronous(2) { log("2") }
    taskPool.start()
    taskPool.asynchronous(3) { log("3") }
    taskPool.asynchronous(4) { log("4") }

    Thread.sleep(100)
  }

  def e9(): Unit = {
    val pool = new PriorityTaskThreadPool(4)
    for (i <- 0 until 10)
      pool.asynchronous(i % 3) { log(s"pri: ${i % 3}, i: $i") }

    Thread.sleep(100)
  }

  def e10(): Unit = {
    val pool = new GracefulPriorityTaskThreadPool(4, 1)
    for (i <- 0 until 10)
      pool.asynchronous(i % 3) { log(s"pri: ${i % 3}, i: $i") }

//    Thread.sleep(100)
    pool.start()
    pool.shutdown()
  }

  e10()
}
