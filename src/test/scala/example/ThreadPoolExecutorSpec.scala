package example

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import example.RunThis._
import org.scalatest.{FlatSpec, Matchers}

class ThreadPoolExecutorSpec extends FlatSpec with Matchers {

  behavior of "ThreadPoolExecutor"

  val counter = new AtomicInteger

  it should "run several tasks in parallel" in {
    val tpe = Executors.newFixedThreadPool(3)

    counter.set(0)

    val tasks: Seq[Runnable] = (1 to 5).map(makeRunnable)

    tasks.foreach(task ⇒ tpe.submit(task))

    Thread.sleep(2500) // hmmm.
    counter.get shouldEqual 15

    tpe.shutdown()
  }

  it should "be able to schedule tasks after exceptions" in {
    val tpe = Executors.newFixedThreadPool(3)

    counter.set(0)

    val tasks: Seq[Runnable] = (1 to 5).map(makeRunnableCrash) ++ (6 to 7).map(makeRunnable)

    tasks.foreach(task ⇒ tpe.submit(task))

    Thread.sleep(3500) // hmmm.
    counter.get shouldEqual 28

    tpe.shutdown()
  }

}
