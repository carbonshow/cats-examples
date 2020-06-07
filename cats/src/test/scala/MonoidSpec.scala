import cats.Monoid
import cats.implicits._
import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers

/**
 * Monoid是对SemiGroup的拓展：提供了空值接口，也就是除了combine，还需要处理遇到空值的情况：
 * combine(x, empty) == combine(empty, x) == x
 *
 * 另外比较有意思的地方是：提供foldMap接口，遍历输入序列，对其中的每个元素形变，并combine生成最终结果
 */
class MonoidSpec extends AnyFlatSpec with Matchers {
  "Combine" should "handle empty values" in {
    Monoid[String].combineAll(List.empty) should equal("")
    Monoid[String].combineAll(List("1", "2", "3")) should equal("123")
    Monoid[Option[String]].combineAll(List(None, Some("abc"), Some("def"))) should equal(Some("abcdef"))
    Monoid[String].empty should equal("")

    Monoid[Option[Int]].combineAll(List(Some(1), None, Some(2))) should equal(Some(3))
    Monoid[Map[Int, String]].combineAll(List(
      Map(1 -> "one"),
      Map.empty,
      Map(2 -> "two"),
      Map(1 -> "one"))) should equal(Map(1 -> "oneone", 2 -> "two"))
  }

  "foldMap" should "map values while traversing and give the accumulated result" in {
    val input = List(1, 2, 3, 4, 5)
    input.foldMap(_.toString) should equal ("12345")
    input.foldMap(i => (i, i.toString)) should equal((15, "12345"))
  }
}
