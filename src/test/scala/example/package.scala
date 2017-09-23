/**
  * Created by sergei.winitzki on 9/22/17.
  */
package object example {
  def elapsed[T](x: ⇒ T): (Double, T) ={
    val initial = System.nanoTime()
    val result = x
    ((System.nanoTime() - initial )  / 1000000000.0, result)
  }
}
