import cats.Apply
import cats.implicits._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Apply是对Functor的扩展，增加了 `ap` 接口。它和 `map` 含义相同唯一差异是接收的参数类型：
 * - map是 A => B
 * - ap是 F[A] => F[B]
 *
 * 也就是说Functor的map，compose在Apply中均可使用，但Apply额外增加了ap。
 * 此外，由于apply的存在也必然涉及到函数参数的数量，可以是1个或者多个。
 */
class ApplySpec extends AnyFlatSpec with Matchers {

  val joinString: (String, String) => String = (x: String, y: String) => x + y
  val plusOne: Int => Int = (x: Int) => x + 1
  val sum2: (Int, Int) => Int = (x: Int, y: Int) => x + y
  val sum3: (Int, Int, Int) => Int = (x: Int, y: Int, z: Int) => x + y + z

  "map and compose" should "work like Functor" in {
    // map
    Apply[Option].map(Some("hello"))(_.length) should equal(Some(5))
    Apply[Option].map2(Some("1"), Some("2"))(joinString) should equal(Some("12"))

    // compose
    val listOpt = Apply[List] compose Apply[Option]
    val strLength = (x: String) => x.length
    listOpt.ap(List(Option(strLength)))(List(Some("hello"), None, Some("hey"))) should equal(List(Some(5), None, Some(3)))
  }

  "ap" should "support Functor transformation" in {
    Apply[Option].ap(Some(plusOne))(Some(5)) should equal(Some(6))
    Apply[Option].ap(None)(Some(5)) should equal(None)
    Apply[Option].ap(Some(plusOne))(None) should equal(None)

    Apply[Option].ap2(Some(sum2))(Some(1), Some(2)) should equal(Some(3))
    Apply[Option].ap3(Some(sum3))(Some(1), Some(2), Some(3)) should equal(Some(6))
    Apply[Option].ap3(Some(sum3))(Some(1), None, Some(3)) should equal(None)
  }

  "tuple" should "pack several elements into one tuple" in {
    Apply[Option].tuple2(Some(1), Some(2)) should equal(Some((1, 2)))
  }

  "apply builder syntax" should "be some sugar to simplify the code" in {
    val option2 = (Option(1), Option(2))
    val option3 = (option2._1, Option.empty[Int], option2._2.map(plusOne))
    option2.tupled should equal(Some(1, 2))
    option3.tupled should equal(None)

    option2 apWith Option(sum2) should equal(Option(3))
    option3 mapN sum3 should equal(None)
  }
}
