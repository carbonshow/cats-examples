import cats.Functor
import cats.implicits._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Functor的特点有两个：
 * - 首先是一个泛型class，接收且只接受一个类型参数，比如：F[A]
 * - 只提供一个接口map，将数据从类型A转换为类型B，比如：F[A]转化为F[B]
 *
 * 所以核心是什么：F不变，A变成B。
 *
 * scala标准库中List，Option等属于Functor，但Int、Tuple则不算。前者非泛型，后者可以接收多个类型。
 *
 * Functor实例的创建，可以来自于单一类型参数类。无论是否支持map接口均可创建：
 * {{{
 * // 支持map的接口
 * implicit val optionFunctor: Functor[Option] = new Functor[Option] {
 *   def map[A, B](fa: Option[A])(f: A => B) = fa map f
 * }
 *
 * // 无map接口，使用andThen。注意需要使用Function1[In, ?]来定义从In到A的变换
 * implicit def function1Functor[In]: Functor[Function1[In, ?]] =
 *   new Functor[Function1[In, ?]] {
 *     def map[A, B](fa: In => A)(f: A => B): Function1[In, B] = fa andThen f
 *   }
 * }}}
 *
 * 除此外有一些关键接口：
 * - lift，普通变换转化为Functor变换
 * - fproduct，将变化前后地值组合为pair
 * - compose，将多个map变换组合起来
 */
class FunctorSpec extends AnyFlatSpec with Matchers {
  "map" should "perform type transformation" in {
    Functor[Option].map(Option(123))(_.toString) should equal(Some("123"))
    Functor[List].map(List("1", "ab", ",.o"))(_.length) should equal(List(1, 2, 3))
    Functor[Option].map(None: Option[Int])(_ + 3) should equal(None)
  }

  "lift" should "turn function A => B to F[A] => F[B]" in {
    val optionLift: Option[Int] => Option[String] = Functor[Option].lift(_.toString)
    optionLift(Some(145)) should equal (Some("145"))
  }

  "fproduct" should "return the pair of original value and result value" in {
    Functor[List].fproduct(List("hello", "the", "cats"))(_.length).toMap.get("the") should equal (Some(3))
  }

  "compose" should "compose the two transformation into one: F[] compose G[] equals to F[G[]]" in {
    val listOption = Functor[List] compose Functor[Option]
    listOption.map(List(Some(1), None, Some(3)))(_ + 1) should equal(List(Some(2), None, Some(4)))
  }
}
