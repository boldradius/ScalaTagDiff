package tools

import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Writes
import play.api.libs.json.Reads

object Json {
  implicit def tuple2Writes[A,B](implicit a: Writes[A], b:Writes[B]) = new Writes[(A, B)] {
    def writes(t: (A, B)) = play.api.libs.json.Json.arr(t._1, t._2)
  }
  
  implicit def tuple2Reads[A,B](implicit a: Reads[A], b:Reads[B]) = new Reads[(A, B)] {
    def reads(t: JsValue) : JsResult[(A,B)]= {
      t match {
        case JsArray(Seq(x,y)) => (a.reads(x).flatMap(x2 => b.reads(y).map(y2 => (x2,y2))))
        case _ => JsError("Invalid tuple")
      }
    }
  }  
  
  implicit def unitWrites = new Writes[Unit] {
    def writes(t: Unit) = play.api.libs.json.Json.arr()
  }
  
  implicit def unitReads = new Reads[Unit] {
    def reads(t: JsValue) : JsResult[Unit]= {
      t match {
        case JsArray(Seq()) => JsSuccess(Unit)
        case _ => JsError("Invalid Unit")
      }
    }
  } 
  
  implicit def eitherFormat[A,B](implicit a: Writes[A], b:Writes[B]) = new Writes[Either[A, B]] {
    def writes(e: Either[A,B]) = play.api.libs.json.Json.obj(e.fold("left" -> _, "right" -> _))
  }

  implicit def byteFormat : Format[Byte] = new Format[Byte] {
    def writes(t: Byte) = play.api.libs.json.Json.toJson(t.toInt) 
    def reads(t: JsValue) : JsResult[Byte] = play.api.libs.json.Json.fromJson[Int](t).map(_.toByte)
  }

  // Defines Reads and Writes given a bijection
  def mapFormat[A, B](f: A => B, r: B => A)(implicit br: Reads[B], bw: Writes[B]) : Format[A] = new Format[A] {
    def writes(v: A) = bw.writes(f(v))
    def reads(j: JsValue) = br.reads(j).map(r)
  }



}