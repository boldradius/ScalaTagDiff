package tools

import play.api.mvc.PathBindable
import play.api.libs.json.{Json => J}
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import Json._
import java.net.URLEncoder.encode
import java.net.URLDecoder.decode
import java.lang.String
import controllers.BrowserEvents._
import controllers.{BrowserEventTypes, BrowserGetEvent,BrowserPostEvent}


object PathBindables {

  implicit def getRoutePathGetEventBinder(implicit stringBinder: PathBindable[String]) = new PathBindable[BrowserGetEvent] {
    override def bind(key: String, value: String): Either[String, BrowserGetEvent] =
       stringBinder.bind(key, value).right.map(s => J.fromJson[BrowserGetEvent](J.parse(decode(s, "UTF-8"))).asEither.left.map(_.toString)).joinRight
    override def unbind(key: String, value: BrowserGetEvent): String =
     stringBinder.unbind(key, encode(J.stringify(J.toJson(value)), "UTF-8"))
  }

  implicit def getRoutePathPostEventBinder(implicit stringBinder: PathBindable[String]) = new PathBindable[BrowserPostEvent] {
    override def bind(key: String, value: String): Either[String, BrowserPostEvent] =
      stringBinder.bind(key, value).right.map(s => J.fromJson[BrowserPostEvent](J.parse(decode(s, "UTF-8"))).asEither.left.map(_.toString)).joinRight
    override def unbind(key: String, value: BrowserPostEvent): String =
      stringBinder.unbind(key, encode(J.stringify(J.toJson(value)), "UTF-8"))
  }


}