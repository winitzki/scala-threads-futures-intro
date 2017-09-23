package example

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Success, Try}

class FutureSpec extends FlatSpec with Matchers {

  // A single sequential computation takes 1 second.
  def doComputation(n: Int): Int = {
    Thread.sleep(1000)
    n * 2
  }

  behavior of "Future for parallel computations"

  it should "run a computation in the future" in {
    val f: Future[Int] = Future(doComputation(10))

    // Try to get the result `Int` value.
    val fValue: Option[Try[Int]] = f.value

    // The `Int` value is not yet available.
    fValue shouldEqual None
    // Wait.
    Thread.sleep(1500)
    // The value is available now, wrapped as an `Option[Try[Int]]`.
    f.value shouldEqual Some(Success(20))
  }

  it should "wait until result is available" in {
    val f: Future[Int] = Future(doComputation(10))

    val fValue: Int = Await.result(f, Duration.Inf)

    // The value is available now.
    fValue shouldEqual 20
  }

  it should "run several computations in parallel" in {
    val results: Seq[Future[Int]] = Seq(10, 20, 30, 40).map { n ⇒
      Future {
        doComputation(n)
      }
    }
    Thread.sleep(1500)
    // All results are available now.
    results.forall(_.value.isDefined) shouldEqual true
  }

  it should "use for-yield block to perform sequential computations in the future" in {
    // Each new future is created after the previous one is completed.
    // The entire computation takes about 4 seconds.
    val result: Future[Int] = for {
      a <- Future(doComputation(10)) // First this will be done.
      b <- Future(doComputation(20)) // Then this.
      c <- Future(doComputation(30)) // Then that.
      d <- Future(doComputation(40))
    } yield {
      a + b + c + d
    }

    val elapsedTime = elapsed {
      // The `result` is still in the future, need to wait for it.
      Await.result(result, Duration.Inf) shouldEqual 200
    }._1

    elapsedTime shouldEqual 4.0 +- 0.1
  }

  it should "perform parallel computations and wait for all to finish" in {
    // This starts all the Future values in parallel.
    // The computation takes about 1 second.
    val result1: Seq[Future[Int]] = Seq(10, 20, 30, 40).map { n ⇒
      Future {
        doComputation(n)
      }
    }
    val result2: Future[Seq[Int]] = Future.sequence(result1)

    val sum: Future[Int] = result2.map((s: Seq[Int]) ⇒ s.sum)

    val elapsedTime = elapsed {
      Await.result(sum, Duration.Inf) shouldEqual 200
    }._1

    elapsedTime shouldEqual 1.0 +- 0.1
  }

  it should "use for-yield block to perform parallel computations in the future" in {
    // All futures are created up front and started in parallel.
    // The entire computation takes about 1 second.
    val futures: Seq[Future[Int]] = Seq(10, 20, 30, 40).map(n ⇒ Future(doComputation(n)))

    val futureSum: Future[Int] = for {
      a <- futures(0)
      b <- futures(1)
      c <- futures(2)
      d <- futures(3)
    } yield {
      a + b + c + d
    }

    val elapsedTime = elapsed {
      // The `result` is still in the future, need to wait for it.
      Await.result(futureSum, Duration.Inf) shouldEqual 200
    }._1

    elapsedTime shouldEqual 1.0 +- 0.1
  }

}
