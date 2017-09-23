package example

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

object RunThis extends App {
  val counter = new AtomicInteger

  def makeRunnable(n: Int): Runnable = { () ⇒
    Thread.sleep(1000)
    val r = counter.addAndGet(n)
    println(s"Counter is now $r")
  }

  def makeRunnableCrash(n: Int): Runnable = { () ⇒
    Thread.sleep(1000)
    val r = counter.addAndGet(n)
    println(s"Crashing, counter is now $r")
    throw new RuntimeException("error, please ignore")
    println("This message should never be printed")
  }

  val tpe = Executors.newFixedThreadPool(3)

  counter.set(0)

  val tasks: Seq[Runnable] = (1 to 5).map(makeRunnableCrash) ++ (6 to 7).map(makeRunnable)

  tasks.foreach(task ⇒ tpe.submit(task))
}
