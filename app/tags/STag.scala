package tags

import scala.xml._

import play.api.libs.json.Json
import play.api.libs.json.Writes
import scalaz.syntax.bifunctor._
import scalaz.syntax.std.option._
import scalaz.syntax.equal._
import scalaz.std.list._
import scalaz.syntax.semigroup._
import scalaz.syntax.monad._
import scalaz.std.either._
import tools.Json._
import tools.Logging._
import Elements._


/**
 * A general interface for all types which can appear in a ScalaTags fragment.
 * This project makes use of a fork of scalatags: https://github.com/lihaoyi/scalatags

scalatags License:
The MIT License (MIT)

Copyright (c) 2013, Li Haoyi

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
trait STag {
  /**
   * Converts an ScalaTag fragment into a `scala.xml.NodeSeq`
   */
  def toXML(): NodeSeq

  /**
   * The children of a ScalaTag node
   */
  def children: Seq[STag]

  override def toString:String = toXML.toString
}




/**
 * An algebraic data type representing a HTML tag
 *
 * @param tag The name of the tag
 * @param children The children of the tag
 * @param attrMap The tag's attributes
 * @param classes The tag's classes
 * @param styles The tag's CSS styles
 */
case class HtmlTag(tag: String = "",
                   children: Seq[STag] = Nil,
                   attrMap: Map[String, Any] = Map.empty,
                   classes: Seq[Any] = Nil,
                   styles: Map[String, Any] = Map.empty,
                   leaf:Boolean = false) extends STag with HtmlTrait {

  def apply(x1: STag*) = this.copy(children = children ++ x1)

  def apply(x:List[STag]):HtmlTag = apply(x : _*)

  def L:HtmlTag = copy(leaf = true)

  def attr(t: (String, Any)*) = {
    copy(attrMap = t.foldLeft(attrMap)(_ + _))
  }

  def toXML(): Elem = {
    val c = flattenChildren(children)
    var newAttrMap = attrMap
    if (classes != Nil) newAttrMap = newAttrMap.updated("class", attrMap.getOrElse("class", "") + classes.map(_.toString + " ").mkString)
    if (styles != Map.empty) newAttrMap = newAttrMap.updated("style", attrMap.getOrElse("style", "") + styles.map {
      case (k, v) => k + ": " + v + "; "
    }.mkString)
    newAttrMap.foldLeft(new Elem(null, tag, Null, TopScope, false, c: _*))(
      (e, k) => e % new UnprefixedAttribute(k._1, k._2.toString, Null)
    )
  }

  def findChildByIndex(index: Int): Option[STag] = children.zipWithIndex.find(_._2 == index).map(_._1)

  def flattenChildren(c: Seq[STag]) =
    c.flatMap(_.toXML())
}

object Elements {

  case class Window(main: HtmlTag, modal: Option[Modal], focus: Option[String])
  case class WindowDiff(main: Diff, modal: Either[Option[Modal], ModalDiff], focus: Option[String])
  case class Modal(width: Int, id:String, contents: HtmlTag)
  case class ModalDiff(contents: Option[Diff])

  /**
   * An STag is either an HtmlTag, or a text node (String), it represents a Leaf in the DOM ( a node without children that could be diff'd )
   * A TagDiff represents a branch in the DOM that is "managed", ie. we care about it's children wrt differences
   *
   */
  type Diff = Either[STag, Option[TagDiff]]

  /**
   * @param classesDiff     Some(_) means there is a difference, replace with _, None = no difference
   * @param cssDiff      Some(_) means there is a difference, replace with _, None = no difference
   * @param diffChildren   recurse
   */
  case class TagDiff(
                      attrDiff: Option[List[(String,String)]] = None,
                      classesDiff: Option[List[String]] = None,
                      cssDiff: Option[List[(String, String)]] = None,
                      diffChildren: List[Diff] = Nil)


  implicit lazy val WriteSTag: Writes[STag] = new Writes[STag] {
    def writes(tag: STag) = Json.toJson(tag.toXML.toString)
  }
  implicit lazy val WriteHtmlTag: Writes[HtmlTag] = new Writes[HtmlTag] {
    def writes(tag: HtmlTag) = Json.toJson(tag.toXML.toString)
  }

  implicit lazy val WindowWrites: Writes[Window] = new Writes[Window] {
    def writes(a: Window) = Json.obj("main" -> a.main, "modal" -> a.modal, "focus" -> a.focus)
  }

  implicit def seqToList(s: Option[Seq[Any]]): Option[List[String]] = s.map(_.map(_.toString).toList)

  implicit def mapToList(s: Option[Map[String, Any]]): Option[List[(String, String)]] = s.map(_.mapValues(_.toString).toList)

  implicit lazy val TagDiffWrites: Writes[TagDiff] = new Writes[TagDiff]{
    def writes(a: TagDiff) = Json.obj(
                            "a" -> a.attrDiff,
                            "c" -> a.classesDiff,
                             "s" -> a.cssDiff,
                             "ch" -> a.diffChildren  )
  }


  implicit lazy val WindowDiffWrites: Writes[WindowDiff] = new Writes[WindowDiff] {
    def writes(a: WindowDiff) = Json.obj("main" -> a.main, "modal" -> a.modal, "focus" -> a.focus)
  }

  implicit lazy val ModalWrites: Writes[Modal] = Json.writes[Modal]
  implicit lazy val ModalDiffWrites: Writes[ModalDiff] = Json.writes[ModalDiff]


  def windowDiff(from: Window, to: Window): WindowDiff =
    WindowDiff(diff(from.main, to.main), modalDiff(from.modal, to.modal), to.focus)

  def modalDiff(from: Option[Modal], to: Option[Modal]): Either[Option[Modal], ModalDiff] = Left(to) //TODO
//      from.flatMap(f => to.map(t =>
//        if (f.width != t.width) Left(to)
//        else diff(f.contents, t.contents).leftMap(m => Some(Modal(t.width,"id", m))).rightMap(m => ModalDiff(None)))).getOrElse(Left(to))

  def printChildren(stag: Seq[STag]): String = stag.foldLeft("")((a, b) => a + ", " + b.toXML().toString())


  def equalSTag(from: Seq[STag], to: Seq[STag]): Boolean = {
    val f = from.map(_.toXML.toString).toList
    val t = to.map(_.toXML.toString).toList
    f == t
  }


  def compareByIndex(fromParent: HtmlTag, toChild: STag, index: Int): Diff =
    fromParent.findChildByIndex(index).cata(oc =>
      if (oc.toXML().toString() == toChild.toXML.toString) Right(None) else Left(toChild),Left(toChild))


  def diffChildren(fromHtml: HtmlTag, toHtml: HtmlTag): List[Diff] = {
    // for each child find corresponding in fromHtml, if not there, add it, otherwise calculate diff
    toHtml.children.zipWithIndex.map(c => {
      c._1 match {
        case htmlTag @ HtmlTag(_, _, _, _, _,leaf) => {
          if(!leaf){ fromHtml.findChildByIndex(c._2).cata(ot => {
            diff(ot, htmlTag)
          }, Left(htmlTag))} else compareByIndex(fromHtml, htmlTag, c._2)
        }
        case (s) => compareByIndex(fromHtml, s, c._2)
      }
    }).toList
  }

  /**
   * Calculates diff on elements with the assumptions that they occur
   * at the same index in parent Element
   *
   * @param fromHtml
   * @return
   */
  def diff(fromHtml: STag, toHtml: STag): Diff = {
    (fromHtml,toHtml) match {
      case ( fromElement@HtmlTag(fromTag, _, fromAttr, fromClasses, fromStyles,_),
             toElement@HtmlTag(toTag, _, toAttr, toClasses, toStyles,leaf)) => {
        if (toTag == fromTag) {
          if(!leaf) {
            lazy val attrDiff = if (toAttr != fromAttr) Some(toAttr) else None
            lazy val cssDiff = if (toStyles != fromStyles) Some(toStyles) else None
            lazy val classesDiff = if (toClasses != fromClasses) Some(toClasses) else None
            lazy val childrenDiff = diffChildren(fromElement, toElement)
            lazy val hasChildDiff = childrenDiff.foldLeft(false)((acc, childDiff) => acc || (childDiff match {
              case Right(None) => false
              case _ => true
            }))
            if (attrDiff.isDefined || cssDiff.isDefined || classesDiff.isDefined || hasChildDiff ) {
              Right(Some(TagDiff(attrDiff,classesDiff, cssDiff, childrenDiff)))
            } else Right(None)
          }else Left(toTag)
        } else Left(toTag)
      }
      case _ => if (toHtml.toXML().toString == fromHtml.toXML().toString()) Right(None) else Left(toHtml)
    }
//      .logDebug("*** diff:" ++ _.toString)
  }



}
