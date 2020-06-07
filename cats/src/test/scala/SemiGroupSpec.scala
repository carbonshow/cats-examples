import cats.Semigroup
import cats.implicits._
import org.scalatest._
import org.scalatest.matchers.should.Matchers

/**
 * 两个具有相同数据类型A的元素，进行合并(combine)操作并返回类型为A的结果。需要满足结合律。
 *
 * 比如：(a combine b) combine c 等价于a combine (b combine c)
 *
 * 使用时需要import cats.SemiGroup，当然为了便利需要隐式转换，import cats.implicits._
 * 值得注意的几个点：
 * - 基础数据类型，比如Int，String等，combine就相当于 +
 * - 容器，比如Seq, Map等，相当于 ++
 * - Option，如果是None那么忽略，如果是Some(a), Some(b)，则对a和b进行combine操作
 * - 可以指定函数映射，将输入地数据进行变换后再进行combine
 */
class SemiGroupSpec extends flatspec.AnyFlatSpec with Matchers{
  "Basic Value Type" should "perform like operator +" in {
    Semigroup[Int].combine(1, 2) should equal (3)
    Semigroup[String].combineAllOption(List("1", "23", "4")) should equal (Some("1234"))
  }

  "collection" should "perform like operator ++" in {
    // List
    Semigroup[List[Int]].combine(List(1, 2, 3), List(7,9,11)) should equal (List(1,2,3,7,9,11))

    // Map
    val aMap = Map("foo" -> Map("bar" -> 5))
    val anotherMap = Map("foo" -> Map("bar" -> 6))
    val combinedMap = Semigroup[Map[String, Map[String, Int]]].combine(aMap, anotherMap)
    combinedMap("foo")("bar") should equal (11)
  }

  "option" should "combine for \"some\" case" in {
    assert(Semigroup[Option[Int]].combine(Some(10), None).contains(10))
    Semigroup[Option[Int]].combine(Some(1), Some(2)) should equal(Some(3))
  }

  "function" should "combine the transformed value" in {
    Semigroup[Int => String].combine(x => (x+1).toString , x => (x-1).toString).apply(9) should equal ("108")
  }
}
