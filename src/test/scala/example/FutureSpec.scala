package example

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Success, Try}

class FutureSpec extends FlatSpec with Matchers {

  behavior of "Future for parallel computations"

  it should "run a computation in the future" in {
    val f: Future[Int] = doComputation(10)

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
    val f: Future[Int] = doComputation(10)

    val fValue: Int = Await.result(f, Duration.Inf)

    // The value is available now.
    fValue shouldEqual 20
  }














  it should "run several computations in parallel" in {
    val results: Seq[Future[Int]] = Seq(10, 20, 30, 40).map { n ⇒ doComputation(n) }
    Thread.sleep(1500)
    // All results are available now.
    results.forall(_.value.isDefined) shouldEqual true
  }

}
