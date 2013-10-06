package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import render.RenderElements
import RenderElements._
import models._
import tools.Json._
import tools.Logging._
import scalaz.concurrent.MVar
import scalaz.concurrent.MVar._
import scalaz.effect.IO
import scalaz.syntax.std.option._
import scala.Some
import tags.Elements._
import BrowserEvents._
import render.ScalaTaggedViews._
import play.api.templates.Html
import controllers.BrowserEventTypes._

object Application extends Controller {


  case class State(id: Long, ui: UISession) {
    lazy val rendering: Window = renderSession(ui)
  }

  case class Page(responseId: Long, page: Either[Window, WindowDiff])

  implicit lazy val WritePage = Json.writes[Page]

  val updateUI: (String, Long, State => UISession) => JsValue = {
    val states: MVar[Map[String, State]] = newMVar(Map.empty[String, State]).unsafePerformIO

    (sessionId, clientStateId, f) => {
      sessionId.logDebug("sessionId " ++ _.toString)
      val (newState, clientState) = states.modify(m => {
        val startState = m.get(sessionId).getOrElse(State(1L, UISession()))
        val clientState = (if (clientStateId == startState.id) Some(startState) else None)
        val newState = (State(startState.id + 1, clientState.fold(startState.ui)(f)))
        IO((m + (sessionId -> newState), (newState, clientState)))
      }).unsafePerformIO

      val page = newState.rendering
      Json.toJson(
        Page(
          newState.id,
          clientState.cata(prev => Right(windowDiff(prev.rendering, newState.rendering)), Left(newState.rendering))))
//        .logDebug(_.toString)
    }

  }

  def index = Action {
    implicit request =>
      Ok(Html(main)).withSession("sessionKey" -> sessionId)
  }

  def browserGetEvent(event: BrowserGetEvent) = Action {
    implicit request => {
      implicit val sessionIdString = sessionId
      Ok(logTime("updateUI time: " + _.toString) {
        updateUI(sessionIdString, event.clientStateId, processBrowserGetEvent(event.event))
      })
    }
  }

  def sessionId(implicit request: Request[Any]): String =
    request.session.get("sessionKey").fold(System.currentTimeMillis().toString)(identity)


  def browserPostEvent() = Action {
    implicit request => {
      Ok(request.body.asJson.fold(Json.toJson("Not a valid request"))(
        _.validate[BrowserPostEvent].fold(
          invalid => {
            Logger.debug("error " + invalid)
            JsObject(Seq("error" -> JsString("error")))
          },
          event => {
            implicit val sessionIdString = sessionId
            logTime("updateUI PostEvent " + _.toString) {
              updateUI(sessionIdString, event.clientStateId, processBrowserPostEvent(event.event))
            }
          })
      ))
    }
  }


  def processBrowserGetEvent(event: Get): State => UISession = {
    event.logDebug("processBrowserGetEvent " ++ _.toString)
    s => event match {
      case Get(et, id) => et match {
        case LeafReplace => s.ui.copy(activeTab = TabLeafReplace)
        case DOMReplace => s.ui.copy(activeTab = DeepDomDiff)
        case About => s.ui.copy(activeTab = DeepDomDiff)
        case OpenModal => s.ui.copy(modalVisible = true)
        case CloseModal => s.ui.copy(modalVisible = false)
        case FirstLeaf => s.ui.copy(leafDemo = FirstL)
        case SecondLeaf => s.ui.copy(leafDemo = SecondL)
        case FirstDeepDom => s.ui.copy(domDemo = FirstD)
        case SecondDeepDom => s.ui.copy(domDemo = SecondD)

      }
    }
  }

  def processBrowserPostEvent(event: Post): State => UISession = {
    s => UISession()
  }


}