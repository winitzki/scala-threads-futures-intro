package example

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, blocking}

class ThreadStarvationSpec extends FlatSpec with Matchers {
  val cpus = Runtime.getRuntime.availableProcessors()

  behavior of "blocking() instruction"

  it should "cause thread starvation without blocking() call" in {
    val elapsedTime = elapsed {
      Await.ready(Future.sequence((1 to 2 * cpus).map(n ⇒ Future {
        doComputation(n)
      })), Duration.Inf)
    }._1

    elapsedTime shouldEqual 2.0 +- 0.1
    println(s"Running ${2 * cpus} parallel computations takes twice as long.")
  }

  it should "maintain parallelism with blocking() call" in {
    val elapsedTime = elapsed {
      Await.ready(Future.sequence((1 to 2 * cpus).map(n ⇒ Future {
        blocking(doComputation(n))
      })), Duration.Inf)
    }._1

    elapsedTime shouldEqual 1.0 +- 0.1
    println(s"Running ${2 * cpus} parallel computations with blocking() instruction takes optimal time.")
  }
}
