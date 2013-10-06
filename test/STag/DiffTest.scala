package STag

import org.specs2.mutable._
import tags.Elements._
import tags._

import play.Logger
import play.api.test._
import play.api.test.Helpers._

class DiffTest extends Specification {
  /*
  "Single different classDiff" should {
    "return Right(Some(TagDiff))" in {
      val from = div.id("A").cls("classA")("test")
      val to = div.id("A").cls("classB")("test")
      val diffResult: Diff = diff(from, to)
      diffResult must beEqualTo((Right(Some(TagDiff("A", Some(List("classB")), None, List(Right(None)))))))

    }
  }

  "Single cssDiff" should {
    "return Right(Some(TagDiff))" in {
      val from = div.id("A").css("margin" -> "10px")("test")
      val to = div.id("A").css("margin" -> "20px")("test")
      val diffResult: Diff = diff(from, to)
      diffResult must beEqualTo((Right(Some(TagDiff("A", None, Some(List(("margin", "20px"))), List(Right(None)))))))

    }
  }

  "Single classDiff and cssDiff" should {
    "return Right(Some(TagDiff))" in {
      val from = div.id("A").cls("classA").css("margin" -> "10px")("test")
      val to = div.id("A").cls("classB").css("margin" -> "20px")("test")
      val diffResult: Diff = diff(from, to)
      diffResult must beEqualTo((Right(Some(TagDiff("A", Some(List("classB")), Some(List(("margin", "20px"))), List(Right(None)))))))

    }
  }


  "Text Node ChildDiff" should {
    "return Right(Some(TagDiff)) with STag Child" in {
      val from = div.id("A").cls("classA").css("margin" -> "10px")("test")
      val to = div.id("A").cls("classB").css("margin" -> "21px")("testA")
      val diffResult: Diff = diff(from, to)
      (diffResult match {
        case Right(Some(TagDiff(id, Some(List(cls)), Some(List((css))), child :: tail))) =>
          if( cls == "classB" && css == ("margin","21px") && isSTagChild(child,"testA")) true else false
        case _ => false
      }) must beTrue

    }
  }

  "Text Node ChildDiff" should {
    "return Right(Some(TagDiff)) with STag Child" in {
      val from = div.id("A").cls("classA").css("margin" -> "10px")("test")
      val to = div.id("A").cls("classB").css("margin" -> "21px")(div("test"))
      val diffResult: Diff = diff(from, to)
      (diffResult match {
        case Right(Some(TagDiff(id, Some(List(cls)), Some(List((css))), child :: tail))) =>
          if( cls == "classB" && css == ("margin","21px") && isSTagChild(child,div("test"))) true else false
        case _ => false
      }) must beTrue

    }
  }

  "Text Node ChildDiff" should {
    "return Right(Some(TagDiff)) with 2 STag Children" in {
      val from = div.id("A").cls("classA").css("margin" -> "10px")("test","test2")
      val to = div.id("A").cls("classB").css("margin" -> "21px")(div("test"),"test3")
      val diffResult: Diff = diff(from, to)
      (diffResult match {
        case Right(Some(TagDiff(id, Some(List(cls)), Some(List((css))), child1 :: child2 :: tail))) =>
          if( cls == "classB" && css == ("margin","21px") && isSTagChild(child1,div("test")) && isSTagChild(child2,"test3")) true else false
        case _ => false
      }) must beTrue

    }
  }


  "Text Node ChildDiff" should {
    "return Right(Some(TagDiff)) with 1 TagDiff Child" in {
      val from = div.id("A").cls("classA").css("margin" -> "10px")(div.id("B")("test"))
      val to = div.id("A").cls("classB").css("margin" -> "21px")(div.id("B")("testA"))
      val diffResult: Diff = diff(from, to)
      (diffResult match {
        case Right(Some(TagDiff(id, Some(List(cls)), Some(List((css))), child1 ::  tail))) =>
          if( cls == "classB" && css == ("margin","21px") && isTagDiffChild(child1,
          TagDiff("B",None,None,List(Left("testA"))))) true else false
        case _ => false
      }) must beTrue

    }
  }





  def isSTagChild(diff:Either[STag,Option[TagDiff]], value:STag):Boolean = diff match {
    case Left(v) => v.toXML.toString == value.toXML.toString
    case Right(_) => false
    case _ =>false
  }

  def isTagDiffChild(diff:Either[STag,Option[TagDiff]], value:TagDiff):Boolean = diff match {
    case Left(v) => false
    case Right(td) => true
    case _ =>false
  }

*/


}
