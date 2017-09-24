package example

import org.scalatest.{FlatSpec, Matchers}
import Counter._

import scala.util.{Failure, Try}

class ThreadSpec extends FlatSpec with Matchers {

  behavior of "Thread"

  it should "start running when calling start()" in {

    counter.set(0)
    val t = new Thread(makeRunnable(1))
    Thread.sleep(1500)
    counter.get shouldEqual 0

    t.start()
    Thread.sleep(1500)
    counter.get shouldEqual 1
  }




  it should "exhaust memory when creating too many threads" in {
    val Some((result, failedTryNewThread)) = (1 to 10000).map { i ⇒
      val tryNewThread = try {
        Try(new Thread(makeRunnable(i)).start())
      } catch {
        case e: OutOfMemoryError ⇒ Failure(e)
      }
      (i, tryNewThread)
    }.find { case (i, tryNewThread) ⇒ tryNewThread.isFailure }

    println(s"Cannot create more than $result threads!")

    failedTryNewThread match {
      case Failure(throwable) ⇒ throwable should have message "unable to create new native thread"
      case _ ⇒ throw new Exception("Test failed!")
    }

    failedTryNewThread.get
  }

}
