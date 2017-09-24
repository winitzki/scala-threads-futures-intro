package example

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, blocking}
import Counter._


class ThreadStarvationSpec extends FlatSpec with Matchers {
  val cpus = Runtime.getRuntime.availableProcessors()

  behavior of "blocking() instruction"

  it should "cause thread starvation without blocking() call" in {
    val (elapsedTime, _) = elapsed {
      Await.ready(Future.sequence((1 to 2 * cpus).map(doComputation)), Duration.Inf)
    }
    elapsedTime shouldEqual 2.0 +- 0.1
    println(s"Running ${2 * cpus} parallel computations takes twice as long.")
  }








  it should "maintain parallelism with blocking() call" in {
    val (elapsedTime, _) = elapsed {
      Await.ready(Future.sequence((1 to 2 * cpus).map(n ⇒ Future {
        blocking(makeRunnable(n).run())
      })), Duration.Inf)
    }
    elapsedTime shouldEqual 1.0 +- 0.1
    println(s"Running ${2 * cpus} parallel computations with blocking() instruction takes optimal time.")
  }

}
