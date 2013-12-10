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
//import scalaz._
import scala.Some
import tags.Elements._
import BrowserEvents._
import render.ScalaTaggedViews._
import play.api.templates.Html
import controllers.BrowserEventTypes._
import tools.Lenses._
import UI._

object Application extends Controller {


  case class ClientSession(id: Long, ui: UISession) {
    lazy val rendering: Window = renderSession(ui)
  }


  case class Page(responseId: Long, page: Either[Window, WindowDiff])

  implicit lazy val WritePage = Json.writes[Page]

  /**
   * (sessionIdString, clientStateCounter, ClientSession => UISession) => JsValue
   */
  lazy val updateUI: (String, Long, ClientSession => UISession) => JsValue = {

    // this is evaluated only once, the first time, to create an empty map
    lazy val states: MVar[Map[String, ClientSession]] = newMVar(Map.empty[String, ClientSession]).unsafePerformIO()


    ( sessionId, clientStateId, f ) => {
      val ( newState, clientState ) = states.modify{ sessionMap =>

        // get current ClientSession for this request, create new one if necessary
        val startState = sessionMap.get(sessionId).getOrElse(ClientSession(1L, UISession()))

        // check the  clientStateId, if it is in sync, we're good, otherwise render from scratch
        val currentClientState = if (clientStateId == startState.id) Some(startState) else None

        // apply ClientSession => UISession or render from scratch
        val updatedState = ClientSession(startState.id + 1, currentClientState.fold(startState.ui)(f))

        // Side effect
        IO((sessionMap + (sessionId -> updatedState), (updatedState, currentClientState)))
      }.unsafePerformIO()

      Json.toJson(
        Page(
          newState.id,
          // If the clientState is None, this is the first request, else its a diff
          clientState.cata(prev => Right(windowDiff(prev.rendering, newState.rendering)), Left(newState.rendering))))
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


  def processBrowserGetEvent(event: Get): ClientSession => UISession = {
    clientState => event match {
      case Get(et, id) => et match {
        case LeafReplace => uiActiveTabLens.set( clientState )(TabLeafReplace).ui
        case DOMReplace => clientState.ui.copy(activeTab = DeepDomDiff)
        case About => clientState.ui.copy(activeTab = DeepDomDiff)
        case OpenModal => clientState.ui.copy(modalVisible = true)
        case CloseModal => clientState.ui.copy(modalVisible = false)
        case FirstLeaf => clientState.ui.copy(leafDemo = FirstL)
        case SecondLeaf => clientState.ui.copy(leafDemo = SecondL)
        case FirstDeepDom => clientState.ui.copy(domDemo = FirstD)
        case SecondDeepDom => clientState.ui.copy(domDemo = SecondD)

      }
    }
  }

  def processBrowserPostEvent(event: Post): ClientSession => UISession = {
    s => UISession()
  }


}