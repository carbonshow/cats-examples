import cats.{Applicative, Monad}
import cats.implicits._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Applicative是对Apply的扩展，增加了一个pure接口，将类型A返回其对应的Functor Context:
 * def pure[A](x: A): F[A]
 */
class ApplicativeSpec extends AnyFlatSpec with Matchers {
  it should "create functor from pure" in {
    Applicative[Option].pure(100) should equal(Some(100))
    Applicative[List].pure(1) should equal(List(1))
  }

  "compose" should "also work" in {
    val listOpt = Applicative[List] compose Applicative[Option]
    listOpt.pure(100) should equal (List(Some(100)))
  }

  "applicative functor" should "is a generalization of Monad" in {
    Applicative[Option].pure(1) should equal (Monad[Option].pure(1))
  }
}
