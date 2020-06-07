import cats._
import cats.implicits._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * 适用于可被折叠产生一个汇总性结果的数据，比如一些collection等。基本过程就是将这些集合中的数据逐一combine并产生最终单一结果。
 *
 * `Foldable[F]`有两种基本实现形式：
 * - `foldLeft(fa, b)(f)` 从左到右折叠fa，立刻执行，需要指定初始值。
 * - `foldRight(fa, b)(f)` 从右到左折叠fa，lazy模式，需要指定初始值。中间结果通过Eval[A]形式保存
 *
 * fold是将所有元素combine并给出最终结果，没有初始值
 */
class FoldableSpec extends AnyFlatSpec with Matchers {
  "foldLeft" should "eagerly fold from left to right with the initial value" in {
    Foldable[List].foldLeft(List(2, 3, 4), -1)(_ + _) should equal(8)
    Foldable[List].foldLeft(List("a", "b", "c"), "~")(_ + _) should equal("~abc")
  }

  "foldRight" should "lazily fold from right to left with the initial value" in {
    val result: Eval[Int] = Foldable[List].foldRight(List(1, 2, 3), Now(0))((x, rest) => Later(x + rest.value))
    result.value should equal(6)
  }

  "fold" should "combine all the elements from left to right" in {
    Foldable[List].fold(List(1, 2, 3)) should equal(6)
    Foldable[List].fold(List("a", "b", "c")) should equal("abc")
  }

  "foldMap" should "map the original value to a new one, then combine them all" in {
    Foldable[List].foldMap(List(1, 2, 3))(_.toString) should equal("123")
  }

  "foldK" should "combines all the values using MonoidK[G] instead of Monoid[G]" in {
    Foldable[List].foldK(List(List(1, 2), List(3, 4, 5))) should equal(List(1, 2, 3, 4, 5))
    Foldable[List].foldK(List(None, Some("12"), Some("345"))) should equal(Option("12"))
    Foldable[List].foldK(List(None, Some(1), Some(2))) should equal(Option(1))
    MonoidK[Option].combineK(Option(2), Option(1)) should equal(Option(2))
  }

  "search and predicate" should "works for find and exists" in {
    Foldable[List].find(List(1, 2, 3))(_ > 2) should equal(Option(3))
    Foldable[List].find(List(1, 2, 3))(_ > 5) should equal(None)
    Foldable[List].exists(List(1, 2, 3))(_ > 5) should equal(false)
    Foldable[List].forall(List(1, 2, 3))(_ < 2) should equal(false)
    Foldable[List].forall(List(1, 2, 3))(_ < 5) should equal(true)
  }

  "toList" should "put the value which satisfy constraints into List. if none, leave the list empty" in {
    Foldable[List].toList(List(1, 2, 3)) should equal(List(1, 2, 3))
    Foldable[Option].toList(Option(1)) should equal(List(1))
    Foldable[Option].toList(None) should equal(List())

    Foldable[List].filter_(List(1, 2, 3))(_ > 5) should equal(List())
    Foldable[List].filter_(List(1, 2, 3))(_ < 3) should equal(List(1, 2))
    Foldable[Option].filter_(Option(4))(_ > 1) should equal(List(4))
    Foldable[Option].filter_(Option(4))(_ != 4) should equal(List())
  }

  "traverse" should "mapping the value and then combine them while discarding the result" in {
    def parseInt(s: String): Option[Int] =
      Either.catchOnly[NumberFormatException](s.toInt).toOption

    Foldable[List].traverse_(List("1", "2", "3"))(parseInt) should equal(Some(()))
    Foldable[List].traverse_(List("a", "b", "c"))(parseInt) should equal(None)
  }

  "compose" should "make Foldable[F[_]] and Foldable[G[_]] into Foldable[F[G[_]]]" in {
    val listOption = Foldable[List] compose Foldable[Option]
    listOption.fold(List(Option(1), Option(2), None, Option(3))) should equal(6)
  }
}

