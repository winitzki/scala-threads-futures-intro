package example

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class FutureChainSpec extends FlatSpec with Matchers {

  behavior of "chained Future"

  it should "do computations using flatMap" in {
    // Each new future is created after the previous one is completed.
    // The entire computation takes about 4 seconds.
    val result: Future[Int] = {
      doComputation(10).flatMap { a ⇒
        doComputation(a).flatMap { b =>
          doComputation(b).flatMap { c =>
            doComputation(c).map { d =>
              a + b + c + d
            }
          }
        }
      }
    }
    val (elapsedTime, _) = elapsed {
      // The `result` is still in the future, need to wait for it.
      Await.result(result, Duration.Inf) shouldEqual 300
    }
    elapsedTime shouldEqual 4.0 +- 0.1
  }

  it should "do computations using a for-yield block" in {
    val result: Future[Int] = for {
      a <- doComputation(10) // First this will be done.
      b <- doComputation(a) // Then this.
      c <- doComputation(b) // Then that.
      d <- doComputation(c)
    } yield {
      a + b + c + d
    }
    val (elapsedTime, _) = elapsed {
      Await.result(result, Duration.Inf) shouldEqual 300
    }
    elapsedTime shouldEqual 4.0 +- 0.1
  }









  it should "perform parallel computations and wait for all to finish" in {
    // This starts all the Future values in parallel.
    // The computation takes about 1 second.
    val result1: Seq[Future[Int]] = Seq(10, 20, 30, 40).map { n ⇒ doComputation(n) }
    val result2: Future[Seq[Int]] = Future.sequence(result1)

    val sum: Future[Int] = result2.map((s: Seq[Int]) ⇒ s.sum)

    val (elapsedTime, _) = elapsed {
      Await.result(sum, Duration.Inf) shouldEqual 200
    }
    elapsedTime shouldEqual 1.0 +- 0.1
  }









  it should "use for-yield block to perform parallel computations in the future" in {
    // All futures are created up front and started in parallel.
    // The entire computation takes about 1 second.
    val futures: Seq[Future[Int]] = Seq(10, 20, 30, 40).map(doComputation)

    val futureSum: Future[Int] = for {
      a <- futures(0)
      b <- futures(1)
      c <- futures(2)
      d <- futures(3)
    } yield {
      a + b + c + d
    }
    val (elapsedTime, _) = elapsed {
      // The `result` is still in the future, need to wait for it.
      Await.result(futureSum, Duration.Inf) shouldEqual 200
    }
    elapsedTime shouldEqual 1.0 +- 0.1
  }

}
