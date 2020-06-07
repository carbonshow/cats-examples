import cats.Monad
import cats.data.OptionT
import cats.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Monad是Applicative的扩展，增加了flatten接口，对于嵌套的Functor Context，可以将其合并，也就是：`F[F[A]]`变为F[A]
 *
 * 核心功能有：
 * - flatten, flatMap，就是字面意思，合并嵌套，或者变换后合并嵌套
 * - ifm，下一步行动依赖于上一步结果
 */
class MonadSpec extends AnyFlatSpec with Matchers {
  "flatten" should "turn nested context of functors into a single one" in {
    Option(Option(1)).flatten should equal(Option(1))
    List(List(1), List(2, 3), List(3, 4)).flatten should equal(List(1, 2, 3, 3, 4))

    Monad[List].flatMap(List(1, 2, 3))(x => List(x, x + 1)) should equal(List(1, 2, 2, 3, 3, 4))
  }

  "ifm" should "works like the if statement" in {
    Monad[Option].ifM(Option(true))(Option("true"), Option("false")) should equal(Option("true"))
    Monad[List].ifM(List(true, false, true))(List(1, 2), List(-2, -1)) should equal(List(1, 2, -2, -1, 1, 2))
  }

}
