package controllers

import play.api.libs.json.Reads
import play.api.libs.json.Format
import play.api.libs.json._
import scalaz._
import tools.Logging._
import BrowserEventTypes._
import tools.Json._

case class BrowserGetEvent(clientStateId:Long,event:Get)
case class BrowserPostEvent(clientStateId:Long,event:Post)

case class Get(event: BrowserEventType, id: Option[Long] = None)
case class Post(event: BrowserEventType, id: Option[Long] = None, formString: Option[String] = None)

object BrowserEvents {

  implicit val browserEventTypeReads = new Reads[BrowserEventType] {
    def reads(t: JsValue): JsResult[BrowserEventType] = {
      t match {
        case JsString(s) => JsSuccess(BrowserEventTypes.withName(s))
        case _ => JsError("Invalid tuple")
      }
    }
  }
    implicit val browserEventTypeWrites = new Writes[BrowserEventType] {
    def writes(e: BrowserEventType): JsValue = {
      JsString(e.toString())
    }
  }

  implicit val getReads = Json.reads[Get]
  implicit val getWrites = Json.writes[Get]

  implicit val postReads = Json.reads[Post]
  implicit val postWrites = Json.writes[Post]

  implicit val getEventReads =  Json.reads[BrowserGetEvent]
  implicit val getEventWrites =  Json.writes[BrowserGetEvent]

  implicit val postEventReads =  Json.reads[BrowserPostEvent]
  implicit val postEventWrites =  Json.writes[BrowserPostEvent]

}